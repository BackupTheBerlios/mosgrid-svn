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
 * Grid/VO definition
 */

package hu.sztaki.lpds.information.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author krisztian
 */

@Entity
@Table(name="groups")
@NamedQueries({
    @NamedQuery(name="GroupsTypeBean.exist",query="SELECT c FROM GroupsTypeBean c WHERE c.name=:pname AND c.type=:ptype"),
    @NamedQuery(name="GroupsTypeBean.all",query="SELECT c FROM GroupsTypeBean c ")
})

    public class GroupsTypeBean implements Serializable
    {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    /**
     * Getting the unique internal ID
     * @return ID
     */
    public long getId() {return id;}
    /**
     * Setting the unique internal ID
     * @param id ID
     */
    public void setId(long id) {this.id = id;}

    private String name;
    /**
     * Getting the group name
     * @return group name
     */
    public String getName() {return name;}
    /**
     * Setting the group name
     * @param name group name
     */
    public void setName(String name) {this.name = name;}

    private String type;
    /**
     * Getting the group type
     * @return group type
     */
    public String getType() {return type;}
    /**
     * Setting the group type
     * @param type group type
     */
    public void setType(String type) {this.type = type;}

    @OneToMany(fetch=FetchType.EAGER)//(mappedBy = "grp")
    @MapKey(name="name")
    private Map<String,GroupPropertyBean> properties;
    /**
     * Getting the group property list
     * @return list
     */
    public Map<String,GroupPropertyBean> getProperties() {return properties;}
    /**
     * Setting the group property list
     * @param properties
     */
    public void setProperties(Map<String,GroupPropertyBean> properties) {this.properties = properties;}

    @OneToMany(fetch=FetchType.EAGER)
    private Collection<ResourceBean> resources;
    /**
     * Getting the group resource list
     * @return resource list
     */
    public Collection<ResourceBean> getResources() {return resources;}
    /**
     * Setting the group resource list
     * @param resources resource list
     */
    public void setResources(Collection<ResourceBean> resources) {this.resources = resources;}


    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    /**
     * @see Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupsTypeBean)) {
            return false;
        }
        GroupsTypeBean other = (GroupsTypeBean) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "hu.sztaki.lpds.information.data.GridTypeBean[id=" + id + "]";
    }

}
