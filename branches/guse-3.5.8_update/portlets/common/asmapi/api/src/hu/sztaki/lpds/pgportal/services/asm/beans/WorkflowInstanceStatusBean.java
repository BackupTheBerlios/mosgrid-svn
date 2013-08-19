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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.pgportal.services.asm.beans;

/**
 * Class to store statuscode and associated color for a workflow instance
 * 
 * @author akos
 */
public class WorkflowInstanceStatusBean {

	private String status = "";
	private String color = "";

	public WorkflowInstanceStatusBean() {

	}

	public WorkflowInstanceStatusBean(String status, String color) {
		this.status = status;
		this.color = color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getColor() {
		return color;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "STATUS: " + status + ", COLOR: " + color;
	}

}
