package org.liukan.xplorSUI;

import org.liukan.mgraph.mgraphxEx;
import org.liukan.mgraph.util.dbIO;
import org.liukan.xplorSUI.db.thePath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

/**
 * Created by liuk on 2016/6/14.
 */
public class crossLinkingUI {
    static String dbUrl = thePath.getPath()+ File.separator+"db"+File.separator+"db.sqlite";
    public static void main(String[] args)
    {
        JFrame  frame = new JFrame("DynaXL");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        Locale cl=new Locale("en", "US");
        mgraphxEx c=new mgraphxEx(cl);
        c.gpanel.setupGraphStyle(20,22,2,false);
        //c.setSize(880, 500);
        JPanel panel_button = new JPanel();
        JButton btnNewNodeButton = new JButton("ReadCross-linksMode");
        btnNewNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    dbIO dbio=new dbIO("org.sqlite.JDBC","jdbc:sqlite:"+dbUrl,null,null);
                    c.gpanel.readGfromDB(dbio,2);
                    dbio.close();
                } catch ( Exception e ) {
                    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                    //System.exit(0);
                }
            }
        });
        panel_button.add(btnNewNodeButton);

        JButton btnSaveButton = new JButton("SaveCross-linksMode");
        btnSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    dbIO dbio=new dbIO("org.sqlite.JDBC","jdbc:sqlite:"+dbUrl,null,null);
                    c.gpanel.saveG2DB("hoho",2,dbio);
                    dbio.close();
                } catch ( Exception e ) {
                    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                    //System.exit(0);
                }
            }
        });
        panel_button.add(btnSaveButton);
        JButton GenButton = new JButton("GenCross-linksConfigure");
        panel_button.add(GenButton);
        JButton ExecButton = new JButton("ExecXplor");
        panel_button.add(ExecButton);
        frame.getContentPane().add(c,BorderLayout.CENTER);
        frame.getContentPane().add(panel_button, BorderLayout.SOUTH);
        frame.getContentPane().add(new InputPanel().panel,BorderLayout.EAST);
        frame.pack();
        frame.setSize( 820,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
