package org.liukan.DynaXL.ui;

import org.liukan.DynaXL.db.thePath;
import org.liukan.DynaXL.io.PdbWrapper;
import org.liukan.DynaXL.io.PdbWrapperRenderer;
import org.liukan.DynaXL.io.rwPDB;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

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
    private JRadioButton semiAutomaticallyGenrateTheRadioButton;
    private JRadioButton chooseAPSFFileRadioButton;
    private JPanel autoPanel;
    private JPanel chooseP;
    private JTextArea textAreaAutoPSF;
    private JButton semiAutomaticallyGenrateTheButton;
    private JTextArea textAreaChooPSF;
    private JButton chooseAPSFFileButton;
    private JPanel cardPanel;
    private JPanel radioP;
    private JTextArea textAreaPSF;
    private JButton chooseProteinPsfButton;
    private String ws, tws;
    public boolean ok;
    public String proteinPdbPath;
    public String proteinPsfPath;
    private CardLayout cl;

    public setPdbFiles(String ws) {
        $$$setupUI$$$();
        createUICards();
        setContentPane(contentPane);
        setModal(true);

        ok = false;
        proteinPdbPath = null;
        this.ws = ws;
        this.tws = ws;
        me = this;
        delCrosslinkerButton.setEnabled(false);
        btnAddCL.setEnabled(false);
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
                //pdbFileChooser pdbfc = new pdbFileChooser(ws);
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
                    chooseAPSFFileButton.setEnabled(true);
                    textAreaChooPSF.setEnabled(true);
                    textAreaAutoPSF.setEnabled(true);
                    semiAutomaticallyGenrateTheButton.setEnabled(true);
                }
            }
        });

        semiAutomaticallyGenrateTheRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cl.show(cardPanel, "auto");
            }
        });
        chooseAPSFFileRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cl.show(cardPanel, "choose");
            }
        });
    }

    private void onOK() {
        if (linkersModel.getSize() < 1 || textAreaProtein.getText().length() < 2) {
            JOptionPane.showMessageDialog(me, "Please set crosslinkers and protein pdb first! or Press Cancel");
            return;
        }
        boolean t = (textAreaAutoPSF.getText().length() > 2 && semiAutomaticallyGenrateTheRadioButton.isSelected())
                || (chooseAPSFFileRadioButton.isSelected() && textAreaChooPSF.getText().length() > 2);
        if (!t) {
            JOptionPane.showMessageDialog(me, "Please set protein psf! or Press Cancel");
            return;
        }
        proteinPdbPath = textAreaProtein.getText();
        if (chooseAPSFFileRadioButton.isSelected() && textAreaChooPSF.getText().length() > 2)
            proteinPsfPath = textAreaChooPSF.getText();
        else
            proteinPsfPath = textAreaAutoPSF.getText();
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
        loadDefaultPDB(new String[]{"BS3", "BS2"});
        setPreferredSize(new Dimension(670, 300));
        pack();
        //setPreferredSize(new Dimension(320, 400));
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - getWidth()) / 2;
        final int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        setVisible(true);

    }

    public boolean loadDefaultPDB(String[] pdbp) {
        for (int fi = 0; fi < pdbp.length; fi++) {

            if (pdbp[fi] == null)
                return false;
            else if (pdbp[fi].length() < 1)
                return false;

            String p = thePath.getPath() + File.separator + "db" + File.separator + pdbp[fi] + ".pdb";
            //System.out.println(p + "loading###################");
            rwPDB pdb = new rwPDB(p);
            if (!pdb.getRes())
                return false;
            //System.out.println(p + "loaded###################");
            int s = linkersModel.getSize();
            //System.out.println(s);
            for (int i = 0; i < s; i++) {
                PdbWrapper pdbw = (PdbWrapper) linkersModel.getElementAt(i);
                if (pdbw.getName().equals(p)) {
                    System.out.println(p + " __ This PDB has added already!");
                }
            }
            linkersModel.addElement(new PdbWrapper(p, pdb));
            //delCrosslinkerButton.setEnabled(true);

        }
        return true;
    }

    public static void main(String[] args) {
        setPdbFiles dialog = new setPdbFiles(null);
        dialog.showCenter();
        System.exit(0);
    }

    private void createUICards() {
        cl = new CardLayout();
        cardPanel = new JPanel(cl);

        radioP.add(cardPanel, BorderLayout.CENTER);
        autoPanel = new JPanel();
        autoPanel.setLayout(new BorderLayout(0, 0));
        cardPanel.add(autoPanel, "auto");
        textAreaAutoPSF = new JTextArea();
        textAreaAutoPSF.setLineWrap(true);
        textAreaAutoPSF.setEditable(false);
        autoPanel.add(textAreaAutoPSF, BorderLayout.CENTER);
        semiAutomaticallyGenrateTheButton = new JButton();
        semiAutomaticallyGenrateTheButton.setEnabled(false);
        semiAutomaticallyGenrateTheButton.setText("<html>Semi-automatically genrate the PSF</html>");
        autoPanel.add(semiAutomaticallyGenrateTheButton, BorderLayout.SOUTH);
        chooseP = new JPanel();
        chooseP.setLayout(new BorderLayout(0, 0));
        cardPanel.add(chooseP, "choose");
        textAreaChooPSF = new JTextArea();
        textAreaChooPSF.setLineWrap(true);
        textAreaAutoPSF.setEditable(false);
        chooseP.add(textAreaChooPSF, BorderLayout.CENTER);
        chooseAPSFFileButton = new JButton();
        chooseAPSFFileButton.setText("Choose a PSF file");
        chooseAPSFFileButton.setEnabled(false);
        chooseP.add(chooseAPSFFileButton, BorderLayout.SOUTH);
        chooseAPSFFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "PSF files", "psf");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(me);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    System.out.println("You chose to open this file: " +
                            chooser.getSelectedFile().getName());
                    String p = chooser.getSelectedFile().getAbsolutePath();
                    tws = p.substring(0, p.lastIndexOf(File.separator) + 1);
                    System.out.println(tws);
                    textAreaChooPSF.setText(p);
                }
            }
        });
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
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        panel6.add(panel7, BorderLayout.NORTH);
        textAreaProtein = new JTextArea();
        textAreaProtein.setEditable(false);
        textAreaProtein.setLineWrap(true);
        textAreaProtein.setRows(4);
        panel7.add(textAreaProtein, BorderLayout.CENTER);
        chooseProteinPdbButton = new JButton();
        chooseProteinPdbButton.setText("Choose protein pdb");
        panel7.add(chooseProteinPdbButton, BorderLayout.SOUTH);
        radioP = new JPanel();
        radioP.setLayout(new BorderLayout(0, 0));
        panel6.add(radioP, BorderLayout.CENTER);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        radioP.add(panel8, BorderLayout.NORTH);
        semiAutomaticallyGenrateTheRadioButton = new JRadioButton();
        semiAutomaticallyGenrateTheRadioButton.setSelected(true);
        semiAutomaticallyGenrateTheRadioButton.setText("<html>Semi-automatically genrate the PSF</html>");
        panel8.add(semiAutomaticallyGenrateTheRadioButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(220, -1), new Dimension(260, -1), new Dimension(300, -1), 0, false));
        chooseAPSFFileRadioButton = new JRadioButton();
        chooseAPSFFileRadioButton.setText("Choose a PSF file");
        panel8.add(chooseAPSFFileRadioButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(semiAutomaticallyGenrateTheRadioButton);
        buttonGroup.add(chooseAPSFFileRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
