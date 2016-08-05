rtf @./topallhdg_new.pro
end

parameter @./parallhdg_new.pro
end


structure @${proteinPsf}.psf end

coor @${proteinPdb}.pdb

!delete  select (name H*) end


hbuild select=(name H*) phistep=360 end
hbuild select=(name H*) phistep=5 end

flags exclude * include bonds angle impr end !
constraint fix (not name H*) end
mini powell nstep 1000 end


write coor output=${proteinPdb}H.pdb end
write psf output=${proteinPsf}H.psf end
stop
