rtf @./topallhdg_new.pro @BS2.top @BS3.top end

parameter @./parallhdg_new.pro end
!!!!!!交联剂copy
structure @./${proteinPsf}H.psf <#list BSs as BS>@./${BS}.psf </#list>end
<#list BSs as BS>
coor @./${BS}.pdb end
</#list>

coor @./${proteinPdb}H.pdb end
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!!氨基酸 !!交联剂相同same segid
<#list segidAndResidOfNodes as segidAndResidOfNode>
vector do (segid "${segidAndResidOfNode.getSegid()}")(resid ${segidAndResidOfNode.getResid()} and not (name ca or name ha or name n or name hn or name c or name o))
</#list>
<#list segidAndResidAndDupOfNodes as segidAndResidAndDupOfNode>
duplicate sele=(segid ${segidAndResidAndDupOfNode.getSegid()} and resi ${segidAndResidAndDupOfNode.getResid()}) segid=${segidAndResidAndDupOfNode.getDupid()} end
</#list>

!!!!交联剂segid 只连fix端
<#list segidAndResidAndFixOfLinks as segidAndResidAndFixOfLink>
vector do (segid "${segidAndResidAndFixOfLink.getSegid()}")(resid ${segidAndResidAndFixOfLink.getResid()})
</#list>

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
topology
    autogenerate angles=true end
    presidue ISOP
        group
        modify atom 1C01 type=C end
        modify atom 1O11 type=O end
        delete atom 1O12        end

        group
        modify atom 2NZ  type=NH1 end
        modify atom 2HZ1 type=H   end
        delete atom 2HZ2 end
        delete atom 2HZ3 end
    add bond 1C01 2NZ
    add angle 1C02 1C01 2NZ
    add angle 1O11 1C01 2NZ
    add angle 1C01 2NZ 2CE
    add angle 1C01 2NZ 2HZ1
    add improper 1O11  1C01  2NZ  2CE                     ! planar -C    fixed for DG by JK
    add improper 2HZ1  2NZ   1C01 1C02                    ! planar +N
    add improper 1C02  1C01  2NZ  2CE                     ! planar peptide        "
    end
end
!!!!!!!!!!!!!!连接!!!!!!!!!!!!!!!!!!!!!!!
!!reference=2 为边的起点
<#list segidAndResidAndFixOfLinks as segidAndResidAndFixOfLink>
patch ISOP
    reference=1=(resi ${segidAndResidAndFixOfLink.getResid()})
    reference=2=(segid ${segidAndResidAndFixOfLink.getSegid()} and resi ${segidAndResidAndFixOfLink.getFixid()})
end
</#list>

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

write psf output=./ein_explicit.psf end

write coor output=./ein_explicit.pdb end

stop
