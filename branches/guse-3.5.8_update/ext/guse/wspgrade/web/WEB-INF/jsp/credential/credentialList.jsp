<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURL">
    <portlet:param value="doSubmit" name="guse"/>
</portlet:actionURL>

<div id="credlistdiv">
<form method="post" action="${pURL}">
<input type="hidden" name="guse" id="guse" value="doSubmit">
<input type="hidden" name="selCredId" id="selCredId" value="x" />

<br>
<c:choose>
<c:when test="${credssize==0}">
    <div align="center">There is no downloaded certificate.</div>
</c:when>
<c:otherwise>

<div id="div_delete" class="shape" style="display:none;position:absolute;background: background;" >
    <blockquote><p align="center">
        Are You sure?<br><br>
       <lpds:submit actionID="guse" actionValue="doDelete" cssClass="portlet-form-button" txt="Delete" tkey="true" />
       <input type="button" onclick="javascript:document.getElementById('div_delete').style.display='none'" value="Cancel"/>
    </blockquote>
</div>

<table width="100%">
    <tr bgcolor="#EFEFFF">
        <td align=center width="55%"><b>Issuer</b></td>
        <td align=center width="13%"><b>Set for&nbsp;Grids</b></td>
        <td align=center width="7%"><b>Time&nbsp;left</b></td>
        <td align=center width="25%">[Actions]</td>
    </tr>
<c:forEach var="cred" items="${creds}">
    <tr>
        <td >${cred.issuer}</td>
        <td >${cred.set}</td>
        <td align=center bgcolor="#ffffff">${cred.tleft}</td>
        <td >
            <lpds:submit actionID="guse" actionValue="doGoDetails" paramID="selCredId" paramValue="${cred.id}" cssClass="portlet-form-button" txt="Details" tkey="true" />
            <lpds:submit actionID="guse" actionValue="doGoMapProxy" paramID="selCredId" paramValue="${cred.id}" cssClass="portlet-form-button" txt="Set for Grid" tkey="true" />
            <%--   <lpds:submit actionID="guse" actionValue="doDelete" paramID="selCredId" paramValue="${cred.id}" cssClass="portlet-form-button" txt="Delete" tkey="true" />--%>
            <input type="button" onclick="javascript:document.getElementById('selCredId').value='${cred.id}';document.getElementById('div_delete').style.display='block';document.getElementById('div_delete').style.width=document.getElementById('credlistdiv').offsetWidth+'px';" value="Delete"/>
        </td>
    </tr>
</c:forEach>

</table>
</c:otherwise>
</c:choose>
    
<table>
    <tr><td><lpds:submit actionID="guse" actionValue="doGoDownload" cssClass="portlet-form-button" txt="Download" tkey="true" /></td><td>(Dowload certificate from MyProxy server.)</td></tr>
    <tr><td><lpds:submit actionID="guse" actionValue="doGoUpload" cssClass="portlet-form-button" txt="Upload" tkey="true" /></td><td>(Upload authentication data to MyProxy server.)</td></tr>
    <tr><td><lpds:submit actionID="guse" actionValue="doGoOtherOps" cssClass="portlet-form-button" txt="Credential Management" tkey="true" /></td><td>(Display information, change MyProxy passphrase, remove a credential from MyProxy server.)</td></tr>
</table>    


<br>
<div><b><i>Message:</i></b> <font color="990033"> ${msg}</font></div>
</form>
</div>