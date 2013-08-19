<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<%@ page import = "com.liferay.portal.util.PortalUtil" %>
<%@ page import = "java.util.Enumeration" %>

<portlet:defineObjects />
<%
   HttpSession sessionObject = request.getSession();
   String jsessionid = sessionObject.getId();
   HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
   String advanced_value = httpReq.getParameter("wspgrade_advanced_assertion");
   Enumeration<String> names = httpReq.getParameterNames();
   boolean advanced = "true".equals(advanced_value);
   request.setAttribute("advanced",advanced);
%>

<portlet:actionURL var = "pURL">
    <portlet:param value="doUpload" name="guse"/>
</portlet:actionURL>
<portlet:resourceURL var="pURLapplet" >
    <portlet:param value="doAppletUpload" name="guse"/>
</portlet:resourceURL>
<portlet:actionURL var = "pURLview">
   <portlet:param value="doView" name="guse"/>
</portlet:actionURL>
<portlet:actionURL var = "pURLdel">
    <portlet:param value="doDelete" name="guse"/>
</portlet:actionURL>
<portlet:actionURL var = "pURLgen">
    <portlet:param value="doGoGenerate" name="guse"/>
</portlet:actionURL>


<div id="credlistdiv">
  <form method="post" action="${pURLdel}">
    <input type="hidden" name="guse" id="guse" value="doDelete">
    <input type="hidden" name="setforgrid" id="setforgrid" value="x" />

    <br>
    <c:choose>
      <c:when test="${uploadedlistsize==0}">
        <div align="center">You have to generate an assertion.</div>
      </c:when>
      <c:otherwise>

        <div id="div_delete" class="shape" style="display:none;position:absolute;background: background;" >
          <blockquote><p align="center">
              Are You sure?<br><br>
              <lpds:submit actionID="guse" actionValue="doDelete" cssClass="portlet-form-button" txt="button.delete" tkey="true" />
              <input type="button" onclick="javascript:document.getElementById('div_delete').style.display='none'" value="Cancel"/>
          </blockquote>
        </div>

        <table class="portlet-pane" cellspacing="1" cellpadding="1" border="0" width="100%">
          <tr>
            <td class="khead" align=center width="50%"><b>Issuer / Subject</b></td>
            <td class="khead" align=center width="28%"><b>Set for&nbsp;Grid</b></td>
            <td class="khead" align=center width="12%"><b>Time&nbsp;left</b></td>
            <td class="khead" align=center width="10%"><b>Action</b></td>
          </tr>
          <c:forEach var="cred" items="${uploadedlist}">
            <tr>
              <td >${cred.issuer}<br/>${cred.subject}</td>
              <td >${cred.set}</td>
              <td align=center>${cred.tleft}</td>
              <td >
                <input type="button" onclick="javascript:document.getElementById('setforgrid').value='${cred.set}';document.getElementById('div_delete').style.display='block';document.getElementById('div_delete').style.width=document.getElementById('credlistdiv').offsetWidth+'px';" value="Delete"/>
              </td>
            </tr>
          </c:forEach>

        </table>
      </c:otherwise>
    </c:choose>
  </form>

  <br>
  <c:if test="${advanced==true}">
	<form method="post" action="${pURL}" enctype="multipart/form-data" name="dataForm">
    <label>Select resource: </label>
    <select name="sresource" >
      <option value="" selected="1">All suitable resources</option>
      <c:forEach var="res" items="${rlist}" >
        <option value="${res}" >${res}</option>
      </c:forEach>
    </select>
    <label> Browse assertion: </label>
    <input  class="portlet-form-button" type="file" name="samlFile" />
    <lpds:submit actionID="guse" actionValue="doUpload" cssClass="portlet-form-button" txt="button.upload" tkey="true" />
  </form> 
  <br>
  </c:if>
  <div>${msg}</div>

  <div class="portlet-pane">
    <H3>Generation of new assertion:</H3>
    <div >
      <APPLET CODE="de.fzj.unicore.security.etd.ETDApplet.class" id="ETDAppletId"
              ARCHIVE="td-applet-1.0.jar"
              CODEBASE="/wspgrade/applet3"
              WIDTH=700 HEIGHT=200
              STYLE="background-color:transparent">

        <!-- Aliases of the keys -->
        <param name="aliases" value="${aliases}"/>
        <!-- corresponding DNs -->
        <param name="subjectdns" value="${subjectdns}"/>
        <!-- session id is not passed by default -->
        <param name="JSESSIONID" value="<%= jsessionid %>" />
        <!-- Upload URL -->
        <param name="action" value="${pURLapplet}"/>
        <!-- Redirect after upload (reload portlet) -->
        <param name="redirect" value="${pURLview}"/>
        <!-- Resources the user can upload -->
        <param name="resources" value="${rlist}"/>
        <c:choose>
	  <c:when test="${advanced}">
            <!-- shall the validity field be visible? -->
            <param name="hideValidity" value="false"/>
            <!-- shall the resources field be visible? -->
            <param name="hideResources" value="true"/>
            <!-- shall the download button be visible? -->
            <param name="hideDownload" value="false"/>
            <!-- Can the user save the generated assertion on his/her harddisk?-->
            <!-- Debugging options-->
            <param name="generateNegativeValidity" value="true"/>
         </c:when>

	 <c:otherwise>
            <!-- shall the validity field be visible? -->
            <param name="hideValidity" value="false"/>
            <!-- shall the resources field be visible? -->
            <param name="hideResources" value="true"/>
            <!-- shall the download button be visible? -->
            <param name="hideDownload" value="true"/>
            <!-- Can the user save the generated assertion on his/her harddisk?-->
            <!-- Debugging options-->
            <param name="generateNegativeValidity" value="false"/>
          </c:otherwise>
        </c:choose>
	

        
        <!-- Show a meaningful error messages if Java isn't working -->
            <div class="portlet-msg-error">Assertion applet could not be loaded.
                Please, ensure that Java and the Java plugin for your browser
                are installed and enabled. Java can be installed via
                <a href="https://www.java.com/download/index.jsp">this website</a>.
            </div>
      </APPLET>
    </div>
  </div>
</div>

