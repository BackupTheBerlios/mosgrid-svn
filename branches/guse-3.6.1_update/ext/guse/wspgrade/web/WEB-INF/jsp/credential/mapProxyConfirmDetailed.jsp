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
    <input type="hidden" name="selCredId" id="selCredId" value="${newcred.id}" />
    <input type="hidden" name="gridName" id="gridName" value="${gridName}" />


    <div align=center><b>Confirm replacing certificate</b></div>

    <table border=0 bgcolor=#ffffff align=center>
        <tr><td>&nbsp;</td></tr>
        <tr><td colspan=2 align=center>
                <span dir="ltr"  class="portlet-msg-info">
                    <font color=brown>REPLACE</font> certificate for Grid: <font color=darkslategrey>${gridName}</font> ?<br><br>
                Are you sure you want to do this?
                </span></td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr align=center><td><table align=center><tr>
                        <td align="center">
                            <lpds:submit actionID="guse" actionValue="doMapReallyProxy" cssClass="portlet-form-button" txt="Yes" tkey="true" />
                        </td>
                        <td align="center">
                            <lpds:submit actionID="guse" actionValue="doGoCredentialList" cssClass="portlet-form-button" txt="Cancel" tkey="true" />
                        </td>
                    </tr></table></td>
        </tr>
        
    </table>
 <br>
    <div><b>Current certificate</b></div>
<table>
    <tr>
        <td  style="width:25%">Downloaded from:</td>
	<td  style="width:75%">${oldcred.dfrom}</td>
    </tr>
    <tr>
        <td  style="width:25%">Issued by:</td>
	<td  style="width:75%">${oldcred.issuer}</td>
    </tr>
    <tr>
        <td  style="width:25%">Subject:</td>
        <td  style="width:75%">${oldcred.subject}</td>
    </tr>
    <tr>
        <td  style="width:25%">Timeleft:</td>
        <td  style="width:75%">${oldcred.tleft}</td>
    </tr>
    <tr>
        <td  style="width:25%">Proxy type:</td>
        <td  style="width:75%">${oldcred.ptype}</td>
    </tr>
    <tr>
        <td  style="width:25%">Strength [bits]:</td>
        <td  style="width:75%">${oldcred.strenght}</td>
    </tr>
    <tr>
        <td  style="width:25%">Description:</td>
        <td  style="width:75%">${oldcred.desc}</td>
    </tr>
    <tr>
        <td  style="width:25%">Set for Grids:</td>
	<td  style="width:75%">${oldcred.set}</td>
    </tr>
</table>

    <b>New certificate</b>
<table>
    <tr>
        <td  style="width:25%">Downloaded from:</td>
	<td  style="width:75%">${newcred.dfrom}</td>
    </tr>
    <tr>
        <td  style="width:25%">Issued by:</td>
	<td  style="width:75%">${newcred.issuer}</td>
    </tr>
    <tr>
        <td  style="width:25%">Subject:</td>
        <td  style="width:75%">${newcred.subject}</td>
    </tr>
    <tr>
        <td  style="width:25%">Timeleft:</td>
        <td  style="width:75%">${newcred.tleft}</td>
    </tr>
    <tr>
        <td  style="width:25%">Proxy type:</td>
        <td  style="width:75%">${newcred.ptype}</td>
    </tr>
    <tr>
        <td  style="width:25%">Strength [bits]:</td>
        <td  style="width:75%">${newcred.strenght}</td>
    </tr>
    <tr>
        <td  style="width:25%">Description:</td>
        <td  style="width:75%">${newcred.desc}</td>
    </tr>
    <tr>
        <td  style="width:25%">Set for Grids:</td>
	<td  style="width:75%">${newcred.set}</td>
    </tr>
</table>

    <b><i>Message:</i></b> <font color="990033">${msg}</font>

</form>