import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.align.gui.jmol.StructureAlignmentJmol;
import org.biojava.nbio.structure.gui.BiojavaJmol;
import org.biojava.nbio.structure.io.PDBFileParser;
import org.liukan.xplorSUI.db.thePath;

import java.io.*;

/**
 * Created by liuk on 2016/7/1.
 */
public class testUI {
    public static void main(String[] args){
        try {
            String pdbDir= thePath.getPath()+ File.separator+"db"+ File.separator;
            File file =new File(pdbDir+"3eza_AH.pdb");
            InputStream inStream = null;
            try {
                inStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            PDBFileParser pdbpars = new PDBFileParser();
            Structure struc=null;
            try {
                struc = pdbpars.parsePDBFile(inStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Structure struc = StructureIO.getStructure("3eza");
            BiojavaJmol jmolPanel = new BiojavaJmol();
            jmolPanel.setStructure(struc);


            jmolPanel.setStructure(struc);

            // send some commands to Jmol
            //jmolPanel.evalString("select * ; color chain;");
            //jmolPanel.evalString("select *; spacefill off; wireframe off; cartoon on;  ");
            //jmolPanel.evalString("select ligands; cartoon off; wireframe 0.3; spacefill 0.5; color cpk;");

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
