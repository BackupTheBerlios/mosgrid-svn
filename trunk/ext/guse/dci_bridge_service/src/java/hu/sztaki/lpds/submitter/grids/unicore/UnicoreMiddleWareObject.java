/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.submitter.grids.unicore;

import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.uas.security.ClientProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An Object storing UNICORE related job information
 * @author Patrick Schäfer
 */
public class UnicoreMiddleWareObject {

    // the UNICORE storage clients for xtreemfs and metadata
    private HashMap<String, StorageClient> xtreemFSStorage;
    private HashMap<String, StorageClient> metadataStorage;

    // users dn
    private String dn;

    // saml assertion
    private ClientProperties clientProperties=null;


    public UnicoreMiddleWareObject() {
        xtreemFSStorage = new HashMap<String, StorageClient>();
        metadataStorage = new HashMap<String, StorageClient>();
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getDn() {
        return dn;
    }

    public void setMetadataStorage(HashMap<String, StorageClient> metadataStorage) {
        this.metadataStorage = metadataStorage;
    }

    public void setXtreemFSStorage(HashMap<String, StorageClient> xtreemFSStorage) {
        this.xtreemFSStorage = xtreemFSStorage;
    }

    public HashMap<String, StorageClient> getMetadataStorage() {
        return metadataStorage;
    }

    public HashMap<String, StorageClient> getXtreemFSStorage() {
        return xtreemFSStorage;
    }

    public ClientProperties getClientProperties() {
        return clientProperties;
    }

    public void setClientProperties(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }
}
