package org.liukan.DynaXL.scriptRes;

/**
 * Created by liuk on 2016/6/24.
 */
//import freemarker.cache.*; // template loaders live in this package

//import java.nio.charset.Charset;
import java.util.*;
import java.io.*;
import freemarker.template.*;

import org.apache.commons.io.FileUtils;
import org.liukan.DynaXL.db.thePath;

import javax.swing.*;

@SuppressWarnings("deprecation")
public class scriptRes
{
    private Configuration cfg;
    private String tempDir=null;
    private File file;
    public ArrayList<segidAndResidOfNode> segidAndResidOfNodes;
    public ArrayList<segidAndResidAndDupOfNode> segidAndResidAndDupOfNodes;
    public ArrayList<segidAndResidAndFixOfLink> segidAndResidAndFixOfLinks;
    public ArrayList<String> linkFileNames;
    public String ProteinPdb;
    public String ProteinPsf;
    private String outDir;
    public ArrayList<String> dynFixs;
    public ArrayList<String> dynGroups;
    public ArrayList<String> linkRs;
    public ArrayList<breakResid> breakResids;
    public ArrayList<Integer> fixId;

    public void init(String WorkSpace) throws Exception
    {
        cfg = new Configuration();
        tempDir=thePath.getPath()+File.separator+"templates";
        if(WorkSpace!=null)
            outDir=WorkSpace;
        else
            outDir=thePath.getPath()+File.separator+"out"+File.separator;
        cfg.setDirectoryForTemplateLoading(new File(tempDir));
        //FileTemplateLoader ftl1= new FileTemplateLoader(new File(tempDir));
        //MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[] { ftl1});
        //cfg.setTemplateLoader(mtl);
        cfg.setObjectWrapper( new DefaultObjectWrapper() );
        //cfg.setClassForTemplateLoading( scriptRes.class, "/templates" );

        segidAndResidOfNodes=new ArrayList<segidAndResidOfNode>();
        segidAndResidAndDupOfNodes=new ArrayList<segidAndResidAndDupOfNode>();
        segidAndResidAndFixOfLinks=new ArrayList<segidAndResidAndFixOfLink>();
        linkFileNames=new ArrayList<String>();
        dynFixs=new ArrayList<String>();
        dynGroups=new ArrayList<String>();
        linkRs=new ArrayList<String>();
        breakResids=new ArrayList<breakResid>();
        fixId=new ArrayList<Integer>();
    }

    public void process()throws Exception
    {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "segidAndResidOfNodes", segidAndResidOfNodes );
        root.put( "segidAndResidAndDupOfNodes",segidAndResidAndDupOfNodes);
        root.put( "segidAndResidAndFixOfLinks",segidAndResidAndFixOfLinks);
        root.put( "BSs",linkFileNames);
        root.put( "dynFixs",dynFixs);
        root.put( "dynGroups",dynGroups);
        root.put( "linkRs",linkRs);
        root.put("breakResids",breakResids);
        root.put("proteinPdb",ProteinPdb);
        root.put("proteinPsf",ProteinPsf);
        root.put("fixIds",fixId);
        Template temp = cfg.getTemplate("isop_patch3.inp.ftl");
        StringWriter out = new StringWriter();
        temp.process( root, out );
        //System.out.println( );
        file = new File(outDir+"isop_patch3.inp");
        FileUtils.writeStringToFile(file, out.getBuffer().toString() , "UTF-8");
        temp = cfg.getTemplate("explict_all_0513_2016_1.tbl.ftl");
        out = new StringWriter();
        temp.process( root, out );
        //System.out.println( );
        file = new File(outDir+"explict_all_0513_2016_1.tbl");
        FileUtils.writeStringToFile(file, out.getBuffer().toString() , "UTF-8");
        temp = cfg.getTemplate("EIN0_explicit_optimize2dx.py.ftl");
        out = new StringWriter();
        temp.process( root, out );
        //System.out.println( );
        file = new File(outDir+"EIN0_explicit_optimize2dx.py");
        FileUtils.writeStringToFile(file, out.getBuffer().toString() , "UTF-8");
        JOptionPane.showMessageDialog(null,"Scripts have been generated in\n"+outDir);
    }
    public static void main(String[] args)throws Exception
    {
        scriptRes hf = new scriptRes();
        hf.init(null);
        hf.process();
    }
}
