# -*- coding: utf-8 -*-
command = xplor.command
from jCoupPot import JCoupPot
from noePot import NOEPot

import prePot

from xplor import select

from xplorPot import XplorPot
from rdcPotTools import *
from pdbTool import *
from atomAction import *
from selectTools import *
from simulationTools import *
from ivm import IVM

import protocol
import monteCarlo

protocol.initRandomSeed(997)

protocol.initParams('./parallhdg_new.pro')

protocol.initStruct('ein_explicit.psf')
protocol.initCoords('ein_explicit.pdb')

from ensembleSimulation import EnsembleSimulation
esim=EnsembleSimulation('ens',2)
esim.setAveType('sum')

from potList import PotList
from simulationTools import StaticRamp, MultRamp
from simulationTools import InitialParams, FinalParams
from avePot import AvePot

potList = PotList()

protocol.initNBond(repel=1.2)

init_t  = 3000
final_t = 25

cool_steps = 12000

rampedParams=[]
highTempParams=[]

bond= AvePot(XplorPot,"BOND")
potList.append( bond)

angl= AvePot(XplorPot,"ANGL")
potList.append( angl)
rampedParams.append( MultRamp(0.4,1,"potList['ANGL'].setScale(VALUE)") )

impr= AvePot(XplorPot,"IMPR")
potList.append( impr )
rampedParams.append( MultRamp(0.4,1,"potList['IMPR'].setScale(VALUE)") )

nbond=AvePot(XplorPot,"VDW")
potList.append(nbond)
rampedParams.append( MultRamp(1.2,0.75,
                              "command('param nbonds repel VALUE end end')") )
rampedParams.append( MultRamp(0.004,4,
                              "command('param nbonds rcon VALUE end end')") )

from torsionDBPotTools import create_TorsionDBPot
tDB = AvePot(create_TorsionDBPot('tDB',
                                 selection=AtomSel('resi 19:24 or resi 142:148',esim.member())))
potList.append( tDB )
rampedParams.append( MultRamp(.01,1,"tDB.setScale(VALUE)") )

from noePotTools import create_NOEPot

noe = create_NOEPot('cxms',"./explict_all_0513_2016_1.tbl")
noe.setPotType("hard")
noe.setScale(2)
noe.setAveType("sum")
potList.append(noe)

rampedParams.append( MultRamp(2,30, "noe.setScale( VALUE )") )

# IVM setup
#

dyn  = IVM()

ensIndex=esim.member().memberIndex()
######################################################
if ensIndex==0:
   dyn.fix(""" segid " " """)
else:
   dyn.fix(""" segid " " and <#if dynFixs?size gt 1>(</#if>resi <#list dynFixs as dynFix>${dynFix} <#if dynFix_has_next>or resi</#if> </#list><#if dynFixs?size gt 1>)</#if> """) # allow 20 phi angle to reorient
   dyn.group(""" segid " " and <#if dynGroups?size gt 1>(</#if>resi <#list dynGroups as dynGroup>${dynGroup} <#if dynGroup_has_next>or</#if> </#list><#if dynGroups?size gt 1>)</#if> """)
   pass

# linker 20-23  and 143-147

command("""
    constraints
<#list segidAndResidAndFixOfLinks as segidAndResidAndFixOfLink>
    inter = (segid ${segidAndResidAndFixOfLink.getSegid()}) (segid " " or segid ${segidAndResidAndFixOfLink.getSegid()})
</#list>

    inter = (segid " " and <#if dynFixs?size gt 1>(</#if>resi <#list dynFixs as dynFix>${dynFix} <#if dynFix_has_next>or resi</#if> </#list><#if dynFixs?size gt 1>)</#if>) (segid " " and <#if dynGroups?size gt 1>(</#if>resi <#list dynGroups as dynGroup>${dynGroup} <#if dynGroup_has_next>or</#if> </#list><#if dynGroups?size gt 1>)</#if>) # 刚性之间
    inter = (segid " " and <#if linkRs?size gt 1>(</#if>resi <#list linkRs as linkR>${linkR} <#if linkR_has_next>or resi</#if> </#list><#if linkRs?size gt 1>)</#if>) (all)
    weights * 1 end end
    """)
############################################################
#
# common setting of groups and hinges
#
###########################################################
##
<#list breakResids as breakResid>
dyn.breakAllBondsIn("""(resi ${breakResid.getS()} and name C) or (resi ${breakResid.getE()} and name N)""")
</#list>
<#list fixIds as fixId>
dyn.breakAllBondsTo(""" resid ${fixId} and name NZ """)
</#list>

#############################################################
protocol.torsionTopology(dyn)

protocol.updatePseudoAtoms()

def structLoopAction(loopInfo):

    protocol.initMinimize(dyn, potList=potList)
    InitialParams( rampedParams )
    dyn.run()
    dyn.run()

    #
    # high temp dynamics
    #

    ini_timestep = 0.010
    potList["VDW"].setScale(0)
    protocol.initDynamics(dyn,
                          potList=potList,
                          bathTemp=init_t,
                          initVelocities=True,
                          stepsize=ini_timestep,
                          finalTime=10,
                          printInterval=100)
    dyn.run()

    #
    # cooling
    #

    timestep=ini_timestep
    potList["VDW"].setScale(1)

    protocol.initDynamics(dyn,
                          potList=potList,
                          bathTemp=init_t,
                          initVelocities=True,
                          stepsize=timestep,
                          finalTime=0.5,
                          printInterval=100)

    dyn.setResetCMInterval( 100 )

    AnnealIVM(initTemp =init_t,
              finalTemp=final_t,
              numSteps = 50,
              ivm=dyn,
              rampedParams = rampedParams).run()
    #
    # final Powell minimization
    #
    protocol.initMinimize(dyn)
    dyn.run()

    loopInfo.writeStructure(potList)

    pass

StructureLoop(numStructures=256,
              pdbTemplate='./EINOpt6x_STRUCTURE.pdb',
              averagePotList=potList,
              genViolationStats=1,
              structLoopAction=structLoopAction).run()
