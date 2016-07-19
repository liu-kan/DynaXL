package org.liukan.DynaXL.io;

import org.liukan.DynaXL.db.thePath;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import static java.nio.file.StandardCopyOption.*;

/**
 * Created by liuk on 2016/7/6.
 */
public class mFiles {
    public TreeMap<String, String> linkermap;
    private String workSpacePath;
    //private String proteinPath;
    private ArrayList<String> crosslinkersPath;
    public int resSize;
    public String proteinPsf;
    public String proteinPdb;

    public mFiles(String ws){
        workSpacePath=ws;
    }
    public void copyProFiles(){
        copyFile(thePath.getPath()+File.separator+"db"+File.separator+"topallhdg_new.pro",workSpacePath+File.separator+"topallhdg_new.pro");
        copyFile(thePath.getPath()+File.separator+"db"+File.separator+"parallhdg_new.pro",workSpacePath+File.separator+"parallhdg_new.pro");
        copyFile(thePath.getPath()+File.separator+"db"+File.separator+"BS2.top",workSpacePath+File.separator+"BS2.top");
        copyFile(thePath.getPath()+File.separator+"db"+File.separator+"BS3.top",workSpacePath+File.separator+"BS3.top");
    }
    private boolean copyFile(String s,String t){
        Path sp=Paths.get(s);
        Path tp=Paths.get(t);
        CopyOption[] options =
                new CopyOption[] { REPLACE_EXISTING };
        try {
            Files.copy(sp, tp, options);
        } catch (IOException x) {
            System.err.format("Unable to copy: %s: %s%n", sp, x);
            return false;
        }
        return true;
    }
    public static boolean fileCanExec(String p){
        Path path = Paths.get(p);
        try {
            path=path.toRealPath();
            File f=path.toFile();
            if(f.exists()){
                if(f.canExecute())
                    return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

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
        t=proteinPsfPath.lastIndexOf(File.separator);
        String proteinpsf=proteinPsfPath.substring(t+1);
        Path sP=Paths.get(proteinPsfPath);
        Path toP=Paths.get(workSpacePath+proteinpsf);
        CopyOption[] options =
                new CopyOption[] { REPLACE_EXISTING };
        try {
                Files.copy(sP, toP, options);
            } catch (IOException x) {
                System.err.format("Unable to copy: %s: %s%n", sP, x);
        }
        this.proteinPdb=proteinp;
        this.proteinPsf=proteinpsf;
        for(int i=0;i<s;i++){
            PdbWrapper pdbw=(PdbWrapper)linkersModel.getElementAt(i);
            rwPDB lpdb=pdbw.getData();
            Map<String, String> m = lpdb.getResMap();
            String cp=m.keySet().toArray(new String[m.size()])[0];
            lpdb.saveFile(workSpacePath+cp+".pdb");
            //TODO deal with crosslink psf
            crosslinkersPath.add(workSpacePath+cp+".pdb");
        }
        copyProFiles();
    }
}
