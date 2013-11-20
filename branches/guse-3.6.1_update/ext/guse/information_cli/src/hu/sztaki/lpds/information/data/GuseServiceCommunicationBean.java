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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.information.data;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author krisztian karoczkai
 */
@Entity
@Table(name="servicecomdef")
@NamedQueries({
    @NamedQuery(name="GuseServiceCommunicationBean.all", query="SELECT c FROM GuseServiceCommunicationBean c")
})

public class GuseServiceCommunicationBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
/** Unique internal ID */
    private long id;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
/** Communication type name */
    private String cname;
    public String getCname() {return cname;}
    public void setCname(String cname) {this.cname = cname;}
/** Description */
    @Lob
    private String txt;
    public String getTxt() {return txt;}
    public void setTxt(String txt) {this.txt = txt;}
// kapcsolatott megvalosito servizek
/*
    @ManyToMany(mappedBy="users",fetch=FetchType.EAGER,cascade=CascadeType.ALL)
    private Collection<GuseServiceBean> service;
    public Collection<GuseServiceBean> getService() {return service;}
    public void setService(Collection<GuseServiceBean> service) {this.service = service;}
*/
    @OneToMany(mappedBy="com",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private Collection<ServiceResourceBean> resources;
    public Collection<ServiceResourceBean> getResources() {return resources;}
    public void setResources(Collection<ServiceResourceBean> resources) {this.resources = resources;}
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GuseServiceCommunicationBean)) {
            return false;
        }
        GuseServiceCommunicationBean other = (GuseServiceCommunicationBean) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.sztaki.lpds.information.data.GuseServiceCommunicationBean[id=" + id + "]";
    }

}
