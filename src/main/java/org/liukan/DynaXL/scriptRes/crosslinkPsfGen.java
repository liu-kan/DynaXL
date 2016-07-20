package org.liukan.DynaXL.scriptRes;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.liukan.DynaXL.db.thePath;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuk on 16-7-19.
 */
public class crosslinkPsfGen {
        private Configuration cfg;
        private String tempDir=null;
        private File file;

        public String BSt,BSn;
        public String ProteinPsf;
        private String outDir;
    private String XplorPath;


    public void init(String WorkSpace,String XplorPath) throws Exception
        {
            this.XplorPath=XplorPath;
            cfg = new Configuration();
            tempDir= thePath.getPath()+File.separator+"templates";
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
        }

        public void process()throws Exception
        {
            Map<String, Object> root = new HashMap<String, Object>();

            root.put( "BSt",BSt);
            root.put( "BSn",BSn);

            Template temp = cfg.getTemplate("psf-bs2bs3.inp.ftl");
            StringWriter out = new StringWriter();
            temp.process( root, out );
            //System.out.println( );
            file = new File(outDir+"psf-bs2bs3.inp");
            FileUtils.writeStringToFile(file, out.getBuffer().toString() , "UTF-8");
            execXplor();

        }
        public void execXplor(){
            try {
                //Runtime.getRuntime().exec(XplorPath);
                ProcessBuilder pb =
                        new ProcessBuilder(XplorPath, "-in", "psf-bs2bs3.inp");
                Map<String, String> env = pb.environment();
                pb.directory(new File(outDir));
                File log = new File("log");
                pb.redirectErrorStream(true);
                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
                Process p = pb.start();
                p.waitFor(); // Wait for the process to finish.
                System.out.println("psf-bs2bs3.inp executed successfully");
            }
            catch (IOException ex) {
                System.out.println(ex);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        public static void main(String[] args)throws Exception
        {
            org.liukan.DynaXL.scriptRes.crosslinkPsfGen hf = new org.liukan.DynaXL.scriptRes.crosslinkPsfGen();
            hf.init(null,null);
            hf.process();
        }
    }

