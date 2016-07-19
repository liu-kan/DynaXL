package org.liukan.DynaXL.ui;

/**
 * Created by liuk on 16-7-19.
 */

import java.awt.Dialog;
        import java.awt.Frame;
        import java.awt.Window;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.awt.event.ComponentAdapter;
        import java.awt.event.ComponentEvent;

        import javax.swing.JButton;
        import javax.swing.JDialog;
        import javax.swing.JLabel;
        import javax.swing.JOptionPane;
        import javax.swing.JPanel;
        import javax.swing.JProgressBar;

/**
 * 锟斤拷锟斤拷锟斤拷锟斤拷锟皆筹拷锟斤拷
 *
 * @author ygh
 *
 */
public class xProgressBar implements ActionListener {

    private static final String DEFAULT_STATUS = "Please Waiting";
    private JDialog dialog;
    private JProgressBar progressBar;
    private JLabel lbStatus;
    private JButton btnCancel;
    private Window parent;
    private Thread thread; //
    private String statusInfo;
    private String resultInfo;
    private String cancelInfo;

    /**
     * @param parent
     * @param thread
     */
    public static void show(Window parent, Thread thread) {
        new xProgressBar(parent, thread, DEFAULT_STATUS, null, null);
    }

    /**
     * @param parent
     * @param thread
     * @param statusInfo
     */
    public static void show(Window parent, Thread thread, String statusInfo) {
        new xProgressBar(parent, thread, statusInfo, null, null);
    }

    /**
     * @param parent
     * @param thread
     * @param statusInfo
     * @param resultInfo
     * @param cancelInfo
     */
    public static void show(Window parent, Thread thread, String statusInfo,
                            String resultInfo, String cancelInfo) {
        new xProgressBar(parent, thread, statusInfo, resultInfo, cancelInfo);
    }

    /**
     * @param parent
     * @param thread
     * @param statusInfo
     * @param resultInfo
     * @param cancelInfo
     */
    private xProgressBar(Window parent, Thread thread, String statusInfo,
                         String resultInfo, String cancelInfo) {
        this.parent = parent;
        this.thread = thread;
        this.statusInfo = statusInfo;
        this.resultInfo = resultInfo;
        this.cancelInfo = cancelInfo;
        initUI();
        startThread();
        dialog.setVisible(true);
    }

    /**
     *
     */
    private void initUI() {
        if (parent instanceof Dialog) {
            dialog = new JDialog((Dialog) parent, true);
        } else if (parent instanceof Frame) {
            dialog = new JDialog((Frame) parent, true);
        } else {
            dialog = new JDialog((Frame) null, true);
        }

        final JPanel mainPane = new JPanel(null);
        progressBar = new JProgressBar();
        lbStatus = new JLabel("" + statusInfo);
        btnCancel = new JButton("Cancel");
        progressBar.setIndeterminate(true);
        btnCancel.addActionListener(this);

        mainPane.add(progressBar);
        mainPane.add(lbStatus);
        mainPane.add(btnCancel);

        dialog.getContentPane().add(mainPane);
        dialog.setUndecorated(true); //
        dialog.setResizable(true);
        //dialog.pack();
        dialog.setSize(390, 150);
        dialog.setLocationRelativeTo(parent); //

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //

        mainPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                layout(mainPane.getWidth(), mainPane.getHeight());
            }
        });
    }

    /**
     *
     */
    private void startThread() {
        new Thread() {
            public void run() {
                try {
                    thread.start(); //
                    //
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //
                    dialog.dispose();

                    if (resultInfo != null && !resultInfo.trim().equals("")) {
                        String title = "Info";
                        JOptionPane.showMessageDialog(parent, resultInfo);
                        //title,JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }.start();
    }

    /**
     * @param width
     * @param height
     */
    private void layout(int width, int height) {
        progressBar.setBounds(20, 20, 350, 15);
        lbStatus.setBounds(20, 50, 350, 25);
        btnCancel.setBounds(width - 120, height - 31, 100, 21);
    }

    @SuppressWarnings("deprecation")
    public void actionPerformed(ActionEvent e) {
        resultInfo = cancelInfo;
        thread.stop();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //
        Thread thread = new Thread() {
            public void run() {
                int index = 0;

                while (index < 3) {
                    try {
                        sleep(1000);
                        System.out.println(++index);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //
        xProgressBar.show((Frame) null, thread, "Status", "Result", "Cancel");

    }
}