package org.liukan.DynaXL.ui;

import net.miginfocom.swing.MigLayout;
import org.liukan.DynaXL.db.thePath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by liuk on 2016/7/5.
 */
public class setWorkDir extends JDialog {
    private String pdbDir=null;
    private JTextField dir;
    private JTextField xplorpath;
    private JButton chooseWorkSpace;
    private JButton setXplor;
    private setWorkDir me=null;
    private String xlporPath=null;
    private boolean isok;

    public void showCenter(){
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - me.getWidth()) / 2;
        final int y = (screenSize.height - me.getHeight()) / 2;
        me.setLocation(x, y);
        me.setVisible(true);
    }
    public boolean ok(){
        return isok;
    }
    public setWorkDir(String wsp,String xp){
        super();
        me=this;
        pdbDir=wsp;
        xlporPath=xp;
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Setup parameters of the App");
        if(pdbDir==null)
            pdbDir = thePath.getPath() + File.separator + "db" + File.separator;
        if(!pdbDir.endsWith(File.separator))
            pdbDir=pdbDir+File.separator;
        JPanel panel = new JPanel(new MigLayout());
        isok=false;
        panel.add(new JLabel("WorkSpace Dir: "));
        dir=new JTextField(pdbDir);
        panel.add(dir,"width 200:400:500,grow");
        chooseWorkSpace=new JButton("Choose WorkSpace Dir...");
        panel.add(chooseWorkSpace,"wrap");
        panel.add(new JLabel("Xplor path: "));
        xplorpath=new JTextField(xlporPath);
        xplorpath.setToolTipText("Input it in the text field or find xplor path by pressing right button");
        panel.add(xplorpath,"grow");
        setXplor=new JButton("Set Xplor path...");
        setXplor.setToolTipText("Find xplor path or input it in the left text field");
        setXplor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc=new JFileChooser(pdbDir);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if(fc.showDialog(me,"Setup Xplor path")==JFileChooser.APPROVE_OPTION){
                    xlporPath=fc.getSelectedFile().getAbsolutePath();
                    xplorpath.setText( xlporPath);
                }
            }
        });
        chooseWorkSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc=new JFileChooser(pdbDir);
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if(fc.showDialog(me,"Setup WorkSpace Dir")==JFileChooser.APPROVE_OPTION){
                    pdbDir=fc.getSelectedFile().getAbsolutePath() ;
                    dir.setText( pdbDir);
                }
            }
        });
        panel.add(setXplor,"grow,wrap");
        JButton ok=new JButton("OK");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isok=true;
                dispose();
            }
        });
        panel.add(ok,"split 2,cell 2 2, align right");
        JButton cancel=new JButton("Cancel");
        panel.add(cancel,"align right");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isok=false;
                dispose();
            }
        });

        //panel.setPreferredSize(new Dimension(400,250));
        setLayout(new BorderLayout());
        add(panel,BorderLayout.CENTER);
        pack();
    }
    public String getWorkSpaceDir(){
        if(isok) {
            if(!pdbDir.endsWith(File.separator))
                pdbDir=pdbDir+File.separator;
            return pdbDir;
        }
        else
            return null;
    }
    public String getXlporPath(){
        if(isok)
            return xlporPath;
        else
            return null;
    }
    public static void main(String[] args){
        setWorkDir ws=new setWorkDir("sdsa",null);
        //ws.setPreferredSize(new Dimension(400,280));
        ws.showCenter();

    }
}
