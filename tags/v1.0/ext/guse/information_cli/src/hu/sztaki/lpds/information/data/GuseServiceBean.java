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
 * gUSE service descriptor
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author krisztian karoczkai
 */
@Entity
@Table(name="service")
@NamedQueries({
    @NamedQuery(name="GuseServiceBean.all", query="SELECT c FROM GuseServiceBean c"),
    @NamedQuery(name="GuseServiceBean.com", query="SELECT c FROM GuseServiceBean c WHERE c.typ.sname=:from"),
    @NamedQuery(name="GuseServiceBean.url", query="SELECT c FROM GuseServiceBean c WHERE c.url=:url"),
    @NamedQuery(name="GuseServiceBean.resource", query="SELECT c FROM GuseServiceBean c WHERE c.com.resources.src.sname=:s0 AND c.com.resources.dst.sname=:s1")
})

public class GuseServiceBean implements Serializable
{
    private static final long serialVersionUID = 1L;
/** Unique internal ID */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

/** Service access for gUSE services */
    private String url;
    public String getUrl() {return url;}
    public void setUrl(String url) {this.url = url;}

/** URL to access a service from outside */
    private String surl;
    public String getSurl() {return surl;}
    public void setSurl(String surl) {this.surl = surl;}

/** URL to initialize the service  */
    private String iurl;
    public String getIurl() {return iurl;}
    public void setIurl(String iurl) {this.iurl = iurl;}

/** The service owner*/
    private String owner;
    public String getOwner() {return owner;}
    public void setOwner(String owner) {this.owner = owner;}

/** Status */
    private boolean state;
    public boolean isState() {return state;}
    public void setState(boolean state) {this.state = state;}

/** Service type */
    @ManyToOne(fetch=FetchType.EAGER)
    private GuseServiceTypeBean typ;
    public GuseServiceTypeBean getTyp() {return typ;}
    public void setTyp(GuseServiceTypeBean typ) {this.typ = typ;}

/** Service property list*/
    @OneToMany(mappedBy="service",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private Collection<ServicePropertyBean> properties;
    public Collection<ServicePropertyBean> getProperties() {return properties;}
    public void setServices(Collection<ServicePropertyBean> properties) {this.properties = properties;}

/** Communication types supported by the service */
    @OneToOne
    private GuseServiceCommunicationBean com;
    public GuseServiceCommunicationBean getCom() {return com;}
    public void setCom(GuseServiceCommunicationBean com) {this.com = com;}

/**Service users list */
 @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="services_user",
          joinColumns=@JoinColumn(name="service_id"),
          inverseJoinColumns=@JoinColumn(name="user_id"))
    private Collection<ServiceUserBean> users;
    public Collection<ServiceUserBean> getUsers() {return users;}
    public void setUsers(Collection<ServiceUserBean> users) {this.users = users;}





    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GuseServiceBean)) {
            return false;
        }
        GuseServiceBean other = (GuseServiceBean) object;
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
