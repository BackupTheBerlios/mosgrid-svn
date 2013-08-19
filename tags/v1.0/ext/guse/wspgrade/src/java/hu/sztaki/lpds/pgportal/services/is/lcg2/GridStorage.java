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

/*
  * GridStorage.java
  *
  * Created on February 10, 2005, 6:53 PM
  */

package hu.sztaki.lpds.pgportal.services.is.lcg2;

import java.util.Hashtable;

/**
  *
  * @author  Tamas Boczko
  */
public class GridStorage {

    private static GridStorage instance;
    
    public static GridStorage getInstance(){
        if (GridStorage.instance == null) {
            GridStorage.instance = new GridStorage();
        }
        return GridStorage.instance;
    }
    
    protected static void init(){
        getInstance();
    }    
    
    private Hashtable gridList;
    
    /** Creates a new instance of GridStorage */
    public GridStorage() {
        this.gridList =new Hashtable();
    }
    
    public LCGGrid getGrid(String gridName){
        return (LCGGrid)this.gridList.get(gridName);
    }
    
    public void addGrid( LCGGrid grid){
        this.gridList.put(grid.getGridName() , grid);
    }
    
    public void setGrid( LCGGrid grid){
        this.gridList.put(grid.getGridName() , grid);
    }
   
    public void removeGrid(String gridName){
        this.gridList.remove(gridName);
    }
    
}
