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

package hu.sztaki.lpds.pgportal.service.workflow.notify.plugins;

import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyBean;

/**
 * Interface class of a notifier plugin
 *
 * @author lpds
 */
public interface NotifyPlugin {
/**
 * Sending notification to the user on any channels (e-mail)
 * @param notifyBean message descriptor
 * @throws java.lang.Exception sending error
 */
    public abstract void sendNotify(NotifyBean notifyBean) throws Exception;
    
}
