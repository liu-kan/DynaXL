package org.liukan.xplorSUI;

import javax.swing.*;

/**
 * Created by liuk on 2016/6/21.
 */
public class InputPanel {
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    public JPanel panel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("InputPanel");
        frame.setContentPane(new InputPanel().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
