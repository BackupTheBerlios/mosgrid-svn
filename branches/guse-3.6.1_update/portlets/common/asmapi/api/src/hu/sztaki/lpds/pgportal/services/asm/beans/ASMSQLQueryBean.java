/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.pgportal.services.asm.beans;

/**
 *
 * @author akos balasko
 */
public class ASMSQLQueryBean {

    private String sqlUrl;
    private String sqlUserName;
    private String sqlPassword;
    private String sqlQuery;

   
    public String getSqlPassword() {
        return sqlPassword;
    }

    public void setSqlPassword(String sqlPassword) {
        this.sqlPassword = sqlPassword;
    }

    public String getSqlUserName() {
        return sqlUserName;
    }

    public void setSqlUserName(String sqlUserName) {
        this.sqlUserName = sqlUserName;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getSqlUrl() {
        return sqlUrl;
    }

    public void setSqlUrl(String sqlUrl) {
        this.sqlUrl = sqlUrl;
    }

    public ASMSQLQueryBean(){

    }
    
    public ASMSQLQueryBean(String sqlUrl,String sqlUserName, String sqlPassword, String sqlQuery){
        this.sqlUrl = sqlUrl;
        this.sqlUserName =sqlUserName;
        this.sqlPassword = sqlPassword;
        this.sqlQuery = sqlQuery;
    }
}
