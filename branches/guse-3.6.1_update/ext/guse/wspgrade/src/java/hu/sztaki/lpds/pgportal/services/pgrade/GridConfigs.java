/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * This file is part of P-GRADE Grid Portal.
 *
 * P-GRADE Grid Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * P-GRADE Grid Portal is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * P-GRADE Grid Portal.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2006-2008 MTA SZTAKI
 *
 */
/*
 */
package hu.sztaki.lpds.pgportal.services.pgrade;

import java.util.*;
import java.io.*;
//import hu.sztaki.lpds.pgportal.services.utils.PropertyLoader;
import hu.sztaki.lpds.information.local.PropertyLoader;
//import hu.sztaki.lpds.pgportal.services.is.mds2.JobManagerSettingsFacade;


/**
 * @author Csaba Nemeth
 */
//
public class GridConfigs {

  public static final String CLUSTERGRID_NAME = "CLUSTER-GRID";
  public static final String LCG_BROKER_NAME = "LCG_2_BROKER"; // this should be the end of the grid name
// HG changed 2-dec-2005
// Id do not know who did this chsnge
  public static final String GLITE_BROKER_NAME = "GLITE_BROKER"; // this should be the end of the grid name
// HG end
  private static GridConfigs instance = null;
  private Hashtable gridConfigs = null;
  private String configFilePath = null;

  public static synchronized GridConfigs getInstance() {
//    System.out.println("GridConfigs.getInstance() START");
    if (GridConfigs.instance == null) {
      GridConfigs.instance = new GridConfigs();
    }
//    System.out.println("GridConfigs.getInstance() END");
    return GridConfigs.instance;
  }

  public synchronized GridConfiguration getGridConfig(String gridName){
    GridConfigs gc = GridConfigs.getInstance();
    GridConfiguration[] gcs = gc.getGridConfigs();
    for (int i = 0; i < gcs.length; i++) {
      if (gcs[i].getName().equals(gridName)){
        return gcs[i];
      }
    }
    return null;
  }

  private GridConfigs() {
    this.gridConfigs = new Hashtable();
    try {
      this.configFilePath = PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"users/.grid.resources.conf";
              //PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "/users/.grid.resources.conf";
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    if (this.configFilePath != null) {
      this.loadConfiguration();
    }
  }

  public synchronized boolean addGridConfiguration(GridConfiguration gc,boolean doSave) {
    if (this.gridConfigs.get(gc.getName()) != null) {
      return false;
    }
    this.gridConfigs.put(gc.getName(), gc);
    if (doSave){
      this.saveGridConfigs();
    }
    return true;
  }

  public static boolean existsGrid(String gridName){
    GridConfigs gc = GridConfigs.getInstance();
    if (gc.getGridConfig(gridName) == null) {
      return false;
    }
    return true;
  }

//  public synchronized void deleteGridConfiguration(String gridName) {
//    System.out.println("deleteGridConfiguration("+gridName+") called");
//    this.getGridConfig(gridName).removeIS();
//    this.gridConfigs.remove(gridName);
//    this.deleteAllJMConfigFiles(gridName);
//    GridJobManagerConfigs gJMConfs = GridJobManagerConfigs.getDefaultInstance(gridName);
//    gJMConfs.removeAllConfigurations(gridName);
//    this.saveGridConfigs();
//  }

  public GridConfiguration[] getGridConfigs() {
    Vector gcs = new Vector();
    Set keys = this.gridConfigs.keySet();
    for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
      GridConfiguration gc = (GridConfiguration)this.gridConfigs.get(iter.next());
      gcs.add(gc);
    }
    Collections.sort(gcs);
    GridConfiguration[] result = new GridConfiguration[gcs.size()];
    for (int i = 0; i < gcs.size(); i++) {
      result[i] = (GridConfiguration) gcs.elementAt(i);
    }
    return result;
  }

  public synchronized void saveGridConfigs() {
    this.saveToFile();
  }

  private void deleteAllJMConfigFiles(String gridName){
    File gridConfigs = new File(this.configFilePath);
    File[] userDirs = gridConfigs.getParentFile().listFiles(new FileFilter(){
      public boolean accept(File f){
        return f.isDirectory()?true:false;
      }
    });
    for (int i = 0; i < userDirs.length; i++) {
      File f = new File(userDirs[i].getAbsolutePath()+"/.resources.conf."+gridName);
      if (f.isFile()) f.delete();
    }
    File f = new File(gridConfigs.getParent()+"/.resources.conf."+gridName);
    if (f.isFile()) f.delete();
  }

  /**
   * Saves current content of the configuration to the user's configuration file.
   */
  private synchronized boolean saveToFile() {
    try {
      File dest = new File(this.configFilePath);
      if (!dest.getParentFile().exists())
        dest.getParentFile().mkdir();
      BufferedWriter bw = new BufferedWriter(new FileWriter(dest));
      bw.write("# GRID CONFIGURATIONS for P-GRADE PORTAL\n");
      for (Iterator iter = this.gridConfigs.keySet().iterator(); iter.hasNext(); ) {
        GridConfiguration item = (GridConfiguration)this.gridConfigs.get(iter.
            next());
        bw.write("" + item);
        bw.newLine();
      }
      bw.flush();
      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }
  //HG changes 13-10-2005 
  //new
  private synchronized void loadConfiguration() {
    File cf = new File(this.configFilePath);
    BufferedReader r = null;
    if (!cf.exists()) {
      return;
    }
    else {
      try {
        r = new BufferedReader(new FileReader(cf));
        String line = null;
        do {
          line = r.readLine();
          if (line != null) {
            if (!line.startsWith("#")) {
              while (line.startsWith(" ")) {
                line = line.substring(1);
              }
              String[] elements = line.split(" ");

              if (elements.length == 0) {
                //empty line
              }
              else if (elements.length == 3) {
                // NOT_DEFINED
                GridConfiguration gc = new GridConfiguration(elements[0]);
                this.addGridConfiguration(gc,false);
              }
              else if (elements.length == 6) {
                if (elements[2].equals(GridConfiguration.IS_TYPES[2])){
                  //LCG2
                  GridConfiguration gc = new GridConfiguration(elements[0]);
                  gc.defineIS((short)2, elements[3], elements[4], elements[5]);
                  this.addGridConfiguration(gc,false);
                }
              } else if (elements.length == 8) {
                if (elements[2].equals(GridConfiguration.IS_TYPES[1])){
                  //MDS2
                  GridConfiguration gc = new GridConfiguration(elements[0]);
                  gc.defineIS((short)1, elements[3], elements[4], elements[5]);
                  gc.setMPSData(elements[6],elements[7]);
                  this.addGridConfiguration(gc,false);
                }
              }
            }
          }
        }
        while (line != null);
        r.close();
      }
      catch (Exception ex) {
        System.out.println("GridConfigs.loadConfiguration() from '" +
                           this.configFilePath + "'  ~ failed:" + ex.getMessage());
      }
    }
  }
  
  public static boolean isBrokered(String gridName) {
      return gridName.endsWith(CLUSTERGRID_NAME) || gridName.endsWith(LCG_BROKER_NAME) || gridName.endsWith(GLITE_BROKER_NAME);
  }

  public static synchronized String[] getGridNames(){
    GridConfiguration[] grids = GridConfigs.getInstance().getGridConfigs();
    if (grids == null || grids.length == 0){
      return null;
    }
    Vector names = new Vector(grids.length);
    for (int i = 0; i < grids.length; i++) {
      names.add(i,grids[i].getName());
    }
    String[] result = new String[grids.length];
    return (String[])names.toArray(result);
  }

} 
