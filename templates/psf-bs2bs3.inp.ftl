rtf @./topallhdg_new.pro @${BSt}.top end

parameter @./parallhdg_new.pro end     

segment
   name=" "
   SETUP=TRUE
   chain

coor @./${BSn}.pdb

end
end

write psf output=${BSn}.psf end

stop
