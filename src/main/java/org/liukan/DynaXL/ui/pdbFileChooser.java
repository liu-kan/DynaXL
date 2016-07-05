package org.liukan.DynaXL.ui;

import org.biojava.nbio.structure.align.gui.jmol.JmolPanel;
import org.jmol.api.JmolSimpleViewer;
import org.liukan.DynaXL.db.thePath;
import org.liukan.DynaXL.io.rwPDB;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;

public class pdbFileChooser extends JDialog {
    private final JScrollPane scroll;
    private String pdbDir;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel fileCandP;

    private JTextArea textPDB;
    private JmolPanel jmolPanel;
    private JFileChooser fileChooser;
    private JmolSimpleViewer viewer;
    private String previewPath;
    private String choosedPath;

    public String getPdbPath() {
        return choosedPath;
    }

    private void intPreviewer() {
        if (pdbDir == null)
            pdbDir = thePath.getPath() + File.separator + "db" + File.separator;
  /*      File file = new File(pdbDir + "3eza_AH.pdb");
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AtomCache cache = new AtomCache();

        cache.setPath(pdbDir);


        StructureIO.setAtomCache(cache);
        StructureIO.setPdbPath(pdbDir);

        //PDBFileParser pdbpars = new PDBFileParser();
        PDBFileReader pdbreader = new PDBFileReader();

       // pdbreader.setAutoFetch(true);
        pdbreader.setPath(pdbDir);
        Structure struc = null;
        try {
            struc = pdbreader.getStructure(pdbDir + "3eza_AH.pdb");
            //struc = pdbpars.parsePDBFile(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Structure struc = StructureIO.getStructure("3eza");

*/

        jmolPanel = new JmolPanel();
        //jmolPanel.setStructure(struc);
        // send some RASMOL style commands to Jmol
        viewer = jmolPanel.getViewer();

        // Jmol could also read the file directly from your file system`
        //viewer.openFile(pdbDir + "3eza_AH.pdb");
        jmolPanel.evalString("select * ; color chain;");
        jmolPanel.evalString("select *; spacefill off; wireframe off; backbone 0.4;  ");
        jmolPanel.evalString("save STATE state_1");

        //jmolPanel.setSize(300, 300);
        jmolPanel.setPreferredSize(new Dimension(500, 500));
        fileCandP.add(jmolPanel, BorderLayout.EAST);
        fileChooser = new JFileChooser(pdbDir);
        fileChooser.setControlButtonsAreShown(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "PDB File", "pdb");
        fileChooser.setFileFilter(filter);
        fileChooser.setPreferredSize(new Dimension(400, 500));
        fileCandP.add(fileChooser, BorderLayout.WEST);
        textPDB.setPreferredSize(new Dimension(400, 500));
        fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                    JFileChooser chooser = (JFileChooser) evt.getSource();
                    //File oldFile = (File) evt.getOldValue();
                    //File newFile = (File) evt.getNewValue();
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        previewPath = chooser.getSelectedFile().getAbsolutePath();
                        preview();
                    }

                }
            }
        });

    }

    private void preview() {
        if (viewer != null && previewPath != null) {
            if (previewPath.endsWith(".pdb")) {
                rwPDB pdb = new rwPDB(previewPath);
                textPDB.setText("");
                textPDB.setRows(pdb.pdbLines.size());
                for (String s : pdb.pdbLines) {
                    // Append each string from ArrayList to the end of text in JTextArea
                    // separated by newline
                    textPDB.append(s + System.getProperty("line.separator"));
                    //System.out.println(s);
                }


                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        scroll.getVerticalScrollBar().setValue(0);
                        viewer.openFile(previewPath);
                    }
                });


            }
        }

    }

    public pdbFileChooser(String pdbDir) {
        this.pdbDir = pdbDir;
        setContentPane(contentPane);
        setModal(true);
        choosedPath = null;
        getRootPane().setDefaultButton(buttonOK);
        //fileCandP.setLayout(new GridLayout(1, 2));
        viewer = null;
        previewPath = null;
        textPDB = new JTextArea();
        textPDB.setLineWrap(true);
        textPDB.setEditable(false);
        textPDB.setVisible(true);

        scroll = new JScrollPane(textPDB);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        fileCandP.add(scroll, BorderLayout.CENTER);
        intPreviewer();
        setSize(800, 500);
        pack();
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
    }

    public void showCenter() {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - getWidth()) / 2;
        final int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        setVisible(true);
    }

    private void onOK() {
        choosedPath = previewPath;
        dispose();
    }

    private void onCancel() {
        choosedPath = null;
        dispose();
    }

    public static void main(String[] args) {
        pdbFileChooser dialog = new pdbFileChooser(null);

        dialog.showCenter();
        System.exit(0);
    }

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
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileCandP = new JPanel();
        fileCandP.setLayout(new BorderLayout(0, 0));
        contentPane.add(fileCandP, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(600, 400), new Dimension(1350, 500), null, 1, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
