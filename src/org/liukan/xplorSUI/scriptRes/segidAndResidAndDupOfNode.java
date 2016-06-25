package org.liukan.xplorSUI.scriptRes;

/**
 * Created by liuk on 2016/6/25.
 */
public class segidAndResidAndDupOfNode {
    String segid;
    String resid;
    String dupid;
    public segidAndResidAndDupOfNode(String segid, String resid,String dupid) {
        this.segid = segid;
        this.resid = resid;
        this.dupid= dupid;
    }

    public String getResid() {
        return resid;
    }
    public String getDupid() {
        return dupid;
    }
    public String getSegid() {
        return segid;
    }


}
