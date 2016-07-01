import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureTools;
import org.biojava.nbio.structure.io.PDBFileParser;
import org.liukan.xplorSUI.db.thePath;

import java.io.*;
import java.util.List;


/**
 * Created by liuk on 2016/7/1.
 */
public class test {
    public static void main(String[] args){
        String pdbDir= thePath.getPath()+ File.separator+"db"+ File.separator;
        File file =new File(pdbDir+"BS2_84.pdb");
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PDBFileParser pdbpars = new PDBFileParser();
        Structure structure=null;
        try {
            structure = pdbpars.parsePDBFile(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*Chain chain = null;
        if (structure != null) {
            chain = structure.getChainByIndex(0);
        }*/
        Atom[] caSa = StructureTools.getRepresentativeAtomArray(structure);

        System.out.println(caSa.length);

    }
}
