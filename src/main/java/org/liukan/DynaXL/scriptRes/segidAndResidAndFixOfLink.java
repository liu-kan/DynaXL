package org.liukan.DynaXL.scriptRes;

/**
 * Created by liuk on 2016/6/25.
 */
public class segidAndResidAndFixOfLink {
    String segid;
    String resid;
    String fixid;
    String grpid;
    String linkResid;
    public segidAndResidAndFixOfLink(String segid, String resid, String fixid,String grpid,String linkResid) {
        this.segid = segid;
        this.resid = resid;
        this. fixid= fixid;
        this.grpid=grpid;
        this.linkResid=linkResid;
    }

    public String getResid() {
        return resid;
    }
    public String getFixid() {
        return  fixid;
    }
    public String getSegid() {
        return segid;
    }
    public String getGrpid() {
        return grpid;
    }
    public String getLinkResid() {
        return linkResid;
    }

}
