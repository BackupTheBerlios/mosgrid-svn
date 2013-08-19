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
 * Handling base properties coming from web interface
 */

package hu.sztaki.lpds.information.service.alice;

import hu.sztaki.lpds.information.inf.InitCommand;
import hu.sztaki.lpds.information.local.PropertyLoader;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author krisztian
 */
public class MyPropertiesInitCommandImpl implements InitCommand
{

    public void run(ServletContext cx, HttpServletRequest request)
    {
        if(PropertyLoader.getInstance().getProperty("service.url")==null)
        {
            PropertyLoader.getInstance().setProperty("service.url",request.getParameter("service.url"));
            PropertyLoader.getInstance().setProperty("is.url",request.getParameter("service.url"));
            PropertyLoader.getInstance().setProperty("is.id",request.getParameter("resource.id"));
        }
    }

}
