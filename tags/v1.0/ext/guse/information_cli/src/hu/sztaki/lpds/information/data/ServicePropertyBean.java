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
 * Service property definition
 */

package hu.sztaki.lpds.information.data;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author krisztian karoczkai
 */
@Entity
@Table(name="serviceproperties")
@NamedQueries({
    @NamedQuery(name="ServicePropertyBean.skey", query="SELECT c FROM ServicePropertyBean c WHERE c.propkey=:key AND c.service.id=:sid")
})
        public class ServicePropertyBean implements Serializable
{
    private static final long serialVersionUID = 1L;
/** Unique ID */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    private String propkey;
    public String getPropkey() {return propkey;}
    public void setPropkey(String propkey) {this.propkey = propkey;}

    private String propvalue;
    public String getPropvalue() {return propvalue;}
    public void setPropvalue(String propvalue) {this.propvalue = propvalue;}

/** Service */
    @ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.PERSIST)
    private GuseServiceBean service;
    public GuseServiceBean getService() {return service;}
    public void setService(GuseServiceBean service) {this.service = service;}

    @Override
    public int hashCode() {
        int hash = 0;
        hash += id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServicePropertyBean)) {
            return false;
        }
        ServicePropertyBean other = (ServicePropertyBean) object;
        if (this.id!=other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.sztaki.lpds.information.data.ServiceProperty[id=" + id + "]";
    }

}
