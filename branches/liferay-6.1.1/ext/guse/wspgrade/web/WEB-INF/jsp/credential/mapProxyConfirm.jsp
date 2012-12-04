<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURL">
    <portlet:param value="doSubmit" name="guse"/>
</portlet:actionURL>

<form method="post" action="${pURL}">
<input type="hidden" name="guse" id="guse" value="">
<input type="hidden" name="gridName" value="${gridName}" />
<input type="hidden" name="selCredId" value="${selCredId}" />
<div align=center><b>Confirm replacing certificate</b></div>

<table border=0 bgcolor=#ffffff align=center>
    <tr><td colspan=2 align=center><span dir="ltr"  class="portlet-msg-info">
                <font color=brown>REPLACE</font> certificate for Grid: <font color=darkslategrey>${gridName}</font> ?<br><br>
                There is already a certificate set for this Grid. You will replace this certificate with the new one.<br>
                Are you sure you want to do this?
            </span></td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr align=center><td><table align=center><tr>
                    <td align="center">
                        <lpds:submit actionID="guse" actionValue="doMapProxyShowDetails" cssClass="portlet-form-button" txt="Details" tkey="true" />
                    </td>
                    <td align="center">
                        <lpds:submit actionID="guse" actionValue="doMapReallyProxy" cssClass="portlet-form-button" txt="Yes" tkey="true" />
                    </td>
                    <td align="center">
                        <lpds:submit actionID="guse" actionValue="doGoCredentialList" cssClass="portlet-form-button" txt="Cancel" tkey="true" />
                    </td>
                </tr></table></td>
    </tr>
</table>

<div><b><i>Message:</i></b> <font color="990033">${msg}</font></div>

</form>