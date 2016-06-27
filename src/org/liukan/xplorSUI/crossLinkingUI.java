package org.liukan.xplorSUI;

import org.liukan.mgraph.mgraphxEx;
import org.liukan.mgraph.util.dbIO;
import org.liukan.xplorSUI.db.dbIO2;
import org.liukan.xplorSUI.db.thePath;
import org.liukan.xplorSUI.ui.CtrlInputPanel;

import javax.swing.*;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Created by liuk on 2016/6/14.
 */
public class crossLinkingUI {
    private final dbIO2 dbio2;
    String dbUrl;
    dbIO dbio;

    public crossLinkingUI() {
        dbUrl = thePath.getPath()+ File.separator+"db"+File.separator+"db.sqlite";
        dbio=new dbIO("org.sqlite.JDBC","jdbc:sqlite:"+dbUrl,null,null);
        dbio2=new dbIO2("org.sqlite.JDBC","jdbc:sqlite:"+dbUrl,null,null);
        JFrame  frame = new JFrame("DynaXL");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        Locale cl=new Locale("en", "US","BIO");
        mgraphxEx c=new mgraphxEx(cl);
        c.gpanel.setupGraphStyle(25,32,2,false);
        JPanel p;
        p = new CtrlInputPanel(c.gpanel,dbio,dbio2).panel;
        frame.getContentPane().add(c,BorderLayout.CENTER);
        frame.getContentPane().add(p,BorderLayout.EAST);

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
                e.getWindow().dispose();
            }
        });
    }

    public static void main(String[] args)
    {
        crossLinkingUI ui=new crossLinkingUI();
    }
}
