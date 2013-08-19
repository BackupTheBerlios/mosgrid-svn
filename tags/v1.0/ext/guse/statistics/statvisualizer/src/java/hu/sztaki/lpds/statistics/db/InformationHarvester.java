/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.statistics.db;

/**
 *
 * @author akos
 */
public class InformationHarvester {

      DBBase connectionSource = null;

    public DBBase getConnectionSource() {
        return connectionSource;
    }

    public void setConnectionSource(DBBase connectionSource) {
        this.connectionSource = connectionSource;
    }
    public InformationHarvester(){
        
    }

}
