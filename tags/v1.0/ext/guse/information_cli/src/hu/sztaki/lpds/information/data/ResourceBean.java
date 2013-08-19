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
 * Resource describer
 */

package hu.sztaki.lpds.information.data;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author krisztian karoczkai
 */
@Entity
@Table(name="GroupResource")
public class ResourceBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    private String site;
    public String getSite() {return site;}
    public void setSite(String site) {this.site = site;}

    private String jobmanager;
    public String getJobmanager() {return jobmanager;}
    public void setJobmanager(String jobmanager) {this.jobmanager = jobmanager;}



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ResourceBean)) {
            return false;
        }
        ResourceBean other = (ResourceBean) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.sztaki.lpds.information.data.ResourceBean[id=" + id + "]";
    }

}
