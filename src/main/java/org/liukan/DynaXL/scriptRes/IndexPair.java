package org.liukan.DynaXL.scriptRes;

/**
 * Created by liuk on 2016/7/9.
 */
public class IndexPair {
    public int index;
    public int which;

    public IndexPair(int index, int which) {
        this.index = index;
        this.which = which;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getWhich() {
        return which;
    }

    public void setWhich(int which) {
        this.which = which;
    }
}
