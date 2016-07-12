package org.liukan.DynaXL.io;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by liuk on 2016/7/6.
 */
public class mFiles {
    public TreeMap<String, String> linkermap;
    private String workSpacePath;
    private String proteinPath;
    private ArrayList<String> crosslinkersPath;
    public int resSize;
    private String proteinPsf;

    public mFiles(String ws){
        workSpacePath=ws;
    }
    public void copyPdbFiles(){

    }
    public void preparePdbFiles(String proteinPath,String proteinPsfPath, DefaultListModel linkersModel){
        crosslinkersPath=new ArrayList<>();
        boolean adjLinersResSeq=false;
        int s=linkersModel.getSize();
        linkermap=new TreeMap<String, String>();
        resSize=0;
        for(int i=0;i<s;i++){
            PdbWrapper pdbw=(PdbWrapper)linkersModel.getElementAt(i);
            Map<String, String> m = pdbw.getData().getResMap();
            for(String k:m.keySet()){
                resSize++;
                if(linkermap.get(k)!=null&&linkermap.get(k)!=m.get(k))
                    adjLinersResSeq=true;
            }
            linkermap.putAll(pdbw.getData().getResMap());
        }
        rwPDB ppdb=new rwPDB(proteinPath);
        int proteinMaxResSeq=ppdb.getMaxResSeq();
        for(String k:linkermap.keySet()){
            if(Integer.parseInt(linkermap.get(k).trim())<=proteinMaxResSeq)
                adjLinersResSeq=true;
        }
        if(adjLinersResSeq){
            int ii=1;
            for(int i=0;i<s;i++){
                PdbWrapper pdbw=(PdbWrapper)linkersModel.getElementAt(i);
                rwPDB lpdb=pdbw.getData();
                Map<String, String> m = lpdb.getResMap();
                for(String k:m.keySet()){
                    int newSeq=proteinMaxResSeq+(ii++);
                    lpdb.setResSeq(k,Integer.toString(newSeq));
                }
            }
        }
        int t=proteinPath.lastIndexOf(File.separator);
        String proteinp=proteinPath.substring(t+1);
        ppdb.saveFile(workSpacePath+proteinp);
        this.proteinPath=workSpacePath+proteinp;
        this.proteinPsf=proteinPsfPath;
        for(int i=0;i<s;i++){
            PdbWrapper pdbw=(PdbWrapper)linkersModel.getElementAt(i);
            rwPDB lpdb=pdbw.getData();
            Map<String, String> m = lpdb.getResMap();
            String cp=m.keySet().toArray(new String[m.size()])[0];
            lpdb.saveFile(workSpacePath+cp+".pdb");
            //TODO deal with crosslink psf
            crosslinkersPath.add(workSpacePath+cp+".pdb");
        }
    }
}
