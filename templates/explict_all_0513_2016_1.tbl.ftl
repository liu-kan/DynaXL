！who连who 连fix和grp
<#list segidAndResidAndFixOfLinks as segidAndResidAndFixOfLink>
assign (segid ${segidAndResidAndFixOfLink.getSegid()} and resi ${segidAndResidAndFixOfLink.getResid()} and name ${segidAndResidAndFixOfLink.getLinkResid()}) (segid ${segidAndResidAndFixOfLink.getSegid()} and resi ${segidAndResidAndFixOfLink.getGrpid()} and name NZ) 3.35 2.05 1.65
</#list>

