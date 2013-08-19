<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURL">
</portlet:actionURL>

<form method="post" action="${pURL}">
<input type="hidden" name="guse" id="guse" value="">

<div align=center><b>Certificate details:</b></div>
<table>
    <tr>
        <td  style="width:25%">Downloaded from:</td>
	<td  style="width:75%">${cred.dfrom}</td>
    </tr>
    <tr>
        <td  style="width:25%">Issued by:</td>
	<td  style="width:75%">${cred.issuer}</td>
    </tr>
    <tr>
        <td  style="width:25%">Subject:</td>
        <td  style="width:75%">${cred.subject}</td>
    </tr>
    <tr>
        <td  style="width:25%">Timeleft:</td>
        <td  style="width:75%">${cred.tleft}</td>
    </tr>
    <tr>
        <td  style="width:25%">Proxy type:</td>
        <td  style="width:75%">${cred.ptype}</td>
    </tr>
    <tr>
        <td  style="width:25%">Strength [bits]:</td>
        <td  style="width:75%">${cred.strenght}</td>
    </tr>
    <tr>
        <td  style="width:25%">Description:</td>
        <td  style="width:75%">${cred.desc}</td>
    </tr>
    <tr>
        <td  style="width:25%">Set for Grids:</td>
	<td  style="width:75%">${cred.set}</td>
    </tr>
</table>
<div align="center">
    <lpds:submit actionID="guse" actionValue="doGoCredentialList" cssClass="portlet-form-button" txt="button.Back" tkey="true" />
</div>    
    
<b><i>Message:</i></b> <font color="990033">Press the button.</font>
</form>