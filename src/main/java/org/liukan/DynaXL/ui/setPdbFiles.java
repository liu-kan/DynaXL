package org.liukan.DynaXL.ui;

import org.liukan.DynaXL.io.PdbWrapper;
import org.liukan.DynaXL.io.PdbWrapperRenderer;
import org.liukan.DynaXL.io.rwPDB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class setPdbFiles extends JDialog {
    private setPdbFiles me;
    public DefaultListModel<PdbWrapper> linkersModel;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList<PdbWrapper> listCrossliners;
    private JButton btnAddCL;
    private JButton delCrosslinkerButton;
    private JTextArea textAreaProtein;
    private JButton chooseProteinPdbButton;
    private String ws, tws;
    public boolean ok;
    public String proteinPdbPath;

    public setPdbFiles(String ws) {
        setContentPane(contentPane);
        setModal(true);
        ok = false;
        proteinPdbPath = null;
        this.ws = ws;
        this.tws = ws;
        me = this;
        delCrosslinkerButton.setEnabled(false);
        linkersModel = new DefaultListModel<PdbWrapper>();
        listCrossliners.setModel(linkersModel);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Setup pdf files for crosslinker and protein");
        listCrossliners.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listCrossliners.setCellRenderer(new PdbWrapperRenderer(120));
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

        btnAddCL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pdbFileChooser pdbfc = new pdbFileChooser(tws);
                pdbfc.showCenter();
                String p = pdbfc.choosedPath;
                rwPDB pdb = new rwPDB(p);
                pdb.getRes();
                int s = linkersModel.getSize();
                //System.out.println(s);
                for (int i = 0; i < s; i++) {
                    PdbWrapper pdbw = (PdbWrapper) linkersModel.getElementAt(i);
                    if (pdbw.getName().equals(p)) {
                        JOptionPane.showMessageDialog(me, "This PDB has added already!");
                        return;
                    }
                }
                if (pdbfc.ok && p != null) {
                    tws = p.substring(0, p.lastIndexOf(File.separator) + 1);
                    System.out.println(tws);
                    linkersModel.addElement(new PdbWrapper(p, pdb));
                    delCrosslinkerButton.setEnabled(true);
                }
            }
        });
        delCrosslinkerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int sel = listCrossliners.getSelectedIndex();
                linkersModel.removeElementAt(sel);
                if (linkersModel.getSize() < 1)
                    delCrosslinkerButton.setEnabled(false);
            }
        });

        chooseProteinPdbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pdbFileChooser pdbfc = new pdbFileChooser(tws);
                pdbfc.showCenter();
                String p = pdbfc.choosedPath;
                if (pdbfc.ok && p != null) {
                    tws = p.substring(0, p.lastIndexOf(File.separator) + 1);
                    System.out.println(tws);
                    textAreaProtein.setText(p);
                }
            }
        });
    }

    private void onOK() {
        if (linkersModel.getSize() < 1 || textAreaProtein.getText().length() < 2) {
            JOptionPane.showMessageDialog(me, "Please set crosslinkers and protein pdb first! or Press Cancel");
            return;
        }
        proteinPdbPath = textAreaProtein.getText();
        ok = true;
        dispose();
    }

    private void onCancel() {
        proteinPdbPath = null;
        linkersModel.clear();
        ok = false;
        dispose();
    }

    public void showCenter() {
        pack();
        //setPreferredSize(new Dimension(420, 300));
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - getWidth()) / 2;
        final int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        setVisible(true);
    }

    public static void main(String[] args) {
        setPdbFiles dialog = new setPdbFiles(null);
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
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 200), new Dimension(450, 260), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(330, 100), new Dimension(350, -1), null, 0, false));
        listCrossliners = new JList();
        panel4.add(listCrossliners, BorderLayout.CENTER);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, BorderLayout.SOUTH);
        btnAddCL = new JButton();
        btnAddCL.setText("Add CrossLiner pdb");
        panel5.add(btnAddCL, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delCrosslinkerButton = new JButton();
        delCrosslinkerButton.setText("Del Crosslinker");
        panel5.add(delCrosslinkerButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel3.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(180, -1), new Dimension(180, -1), null, 0, false));
        textAreaProtein = new JTextArea();
        textAreaProtein.setLineWrap(true);
        panel6.add(textAreaProtein, BorderLayout.CENTER);
        chooseProteinPdbButton = new JButton();
        chooseProteinPdbButton.setText("Choose protein pdb");
        panel6.add(chooseProteinPdbButton, BorderLayout.SOUTH);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
