import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created by liuk on 16-8-8.
 */
public class twoText {
    public static void main(String[] args) {

        // Text Panes initialization. I am just setting text so you can see
// what each pane is.
        JTextPane leftDiffPane = new JTextPane();
        leftDiffPane.setText("left");
        JTextPane rightDiffPane = new JTextPane();
        rightDiffPane.setText("right");

        // Wrap the Text Panes with a Panel since you can only
// have a single component within a scroll pane.
        JPanel bothDiffsPanel = new JPanel();
        bothDiffsPanel.setLayout(new GridLayout(1, 2));
        JScrollPane spLeft = new JScrollPane(leftDiffPane);
        JScrollPane spRight = new JScrollPane(rightDiffPane);
        bothDiffsPanel.add(spLeft, BorderLayout.WEST);
        bothDiffsPanel.add(spRight, BorderLayout.EAST);

        // Fill in the frame with both of the panels.
        JFrame f=new JFrame();
        //f.getContentPane().add(bothDiffsPanel);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(bothDiffsPanel, BorderLayout.CENTER);

        //spLeft.setWheelScrollingEnabled(false);
        spLeft.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                //spRight.dispatchEvent(e);
                Adjustable adjL = spLeft.getVerticalScrollBar();
                Adjustable adjR = spRight.getVerticalScrollBar();
                if(adjL!=null) {
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
                if(adjR!=null) {
                    int scroll = e.getUnitsToScroll() * adjR.getBlockIncrement();
                    if (adjL != null)
                        adjL.setValue(adjR.getValue() + scroll);
                    if (adjR != null)
                        adjR.setValue(adjR.getValue() + scroll);
                }
            }
        });
        f.pack();
        f.setVisible(true);
    }
}
