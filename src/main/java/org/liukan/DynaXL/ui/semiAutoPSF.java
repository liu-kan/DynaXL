package org.liukan.DynaXL.ui;

import org.apache.commons.io.FileUtils;
import org.liukan.DynaXL.io.rwPDB;
import org.liukan.DynaXL.scriptRes.xPsfGen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class semiAutoPSF extends JDialog {
    private String workSpacePath;
    public boolean ok;
    private String xplorPath;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel mainPanel;
    private String proteinPath;
    private String proteinPdb;
    private String proteinPsf;
    private JTextArea rightDiffPane;
    private JTextArea leftDiffPane;
    public String psfPath;

    public semiAutoPSF(String workSpacePath, String xplorPath, String pdbPath) {
        proteinPath = pdbPath;
        ok = false;
        this.xplorPath = xplorPath;
        this.workSpacePath = workSpacePath;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        textDiffViewer();
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
    }

    private void textDiffViewer() {
        leftDiffPane = new JTextArea();
        leftDiffPane.setFont(new Font("monospaced", Font.PLAIN, 12));
        rightDiffPane = new JTextArea();
        rightDiffPane.setFont(new Font("monospaced", Font.PLAIN, 12));

        // Wrap the Text Panes with a Panel since you can only
// have a single component within a scroll pane.
        JPanel bothDiffsPanel = new JPanel();
        bothDiffsPanel.setLayout(new GridLayout(1, 2));
        JScrollPane spLeft = new JScrollPane(leftDiffPane);
        JScrollPane spRight = new JScrollPane(rightDiffPane);
        JPanel JL = new JPanel(new BorderLayout());
        JL.add(spLeft, BorderLayout.CENTER);
        JL.add(new JLabel("the PDB file"), BorderLayout.NORTH);
        JPanel JR = new JPanel(new BorderLayout());
        JR.add(spRight, BorderLayout.CENTER);
        JR.add(new JLabel("the PSF file"), BorderLayout.NORTH);

        bothDiffsPanel.add(JL, BorderLayout.WEST);
        bothDiffsPanel.add(JR, BorderLayout.EAST);

        mainPanel.add(bothDiffsPanel, BorderLayout.CENTER);

        //spLeft.setWheelScrollingEnabled(false);
        spLeft.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                //spRight.dispatchEvent(e);
                Adjustable adjL = spLeft.getVerticalScrollBar();
                Adjustable adjR = spRight.getVerticalScrollBar();
                if (adjL != null) {
                    int scroll = e.getUnitsToScroll() * adjL.getBlockIncrement();
                    if (adjL != null)
                        adjL.setValue(adjL.getValue() + scroll);
                    if (adjR != null)
                        adjR.setValue(adjL.getValue() + scroll);
                }
            }
        });
        spRight.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                //spLeft.dispatchEvent(e);
                Adjustable adjL = spLeft.getVerticalScrollBar();
                Adjustable adjR = spRight.getVerticalScrollBar();
                if (adjR != null) {
                    int scroll = e.getUnitsToScroll() * adjR.getBlockIncrement();
                    if (adjL != null)
                        adjL.setValue(adjR.getValue() + scroll);
                    if (adjR != null)
                        adjR.setValue(adjR.getValue() + scroll);
                }
            }
        });

        if (this.proteinPath.toLowerCase().endsWith(".pdb")) {
            rwPDB pdb = new rwPDB(proteinPath);
            leftDiffPane.setText("");
            leftDiffPane.setRows(pdb.pdbLines.size());
            for (String s : pdb.pdbLines) {
                // Append each string from ArrayList to the end of text in JTextArea
                // separated by newline
                leftDiffPane.append(s + System.getProperty("line.separator"));
                //System.out.println(s);
            }
            pdb2psf();
            rightDiffPane.setText("");

            List<String> psfLines = null;
            try {
                psfLines = FileUtils.readLines(new File(workSpacePath + File.separator + proteinPsf));
            } catch (IOException e) {
                e.printStackTrace();
            }

            rightDiffPane.setRows(psfLines.size());
            for (String ss : psfLines) {
                // Append each string from ArrayList to the end of text in JTextArea
                // separated by newline
                rightDiffPane.append(ss + System.getProperty("line.separator"));
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    spLeft.getVerticalScrollBar().setValue(0);

                    spRight.getVerticalScrollBar().setValue(0);
                }
            });


        }
    }

    private void pdb2psf() {
        int t = proteinPath.lastIndexOf(File.separator);
        String proteinp = proteinPath.substring(t + 1);

        this.proteinPdb = proteinp;

        proteinPsf = proteinp.substring(0, proteinp.toLowerCase().lastIndexOf(".pdb")) + ".psf";
        xPsfGen psfgen = new xPsfGen();
        try {
            psfgen.init(workSpacePath, xplorPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        psfgen.execPdb2Psf(proteinPath, workSpacePath + File.separator + proteinPsf);
    }

    private void onOK() {
        try {
            psfPath = workSpacePath + File.separator + proteinPsf;
            FileWriter fwPsf = new FileWriter(workSpacePath + File.separator + proteinPsf, true);
            rightDiffPane.write(fwPsf);
            FileWriter fwPdb = new FileWriter(workSpacePath + File.separator + proteinPdb, true);
            leftDiffPane.write(fwPdb);
            ok = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void showCenter() {
        setPreferredSize(new Dimension(1024, 600));
        pack();
        //setPreferredSize(new Dimension(320, 400));
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - getWidth()) / 2;
        final int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        setVisible(true);

    }

    public static void main(String[] args) {
        semiAutoPSF dialog = new semiAutoPSF("/home/liuk/wp", "/home/liuk/bin", "/home/liuk/proj/DynaXL/db/3eza_AH.pdb");
        dialog.setPreferredSize(new Dimension(1024, 600));
        dialog.pack();
        dialog.setVisible(true);
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        contentPane.add(mainPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Files editor and reviewer");
        mainPanel.add(label1, BorderLayout.NORTH);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
