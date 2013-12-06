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
package hu.sztaki.lpds.wfs.com;

import java.util.Vector;

/**
 * A volatile output fileok informacioit
 * tartalmazo bean.
 *
 * A portal a wfs es a storage hasznalja.
 *
 * @author lpds
 */
public class VolatileBean {
    
    private ComDataBean comDataBean;
    
    // volatile entry beaneket tartalmaz
    private Vector volatileVector;
    
    /**
     * JavaBeaneknek szukseges konstruktor
     */
    public VolatileBean() {
        setComDataBean(new ComDataBean());
        setVolatileVector(new Vector());
    }
    
    /**
     * Egyszerubb hasznalat erdekeben alkalmazott konstruktor
     *
     * @param comBean - comdatabean workflow leiro bean
     */
    public VolatileBean(ComDataBean comBean) {
        setComDataBean(comBean);
        setVolatileVector(new Vector());
    }
    
    /**
     * Visszaadja a workflow leiro beant.
     * @return ComDataBean
     */
    public ComDataBean getComDataBean() {
        return comDataBean;
    }
    
    /**
     * Beallitja a workflow leiro beant.
     * @param comDataBean kommunikacios leiro
     */
    public void setComDataBean(ComDataBean comDataBean) {
        this.comDataBean = comDataBean;
    }
    
    /**
     * Visszaadja a volatile output informaciokat
     * tartalmazo vector erteket.
     *
     * @return Vector volatileVector
     */
    public Vector getVolatileVector() {
        return volatileVector;
    }
    
    /**
     * Beallitja a volatile output informaciokat
     * tartalmazo vector erteket.
     * @param volatileVector portlista
     */
    public void setVolatileVector(Vector volatileVector) {
        this.volatileVector = volatileVector;
    }
    
    /**
     * Hozzaad egy uj volatile entryt a listahoz
     * @param entryBean uj volatile entitas
     */
    public void addEntry(VolatileEntryBean entryBean) {
        this.volatileVector.addElement(entryBean);
    }
    
}
