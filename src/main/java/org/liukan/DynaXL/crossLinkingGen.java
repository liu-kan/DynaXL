package org.liukan.DynaXL;

import org.liukan.mgraph.graphStru;
import org.liukan.mgraph.medge;
import org.liukan.mgraph.mnode;
import org.liukan.DynaXL.scriptRes.*;

import javax.swing.*;
import java.util.*;

/**
 * Created by liuk on 2016/6/14.
 */
public class crossLinkingGen {
    private final graphStru gs;
    private final scriptRes scripts;
    private  String segidPrefix;
    /**
     * Map<mnode,String> procedNodes(node,segid)
     */
    private Map<mnode,String> procedNodes;
    /**
     * Map<String,String> linkResid<edge.id,linkResid>
     */
    private Map<String,String> linkResid;
    /**
     * Map<String,String> segID<edge.id,segid>
     */
    private Map<String,String> segID;
    /**
     * Map<String,String> segID2<node.id,segid>
     */
    private Map<String,String> segID2;
    /**
     * segIDofLinks<String,String> segIDofLinks<e.label,segid>
     */
    private Map<String,String> segIDofLinks;
    /**
     * segIDofLinks<String,String> segIDofLinks0<e.label,segid>
     */
    private ArrayList<String> domainDef;
    private Map<String,String> segIDofLinks0;
    private String WorkSpace;
    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }
    public crossLinkingGen(graphStru gs, ArrayList<String> domainDef, String workSpaceDir) {
        WorkSpace=workSpaceDir;
        segidPrefix="XL";
        procedNodes=new HashMap<mnode,String>();
        this.gs=gs;
        this.domainDef=domainDef;
        segID=new TreeMap<String,String>();
        segID2=new TreeMap<String,String>();
        linkResid=new TreeMap<String,String>();
        segIDofLinks=new TreeMap<>();
        segIDofLinks0=new TreeMap<>();
        scripts = new scriptRes();
        try {
            scripts.init(WorkSpace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void genSegid(){
        int size = gs.nodes.size();
        for (int j = 0; j < size; j++) {
            mnode n = gs.nodes.get(j);
            if (n.id.length() < 1)
                continue;
            String sid=assignSegid(n);
            scripts.segidAndResidOfNodes.add(new segidAndResidOfNode(sid,n.label));
            System.out.println(sid+","+n.label);
        }
        System.out.println("===###="+segID);

        size = gs.edges.size();
        for (int j = 0; j < size; j++) {
            medge e = gs.edges.get(j);
            if (e.id.length() < 1)
                continue;
            //System.out.println(dupSegid(gs.getNode(e.source),gs.getNode(e.target))+","+gs.getNode(e.target).label);
            dupSegid(e);
            scripts.linkFileNames.add(genLinkFileNames(e.label,e.id)) ;
            genSegIDofLinks(e);
        }


    }
    private String genSegIDofLinks(medge e){
        String rv=segIDofLinks0.get(e.label);
        if(rv==null){
            rv="C"+e.id;
            segIDofLinks0.put(e.label,rv);
        }
        return rv;
    }
    private void crossLinkResid() {
        int size = gs.edges.size();
        String rv;
        for (int j = 0; j < size; j++) {
            medge e = gs.edges.get(j);
            if (e.id.length() < 1)
                continue;
            rv=genNewLinkResid(e.id);
            System.out.println(segID.get(e.id)+","+rv+","+gs.getNode(e.source).label);
            scripts.segidAndResidAndFixOfLinks.add(new segidAndResidAndFixOfLink(segID.get(e.id),rv,
                    gs.getNode(e.source).label,gs.getNode(e.target).label,segIDofLinks0.get(e.label)));
        }

    }

    private String dupSegid(medge e) {
        mnode source=gs.getNode(e.source);
        mnode target=gs.getNode(e.target);
        String ssid=procedNodes.get(source);
        String tsid=procedNodes.get(target);
        String rv=null;
        if(ssid==null||tsid==null)
            return null;
        if(ssid.equals(tsid))
            return null;
        Set<String> keysST=getKeysByValue(segID2,ssid);
        Set<String> keysTS=getKeysByValue(segID2,tsid);
        if(keysST.size()>1&&keysTS.size()>1) {
            //for (String nodeid : keys)
            {
                //System.out.println("=2=");
                rv=genNewSegid(e.id);
                scripts.segidAndResidAndDupOfNodes.add(new segidAndResidAndDupOfNode(segID2.get(source.id),source.label,rv));
                scripts.segidAndResidAndDupOfNodes.add(new segidAndResidAndDupOfNode(segID2.get(target.id),target.label,rv));
                System.out.println(segID2.get(source.id)+","+source.label+","+rv);
                System.out.println(segID2.get(target.id)+","+target.label+","+rv);
            }
        }else if(keysST.size()==1){
            scripts.segidAndResidAndDupOfNodes.add(new segidAndResidAndDupOfNode(segID2.get(target.id),target.label,ssid));
            System.out.println(segID2.get(target.id) + "," + target.label + "," + ssid);
        }else if(keysTS.size()==1){
            scripts.segidAndResidAndDupOfNodes.add(new segidAndResidAndDupOfNode(segID2.get(source.id),source.label,tsid));
            System.out.println(segID2.get(source.id) + "," + source.label + "," + tsid);
        }
        return rv;
    }


    private String genNewLinkResid(String linkid){
        String rv=Integer.toString(500+Integer.parseInt(linkid));
        linkResid.put(linkid,rv);
        return  rv;
    }
    private String assignSegid(mnode n) {
        LinkedList<medge> cn=getConnectedEdges(n);//获取相连接的点
        String segid=null;
        if(cn.size()>1) {
            segid = findSegid(cn);//已处理点中 是否有点连接点 如果有返回相连接点sid
        }
        if(segid==null)
            segid=genNewSegid(cn.get(0).id);
        procedNodes.put(n,segid);
        segID2.put(n.id,segid);
        return segid;
    }

    private LinkedList<medge> getConnectedEdges(mnode n) {
        LinkedList<medge> rv=new LinkedList<medge>();
        int size = gs.edges.size();
        for (int j = 0; j < size; j++) {
            medge e=gs.edges.get(j);
            if(e.source.length()<1||e.target.length()<1)
                continue;
            if(n.id.equals(e.source)||n.id.equals(e.target))
                rv.add(e);
        }
        return rv;
    }

    private String findSegid(LinkedList<medge> cn) {
        int size = cn.size();
        String segid=null;
        for (int j = 0; j < size; j++) {
            medge n = cn.get(j);
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
    private String genNewSegid(String n) {
        String segid;
        segid=segidPrefix+n;
        segID.put(n,segid);
        return segid;
    }


    public void genSricpt() {
        genSegid();
        crossLinkResid();
        if(domainsAndLinks()!=true)
            return;
        try {
            scripts.process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean domainsAndLinks() {
        List<Integer> fixIdx =  new ArrayList<Integer>();
        List<Integer> fexIdx =  new ArrayList<Integer>();

        String textRigid = domainDef.get(0);
        String[] rigids=textRigid.split("#");
        if(rigids.length>2) {
            JOptionPane.showMessageDialog(null, "Dyn.fix and Dyn.group must be separated by #");
            return false;
        }
        String dynfixs=rigids[0].trim();
        String[] dynfix=dynfixs.split(",");
        String dyngrps=rigids[1].trim();
        String[] dyngrp=dyngrps.split(",");
        for(String i:dynfix){
            scripts.dynFixs.add(i);
            String fix[]=i.split(":");
            if(fix.length!=2){
                JOptionPane.showMessageDialog(null, "A single Dyn.fix must be defined like 1:19!");
                return false;
            }
            fixIdx.add(Integer.parseInt(fix[0]));
            fixIdx.add(Integer.parseInt(fix[1]));
        }
        for(String i:dyngrp){
            scripts.dynGroups.add(i);
        }

        String textFlex = domainDef.get(1).trim();
        String[] links=textFlex.split(",");
        for(String i:links){
            scripts.linkRs.add(i);
            String link[]=i.split(":");
            if(link.length!=2){
                JOptionPane.showMessageDialog(null, "A single linker must be defined like 1:19!");
                return false;
            }
            fexIdx.add(Integer.parseInt(link[0]));
            fexIdx.add(Integer.parseInt(link[1]));
        }
        for(Integer fixI:fixIdx){
            for(Integer fexI:fexIdx){
                if(Math.abs(fixI-fexI)==1){
                    scripts.breakResids.add(new breakResid(Math.min(fixI,fexI),Math.max(fixI,fexI))) ;
                }
            }
        }
        return true;
    }

    private String genLinkFileNames(String linkName,String edgeid) {
        return linkName+"_"+edgeid;
    }
}