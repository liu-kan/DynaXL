import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.*;

public class Main {
    public static void main(String[] argv) {

        JFileChooser chooser = new JFileChooser();

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                    JFileChooser chooser = (JFileChooser) evt.getSource();
                    File oldFile = (File) evt.getOldValue();
                    File newFile = (File) evt.getNewValue();
                    System.out.println("======");
                    System.out.println(oldFile);
                    System.out.println(newFile);
                    System.out.println("+++++++");
                    System.out.println(chooser.getSelectedFile());
                }
            }
        });
        JFrame f=new JFrame();
        f.getContentPane().add(chooser);
        f.pack();
        f.setVisible(true);

    }
}
