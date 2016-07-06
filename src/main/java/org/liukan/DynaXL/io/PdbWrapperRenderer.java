package org.liukan.DynaXL.io;

import org.liukan.DynaXL.ui.TestListModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liuk on 16-7-6.
 */

public class PdbWrapperRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean hasFocus) {
        if (value instanceof PdbWrapper) {
            PdbWrapper item=((PdbWrapper) value);
            String s=item.getName()+", #Atom: "+item.getData().getMaxResSeq();
            return super.getListCellRendererComponent(
                    list, s, index,
                    isSelected, hasFocus);
        }
        return super.getListCellRendererComponent(list, value, index,
                isSelected, hasFocus);
    }
}