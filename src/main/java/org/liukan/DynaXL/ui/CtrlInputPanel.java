package org.liukan.DynaXL.ui;

import freemarker.core.StringArraySequence;
import org.liukan.DynaXL.db.thePath;
import org.liukan.DynaXL.io.PdbWrapper;
import org.liukan.DynaXL.io.mFiles;
import org.liukan.DynaXL.io.rwPDB;
import org.liukan.mgraph.mgraphx;
import org.liukan.mgraph.mgraphxEx;
import org.liukan.mgraph.util.dbIO;
import org.liukan.DynaXL.crossLinkingGen;
import org.liukan.DynaXL.db.dbIO2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by liuk on 2016/6/22.
 */
public class CtrlInputPanel {
    private final dbIO dbio;
    private final dbIO2 dbio2;
    private editEdgeDB editEdgeDlg;

    private JButton a7ExecuteInXplorButton;
    private JButton generateScriptButton;
    private JButton saveCrossLinksModeButton;
    private mgraphxEx mg;
    private ArrayList<String> domainDef;
    private String WorkSpaceDir = null;
    private String XplorPath;
    private setPdbFiles setpdbfiles;
    private TreeMap<String, String> linkermap;
    private mFiles mf;
    private int proteinMaxResId;

    public CtrlInputPanel(mgraphxEx _mg, dbIO _dbio, dbIO2 _dbio2, editEdgeDB ee) {
        mg = _mg;
        dbio = _dbio;
        proteinMaxResId = -1;
        this.editEdgeDlg = ee;
        mf = null;
        dbio2 = _dbio2;
        modeTab.setSelectedIndex(1);
        domainDef = new ArrayList<>();
        WorkSpaceDir = dbio2.readVar("workSpaceDir");
        mg.setEnabled(false);
        if (WorkSpaceDir == null) {
            WorkSpaceDir = thePath.getPath() + File.separator + "workSpace" + File.separator;
        }
        XplorPath = dbio2.readVar("xplorPath");
        readCrossLinksModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int gid = 2;
                try {

                    mg.gpanel.readGfromDB(dbio, gid);

                } catch (Exception e2) {
                    System.err.println(e2.getClass().getName() + ": " + e2.getMessage());
                    //System.exit(0);
                }
                ArrayList<String> p = dbio2.readCrossLink(gid);
                textRigid.setText(p.get(0));
                textFlex.setText(p.get(1));
            }
        });
        saveCrossLinksModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int gid = -1;
                try {
                    gid = mg.gpanel.saveG2DB("hoho", 2, dbio);

                } catch (Exception e2) {
                    System.err.println(e2.getClass().getName() + ": " + e2.getMessage());
                    //System.exit(0);
                    return;
                }
                saveCrossLink(gid);
            }
        });
        generateScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numS = JOptionPane.showInputDialog("How many alternative STRUCTUREs do you want to generate?");
                int num = 32;
                try {
                    num = Integer.parseInt(numS);
                    if (num < 1 || num > 1024) {
                        JOptionPane.showMessageDialog(null, "Please input a Integer between 1 and 1024");
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Please input a Integer between 1 and 1024");
                    return;
                }
                if (mf == null)
                    JOptionPane.showMessageDialog(panel, "Please follow the index order of buttons");
                domainDef.clear();
                String s = textRigid.getText().trim();
                String s1 = textFlex.getText().trim();
                if (s.length() < 1 || s1.length() < 1) {
                    JOptionPane.showMessageDialog(null, "Please input info about Domains and Links!");
                    return;
                }
                domainDef.add(s);
                domainDef.add(s1);
                crossLinkingGen gen = new crossLinkingGen(mg.gpanel.saveG2graphStru("", 0), domainDef, WorkSpaceDir);
                gen.proteinMaxID = proteinMaxResId;
                gen.setXplor(XplorPath);
                gen.setLinkersMap(mf.linkermap);

                gen.setPdbAndPsfOfProtein(mf.proteinPdb, mf.proteinPsf);
                gen.setNumPDB(num);
                gen.genSricpt();
            }
        });
        a1ChooseAWorkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //WorkSpaceDir = dbio2.readVar("workSpaceDir");
                if (WorkSpaceDir == null)
                    WorkSpaceDir = thePath.getPath() + File.separator + "workSpace" + File.separator;
                //XplorPath = dbio2.readVar("xplorPath");
                if (XplorPath == null)
                    XplorPath = "/usr/local/bin";
                setWorkDir sws = new setWorkDir(WorkSpaceDir, XplorPath);
                sws.showCenter();
                if (sws.ok()) {
                    WorkSpaceDir = sws.getWorkSpaceDir();
                    XplorPath = sws.getXlporPath();
                    dbio2.writeVar("workSpaceDir", WorkSpaceDir);
                    dbio2.writeVar("xplorPath", XplorPath);
                }
            }
        });
        a2ChoosePDBFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setpdbfiles = new setPdbFiles(WorkSpaceDir);

                setpdbfiles.showCenter();

                if (setpdbfiles.ok) {
                    Path path = FileSystems.getDefault().getPath("log");
                    //delete if exists
                    try {
                        boolean success = Files.deleteIfExists(path);
                        System.out.println("Delete status: " + success);
                    } catch (IOException | SecurityException ex) {
                        System.err.println(ex);
                    }
                    mf = new mFiles(WorkSpaceDir, XplorPath);
                    proteinMaxResId = mf.preparePdbFiles(setpdbfiles.proteinPdbPath, setpdbfiles.proteinPsfPath, setpdbfiles.linkersModel);
                    ArrayList<String> sl = new ArrayList<String>();
                    int size = setpdbfiles.linkersModel.getSize();
                    for (int i = 0; i < size; i++) {
                        Map<String, String> m = setpdbfiles.linkersModel.getElementAt(i).getData().getResMap();
                        sl.addAll(m.keySet());

                    }
                    editEdgeDlg.setLinkersName(sl);
                    mg.setEnabled(true);
                }
            }
        });
        a7ExecuteInXplorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Thread thread = new Thread() {
                    public void run() {
                        try {
                            //Runtime.getRuntime().exec(XplorPath);
                            ProcessBuilder pb =
                                    new ProcessBuilder(XplorPath + File.separator + "xplor",
                                            "-in", "isop_patch3.inp");
                            Map<String, String> env = pb.environment();
                            pb.directory(new File(WorkSpaceDir));
                            File log = new File("log");
                            //pb.redirectErrorStream(true);
                            //pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
                            Process p = pb.start();
                            p.waitFor(); // Wait for the process to finish.
                            System.out.println("isop_patch3 executed successfully");
System.out.println("\"" + XplorPath + File.separator + "xplor -smp " + Integer.toString(Runtime.getRuntime().availableProcessors()) +
        " -py EIN0_explicit_optimize2dx.py\"");
                            pb = new ProcessBuilder("/bin/bash", "-c", "\"" + XplorPath + File.separator + "xplor -smp " + Integer.toString(Runtime.getRuntime().availableProcessors()) +
                                    " -py EIN0_explicit_optimize2dx.py\"");

                            pb.directory(new File(WorkSpaceDir));
                            pb.redirectErrorStream(true);
                            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
                            p = pb.start();
                            p.waitFor(); //
                            System.out.println("PY executed successfully");
                        } catch (IOException ex) {
                            System.out.println(ex);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.setPriority(10);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        xProgressBar.show((Frame) null, thread,
                                "Calculating", "Calculated successfully!", "Cancel");
                    }
                });
            }
        });
    }

    private void saveCrossLink(int gid) {
        dbio2.saveCrossLink(gid, textRigid.getText().trim(), textFlex.getText().trim());
    }

    private JButton readCrossLinksModeButton;
    public JPanel panel;
    private JTabbedPane modeTab;
    private JTextField textRigid;
    private JTextField textFlex;
    private JPanel expMode;
    private JPanel quickMode;
    private JTextField textBoundary;
    private JButton a2ChoosePDBFilesButton;
    private JButton a1ChooseAWorkButton;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(10, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("<html>DynaXL generates and executes<br>\nthe script for rigid-body ensemble <br>\nrefinement against cross-links<br>\n identified with high confidence</html>");
        panel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        a7ExecuteInXplorButton = new JButton();
        a7ExecuteInXplorButton.setText("7. Execute in Xplor");
        panel.add(a7ExecuteInXplorButton, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateScriptButton = new JButton();
        generateScriptButton.setText("6. Generate script");
        panel.add(generateScriptButton, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveCrossLinksModeButton = new JButton();
        saveCrossLinksModeButton.setText("Save Cross-links");
        panel.add(saveCrossLinksModeButton, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        readCrossLinksModeButton = new JButton();
        readCrossLinksModeButton.setText("Read Cross-links");
        panel.add(readCrossLinksModeButton, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modeTab = new JTabbedPane();
        modeTab.setEnabled(true);
        panel.add(modeTab, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        quickMode = new JPanel();
        quickMode.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        quickMode.setEnabled(false);
        modeTab.addTab("Quick Mode", quickMode);
        final JLabel label2 = new JLabel();
        label2.setEnabled(false);
        label2.setText("<html>Domain boundaries define like:<br>\n21,22#145,146</html>");
        quickMode.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textBoundary = new JTextField();
        textBoundary.setEnabled(false);
        quickMode.add(textBoundary, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        quickMode.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        quickMode.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        expMode = new JPanel();
        expMode.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        modeTab.addTab("Expert Mode", expMode);
        final JLabel label3 = new JLabel();
        label3.setText("<html>\n4. Domains define like:<br>\n1:19,148:250#24:142<br>\nDomain 1 and Domain 2 are<br>\nseparated by #\n</html>");
        expMode.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textRigid = new JTextField();
        expMode.add(textRigid, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("<html>\n5. Linkers define like:<br>\n20:23,143:147\n</html>");
        expMode.add(label4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFlex = new JTextField();
        expMode.add(textFlex, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        a2ChoosePDBFilesButton = new JButton();
        a2ChoosePDBFilesButton.setText("2. Choose PDB files");
        panel.add(a2ChoosePDBFilesButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        a1ChooseAWorkButton = new JButton();
        a1ChooseAWorkButton.setText("[1]. Choose a work directory");
        panel.add(a1ChooseAWorkButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("<html>3. Draw the relationship diagram of<br> cross link\n</html>");
        panel.add(label5, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
