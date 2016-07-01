package org.liukan.xplorSUI.scriptRes;

/**
 * Created by liuk on 2016/6/25.
 */
public class segIDandNode{
    public String segID;

    public String getNode() {
        return node;
    }

    public String getSegID() {
        return segID;
    }
    public String node;
    public segIDandNode(String segID,String node){
        this.segID=segID;this.node=node;
    }
}
