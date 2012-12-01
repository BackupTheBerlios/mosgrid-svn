package org.xtreemfs.portlet;

import java.util.ArrayList;

import org.xtreemfs.common.clients.Volume;
import org.xtreemfs.foundation.pbrpc.generatedinterfaces.RPC;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * Follows the thread local pattern for User data in a vaadin application
 * 
 * http://devblog.mycorner.fi/55/threadlocal-pattern-with-vaadin/
 * @author Patrick Schäfer
 *
 */
public class UserSettings implements TransactionListener {
  private static final long serialVersionUID = 5936942290863994902L;
  
  Volume volume = null;
  RPC.UserCredentials credentials = null;
  
  String currentDir = "";  
  String homeDir = "";
  
  String assertionFile = "";
 
  // Ids der Verzeichnisse im aktuellen Verzeichnis
  private ArrayList<Object> directoryIds = new ArrayList<Object>();
  
  private Application application;
  private static ThreadLocal<UserSettings> instance = 
    new ThreadLocal<UserSettings>();
  
  public UserSettings(Application application) {    
    this.application = application;
    // Set a value for the ThreadLocal to avoid any NPEs
    instance.set(this);
  }
  
  @Override
  public void transactionEnd(Application application, Object transactionData) {
      // Clear thread local instance at the end of the transaction
      if (this.application == application) {
          instance.set(null);
      }
  }

  @Override
  public void transactionStart(Application application, Object transactionData) {
      // Set the thread local instance
      if (this.application == application) {
          instance.set(this);
      }
  }

  public static void initialize(
      Application application) { 
      if (application == null) {
          throw new IllegalArgumentException("Application may not be null");
      }
      UserSettings appSettings = new UserSettings(application);
      application.getContext().addTransactionListener(appSettings);
  }
  
  /**
   * Returns an XtreemFS Volume
   * @return
   */
  public static Volume getVolume() {
    return instance.get().volume;
  }

  public static void setVolume(Volume volume) {
    instance.get().volume = volume;
  }

  /**
   * Returns an XtreemFS User Credential
   * @return
   */
  public static RPC.UserCredentials getCredentials() {
    return instance.get().credentials;
  }

  public static void setCredentials(RPC.UserCredentials credentials) {
    instance.get().credentials = credentials;
  }

  public static String getCurrentDir() {
    if (instance.get() != null) {      
      return instance.get().currentDir;
    }
    return null;
  }

  public static void setCurrentDir(String currentDir) {
    instance.get().currentDir = currentDir;
  }

  public static String getHomeDir() {
    return instance.get().homeDir;
  }

  public static void setHomeDir(String homeDir) {
    instance.get().homeDir = homeDir;
  }

  public static String getAssertionFile() {
    return instance.get().assertionFile;
  }

  public static void setAssertionFile(String assertionFile) {
    instance.get().assertionFile = assertionFile;
  }

  public static ArrayList<Object> getDirectoryIds() {
    if (instance.get() != null) {
      ArrayList<Object> directoryIds = instance.get().directoryIds;
      if (directoryIds != null) {
        return directoryIds;
      }
    }
    return  new ArrayList<Object>();
  }

  public static Object[] getDirectoryIdsAsArray() {
    if (instance.get() != null 
        && instance.get().directoryIds != null) {
      return instance.get().directoryIds.toArray(new Object[]{});
    }
    return new Object[]{};
  }

  public static void setDirectoryIds(ArrayList<Object> directoryIds) {
    instance.get().directoryIds = directoryIds;
  }

  public static void addDirectoryId(Object id) {
    instance.get().directoryIds.add(id);
  }

}
