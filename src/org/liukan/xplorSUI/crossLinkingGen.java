package org.liukan.xplorSUI;

import org.liukan.mgraph.graphStru;
import org.liukan.mgraph.medge;
import org.liukan.mgraph.mnode;

import java.util.*;

/**
 * Created by liuk on 2016/6/14.
 */
public class crossLinkingGen {
    private final graphStru gs;
    private  String segidPrefix;


    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }
    public crossLinkingGen(graphStru gs) {
        segidPrefix="XL";
        procedNodes=new HashMap<mnode,String>();
        this.gs=gs;
        segID=new TreeMap<String,String>();
        segID2=new TreeMap<String,String>();
    }
    public void genSegid(){
        int size = gs.nodes.size();
        for (int j = 0; j < size; j++) {
            mnode n = gs.nodes.get(j);
            if (n.id.length() < 1)
                continue;
            String sid=assignSegid(n);
            System.out.println(sid+","+n.label);
        }
        System.out.println("===###="+segID);

        size = gs.edges.size();
        for (int j = 0; j < size; j++) {
            medge e = gs.edges.get(j);
            if (e.id.length() < 1)
                continue;
            //System.out.println(dupSegid(gs.getNode(e.source),gs.getNode(e.target))+","+gs.getNode(e.target).label);
            dupSegid(gs.getNode(e.source),gs.getNode(e.target));
        }

    }

    private String dupSegid(mnode source, mnode target) {
        String ssid=procedNodes.get(source);
        String tsid=procedNodes.get(target);
        if(ssid==null||tsid==null)
            return null;
        if(ssid.equals(tsid))
            return null;
        Set<String> keys=getKeysByValue(segID2,ssid);
        if(keys.size()>1) {
            //for (String nodeid : keys)
            {
                //System.out.println("=2=");
                System.out.println(segID2.get(source.id)+","+source.label+","+genNewDupid(source.id,target.id));
                System.out.println(segID2.get(target.id)+","+target.label+","+genNewDupid(source.id,target.id));
            }
        }else
            System.out.println(segID2.get(target.id)+","+target.label+","+ssid);
        return ssid;
    }

    /**
     * (n,segid)
     */
    private Map<mnode,String> procedNodes;
    /**
     * Map<String,String> segID<n.id,segid>
     */
    private Map<String,String> segID;
    private Map<String,String> segID2;

    private String assignSegid(mnode n) {
        LinkedList<mnode> cn=getConnectedNodes(n);//获取相连接的点
        String segid=null;
        if(cn.size()>1) {
            segid = findSegid(cn);//已处理点中 是否有点连接点 如果有返回相连接点sid
        }
        if(segid==null)
            segid=genNewSegid(n);
        procedNodes.put(n,segid);
        segID2.put(n.id,segid);
        return segid;
    }

    private LinkedList<mnode> getConnectedNodes(mnode n) {
        LinkedList<mnode> rv=new LinkedList<mnode>();
        int size = gs.edges.size();
        for (int j = 0; j < size; j++) {
            medge e=gs.edges.get(j);
            if(e.source.length()<1||e.target.length()<1)
                continue;
            if(n.id.equals(e.source))
                rv.add(gs.getNode(e.target));
            else if(n.id.equals(e.target))
                rv.add(gs.getNode(e.source));
        }
        return rv;
    }

    private String findSegid(LinkedList<mnode> cn) {
        int size = cn.size();
        String segid=null;
        for (int j = 0; j < size; j++) {
            mnode n = cn.get(j);
            segid = segID.get(n.id);
            if (segid != null)
                break;
        }
        return segid;
    }
    private String genNewDupid(String s,String t) {
        String segid;
        segid=s+segidPrefix+t;
        return segid;
    }
    private String genNewSegid(mnode n) {
        String segid;
        segid=segidPrefix+n.id;
        segID.put(n.id,segid);
        return segid;
    }


}
