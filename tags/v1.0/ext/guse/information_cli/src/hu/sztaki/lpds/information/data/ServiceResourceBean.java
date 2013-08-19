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
 * Definition of the service connection in the given communication type
 */

package hu.sztaki.lpds.information.data;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author krisztian karoczkai
 */
@Entity
@Table(name="serviceresource")
@NamedQueries({
    @NamedQuery(name="ServiceResourceBean.com",query="SELECT c FROM ServiceResourceBean c WHERE c.src.sname=:s0 AND c.dst.sname=:s1")
})
public class ServiceResourceBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
/** Unique internal ID */
    private long id;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

/** Source service type*/
    @OneToOne(fetch=FetchType.EAGER)
    private GuseServiceTypeBean src;
    public GuseServiceTypeBean getSrc() {return src;}
    public void setSrc(GuseServiceTypeBean src) {this.src = src;}

/** Target service type*/
    @OneToOne(fetch=FetchType.EAGER)
    private GuseServiceTypeBean dst;
    public GuseServiceTypeBean getDst() {return dst;}
    public void setDst(GuseServiceTypeBean dst) {this.dst = dst;}

/** Used resource */
    private String res;
    public String getRes() {return res;}
    public void setRes(String res) {this.res = res;}

/** Caller class */
    private String caller;
    public String getCaller() {return caller;}
    public void setCaller(String caller) {this.caller = caller;}

    @ManyToOne(fetch=FetchType.EAGER)
    private GuseServiceCommunicationBean com;
    public GuseServiceCommunicationBean getCom() {return com;}
    public void setCom(GuseServiceCommunicationBean com) {this.com = com;}

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceResourceBean)) {
            return false;
        }
        ServiceResourceBean other = (ServiceResourceBean) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.sztaki.lpds.information.data.ServiceResourceBean[id=" + id + "]";
    }

}
