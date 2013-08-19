<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURL">
</portlet:actionURL>

<form method="post" action="${pURL}">
<input type="hidden" name="guse" id="guse" value="">
<input type="hidden" name="selCredId" id="selCredId" value="${selCredId}" />

<div align="center"><b>Set certificate for GRID</b></div>
<br>
<table border=0 align=center>
    <tr><td colspan=2 align=center>Certificate DOWNLOAD successful.</td></tr>
    <tr><td>&nbsp; </td></tr>
    <tr><td colspan=2 align=center>Certificate download was successful. You may set this certificate for a Grid now.</td></tr>
    <tr><td colspan=2 align=center>(Of course, you can set it later as well.)</td></tr>
    <tr><td>&nbsp; </td></tr>
    <tr><td colspan=2 align=center>Do you want to do this now?</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr align=center><td>
            <lpds:submit actionID="guse" actionValue="doGoMapProxy" cssClass="portlet-form-button" txt="button.setforgrid" tkey="true" />
                    <lpds:submit actionID="guse" actionValue="doGoCredentialList" cssClass="portlet-form-button" txt="button.cancel" tkey="true" />
        </td></tr>
</table>
<br>
<div><b><i>Message:</i></b> <font color="990033">${msg}</font></div>
</form>