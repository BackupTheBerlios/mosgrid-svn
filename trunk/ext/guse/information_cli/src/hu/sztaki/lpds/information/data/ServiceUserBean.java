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
 * Description of the service users
 */

package hu.sztaki.lpds.information.data;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author krisztian karoczkai
 */
@Entity
@Table(name="serviceuser")
@NamedQueries(@NamedQuery(name="ServiceUserBean.lname",query="SELECT s FROM ServiceUserBean s WHERE s.lname=:lname"))
public class ServiceUserBean implements Serializable
{
    private static final long serialVersionUID = 1L;
/** Unique internal ID */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

/** The user's login name */
    private String lname;
    public String getLname() {return lname;}
    public void setLname(String lname) {this.lname = lname;}

/** Service connection */
    @ManyToMany(mappedBy="users",fetch=FetchType.EAGER)
    private Collection<GuseServiceBean> service;
    public Collection<GuseServiceBean> getService() {return service;}
    public void setService(Collection<GuseServiceBean> service) {this.service = service;}

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceUserBean)) {
            return false;
        }
        ServiceUserBean other = (ServiceUserBean) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.sztaki.lpds.information.data.ServiceUserBean[id=" + id + "]";
    }

}
