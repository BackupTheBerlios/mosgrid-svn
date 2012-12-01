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
/**
 * GUSE service communication type definition
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
@Table(name="servicetype")
@NamedQueries({
    @NamedQuery(name="GuseServiceTypeBean.sname", query="SELECT c FROM GuseServiceTypeBean c WHERE c.sname=:value"),
    @NamedQuery(name="GuseServiceTypeBean.all", query="SELECT c FROM GuseServiceTypeBean c")
})
        
public class GuseServiceTypeBean implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
/** Service type internal ID */
    private long id;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
/** Service type name */
    private String sname;
    public String getSname() {return sname;}
    public void setSname(String sname) {this.sname = sname;}

/** Service type describer */
    @Lob
    private String txt;
    public String getTxt() {return txt;}
    public void setTxt(String txt) {this.txt = txt;}

/** List of services*/
    @OneToMany(mappedBy="typ",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private Collection<GuseServiceBean> services;
    public Collection<GuseServiceBean> getServices() {return services;}
    public void setServices(Collection<GuseServiceBean> services) {this.services = services;}
 
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GuseServiceTypeBean)) {
            return false;
        }
        GuseServiceTypeBean other = (GuseServiceTypeBean) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.sztaki.lpds.information.data.GuseServiceBean[id=" + id + "]";
    }

}
