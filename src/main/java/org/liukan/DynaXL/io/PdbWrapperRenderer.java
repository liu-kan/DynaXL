package org.liukan.DynaXL.io;

import org.liukan.DynaXL.ui.TestListModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liuk on 16-7-6.
 */

public class PdbWrapperRenderer extends DefaultListCellRenderer {
    private final int width;
    public static final String HTML_1 = "<html><body style='width: ";
    public static final String HTML_2 = "px'>";
    public static final String HTML_3 = "</html>";

    public PdbWrapperRenderer(int width) {
        this.width = width;
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean hasFocus) {
        if (value instanceof PdbWrapper) {
            PdbWrapper item=((PdbWrapper) value);
            String s=item.getName()+", #Atom: "+item.getData().getMaxResSeq();
            String text = HTML_1 + String.valueOf(width) + HTML_2 + s
                    + HTML_3;
            return super.getListCellRendererComponent(
                    list, text, index,
                    isSelected, hasFocus);
        }
        return super.getListCellRendererComponent(list, value, index,
                isSelected, hasFocus);
    }
}