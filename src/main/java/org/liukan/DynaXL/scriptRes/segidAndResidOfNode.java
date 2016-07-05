package org.liukan.DynaXL.scriptRes;

/**
 * Created by liuk on 2016/6/25.
 */
public class segidAndResidOfNode {
    String segid;

    public segidAndResidOfNode(String segid, String resid) {
        this.segid = segid;
        this.resid = resid;
    }

    public String getResid() {
        return resid;
    }

    public String getSegid() {
        return segid;
    }

    String resid;
}
