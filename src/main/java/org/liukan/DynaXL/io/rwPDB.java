package org.liukan.DynaXL.io;

import org.liukan.DynaXL.db.thePath;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by liuk on 2016/7/2.
 */
public class rwPDB {
    private boolean falseLoad;
    private String path=null;

    public int getMaxResSeq() {
        if(maxResSeq==0)
            getRes();
        return maxResSeq;
    }

    int maxResSeq;
    public ArrayList<String> pdbLines;
    public Map<String, String> resMap;
    public rwPDB(String path){
        maxResSeq=0;
        falseLoad=false;
        this.path=path;
        pdbLines=new ArrayList<>();
        resMap = new TreeMap<String, String>();
        String line;
        try {
            InputStream fis = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("ASCII"));
            BufferedReader br = new BufferedReader(isr);

            while ((line = br.readLine()) != null) {

                    pdbLines.add(line);
            }
        }catch (Exception e){
            falseLoad=true;
            System.out.println(e);
        }
    }

    public boolean getRes(){
        if(falseLoad)
            return false;
        for(String line:pdbLines){
            if(line.startsWith("ATOM ")) {
                String rsq=getResSeq(line);
                resMap.put(getResName(line), rsq);
                int irsq=Integer.parseInt(rsq.trim());
                if(maxResSeq<irsq){
                    maxResSeq=irsq;
                }
            }
        }
        return true;
    }
    public void setResSeq(String resName,String resSeq){
        int size=pdbLines.size();
        for(int i=0;i<size;i++){
            String line=pdbLines.get(i);
            if(line.startsWith("ATOM ")||line.startsWith("TER   ")) {
                if (getResName(line).equals(resName)) {
                    line = line.substring(0, 22) + String.format("%4s", resSeq) + line.substring(26);
                    //System.out.println(line);
                    pdbLines.set(i, line);
                    resMap.put(resName, resSeq);
                }
            }
        }

    }
    String getResName(String line){
        return line.substring(17,20);
    }

    String getResSeq(String line){
        return line.substring(22,26);
    }
    public boolean saveFile(){
        return saveFile(path);
    }
    public boolean saveFile(String pdbpath){

        try {
            FileWriter writer = new FileWriter(pdbpath);
            for(String str: pdbLines) {
                writer.write(str+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public String getFirstResName(){
        if(resMap.size()<1)
            getRes();
        return resMap.keySet().toArray(new String[resMap.size()])[0];
    }
    public Map<String, String> getResMap(){
        if(resMap.size()<1)
            getRes();
        return resMap;
    }
    public static void main(String[] args){
        String pdbDir= thePath.getPath()+ File.separator+"db"+ File.separator;
        //File file =new File(pdbDir+"3eza_AH.pdb");
        rwPDB pdb = new rwPDB(pdbDir + "BS2.pdb");
        pdb.getRes();
        System.out.print(pdb.resMap);
        pdb.setResSeq("BS2","608");
        System.out.print(pdb.resMap);
        System.out.println(pdb.maxResSeq);
        pdb.saveFile();
    }

}
