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
 * File tartalomra vonatkozo feltetel leiras
 */

package hu.sztaki.lpds.storage.com;

/**
 * @author krisztian
 */
public class IfBean {
    private String srcpath;
    private String operation;
    private String dstvalue;
    private String dstpath;

    public String getDstpath() {return dstpath;}

    public void setDstpath(String dstpath) {this.dstpath = dstpath;}

    public String getDstvalue() {return dstvalue;}

    public void setDstvalue(String dstvalue) {this.dstvalue = dstvalue;}

    public String getOperation() {return operation;}

    public void setOperation(String operation) {this.operation = operation;}

    public String getSrcpath() {return srcpath;}

    public void setSrcpath(String srcpath) {this.srcpath = srcpath;}


}
