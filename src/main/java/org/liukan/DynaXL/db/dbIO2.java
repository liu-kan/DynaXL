package org.liukan.DynaXL.db;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by liuk on 2016/6/27.
 */
public class dbIO2 {

    /** The c. */
    Connection c = null;

    /** The stmt. */
    Statement stmt = null;

    /** The password. */
    String driver,conn_url,username,password;

    /**
     * 构造函数，下面各参数以mysql为例.
     *
     * @param driver com.mysql.jdbc.Driver
     * @param conn_url jdbc:mysql://192.168.0.2:3336/db_zlpj
     * @param username root
     * @param password wipm
     */
    public dbIO2(String driver,String conn_url,String username,
                String password){
        this.driver=driver;
        this.conn_url=conn_url;this.username=username;
        this.password=password;
        conndb(driver, conn_url,username,
                password);
    }

    /**
     * 已构造函数中设置的参数连接数据库.
     */
    public void conndb(){
        conndb(driver, conn_url,username,password);
    }

    /**
     * 连接数据库，参数同构造函数.
     *
     * @param driver the driver
     * @param conn_url the conn_url
     * @param username the username
     * @param password the password
     */
    public void conndb(String driver,String conn_url,String username,
                       String password){
        String connCMD = String.format("%s?user=%s&password=%s&useUnicode=true&characterEncoding=UTF8",conn_url,username, password);
        try {
            Class.forName(driver);
            if(driver.contains("sqlite"))
                connCMD=conn_url;
            c = DriverManager.getConnection(connCMD);
            //c.setAutoCommit(false);
            System.out.println("Opened database successfully");
        }
        catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            //System.exit(0);
        }
    }
    public ArrayList<String> readCrossLink(int gid){
        ArrayList<String> rv=null;
        PreparedStatement statement=null;
        try {
            stmt=c.createStatement();

            ResultSet rs = stmt.executeQuery( "select * FROM crosslinks where gid="+gid+";" );
            if (!rs.isBeforeFirst() ) {
                return null;
            } else{
                rv=new ArrayList<>();
                String sql = "select * FROM crosslinks  where gid=?";
                statement = c.prepareStatement(sql);
                statement.setInt(1, gid);
                rs = statement.executeQuery();
                String parDomain=null,parLinker=null;
                while ( rs.next() ) {
                    parDomain=rs.getString("parDomain");
                    parLinker=rs.getString("parLinker");
                }
                rv.add(parDomain);rv.add(parLinker);
                stmt.close();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {

        }
        return rv;
    }
    public int saveCrossLink(int gid,String parDomain ,String parLinker){
        if(gid<0)
            return gid;
        try {
            PreparedStatement statement=null;
            stmt=c.createStatement();
                ResultSet rs = stmt.executeQuery( "select * FROM crosslinks where gid="+gid+";" );

                if (!rs.isBeforeFirst() ) {

                    String sql = "INSERT INTO crosslinks (gid, parDomain, parLinker)" +
                            "VALUES (?, ?, ?)";

                        statement = c.prepareStatement(sql);

                    statement.setInt(1, gid);
                    statement.setString(2, parDomain);
                    statement.setString(3, parLinker);
                    statement.executeUpdate();

                } else{
                    //System.out.println("update G!");
                    String sql = "UPDATE crosslinks SET parDomain=?, parLinker=? WHERE gid=?";

                    statement = c.prepareStatement(sql);
                    statement.setString(1, parDomain);
                    statement.setString(2, parLinker);
                    statement.setInt(3, gid);
                    statement.executeUpdate();

                }
            } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return gid;
    }
    public String readVar(String name){
        ResultSet rs;
        String  value=null;
        try {
            PreparedStatement statement=null;
            stmt=c.createStatement();
            rs = stmt.executeQuery( "select * FROM parms  where name=\""+name+"\";" );
            if (!rs.isBeforeFirst() ) {
                return null;
            } else{
                String sql = "select * FROM parms  where name=?";
                statement = c.prepareStatement(sql);
                statement.setString(1, name);
                rs = statement.executeQuery();
                while ( rs.next() ) {
                    value = rs.getString("value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return value;
    }
    public boolean writeVar(String name,String v){
        try {
            PreparedStatement statement=null;
            stmt=c.createStatement();
            ResultSet rs = stmt.executeQuery( "select * FROM parms  where name=\""+name+"\";" );
            if (!rs.isBeforeFirst() ) {
                String sql = "INSERT INTO parms (name, value)" +
                        "VALUES (?, ?)";
                statement = c.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, v);
                statement.executeUpdate();
            } else{
                String sql = "UPDATE parms SET value=? WHERE name=?";
                statement = c.prepareStatement(sql);
                statement.setString(1, v);
                statement.setString(2, name);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void close() {
        try {
            c.close();
            System.out.println("DB closed");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("Have tried to close DB");
        }
    }
}
