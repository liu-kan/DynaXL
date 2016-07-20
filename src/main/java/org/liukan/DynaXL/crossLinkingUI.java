package org.liukan.DynaXL;

import org.liukan.DynaXL.io.ResourceUtil;
import org.liukan.mgraph.mgraphxEx;
import org.liukan.mgraph.util.dbIO;
import org.liukan.DynaXL.db.dbIO2;
import org.liukan.DynaXL.db.thePath;
import org.liukan.DynaXL.ui.CtrlInputPanel;
import org.liukan.DynaXL.ui.editEdgeDB;

import javax.swing.*;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;

/**
 * Created by liuk on 2016/6/14.
 */
public class crossLinkingUI {
    private final dbIO2 dbio2;
    private final String dbUrl2;
    String dbUrl;
    dbIO dbio;

    public crossLinkingUI() {
        dbUrl = thePath.getPath()+ File.separator+"db"+File.separator+"db.sqlite";
        dbUrl2 = thePath.getPath()+ File.separator+"db"+File.separator+"db2.sqlite";
        dbio=new dbIO("org.sqlite.JDBC","jdbc:sqlite:"+dbUrl,null,null);
        dbio2=new dbIO2("org.sqlite.JDBC","jdbc:sqlite:"+dbUrl2,null,null);
        JFrame  frame = new JFrame("DynaXL");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        Locale cl=new Locale("en", "US","BIO");
        editEdgeDB ee=new editEdgeDB();
        mgraphxEx c=new mgraphxEx(cl,ee);
        c.gpanel.setupGraphStyle(25,32,2,false);
        JPanel p;
        p = new CtrlInputPanel(c,dbio,dbio2,ee).panel;
        frame.getContentPane().add(c,BorderLayout.CENTER);
        frame.getContentPane().add(p,BorderLayout.EAST);
        frame.setIconImages(ResourceUtil.getIconImages());
        frame.pack();
        frame.setSize( 820,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closing");
                dbio.close();
                dbio2.close();
                e.getWindow().dispose();
            }
        });
    }

    public static void main(String[] args)
    {
        crossLinkingUI ui=new crossLinkingUI();
    }
}
