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
 * Valos output port generatorsagi vizsgalatahoz szukseges seged bean
 * Az output portrol nyilvantartott adatoknak
 */

package hu.sztaki.lpds.wfs.service.angie.datas;

/**
 * @author krisztian
 */
public class OutputDescrioptionBean {
    String name;
    long id;
    boolean generator=false;


    public OutputDescrioptionBean(long id, String name) {
        this.name = name;
        this.id = id;
    }



    public boolean isGenerator() {
        return generator;
    }

    public void setGenerator(boolean generator) {
        this.generator = generator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
