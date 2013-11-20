<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURL">
</portlet:actionURL>

<form method="post" action="${pURL}">
    <input type="hidden" name="guse" id="guse" value="">

    <div align="center"><b>Credential Management Operations</b></div>
    <br>

    <table width="100%" cellpadding="5" >
        <tr>
            <td  width="20%">Hostname</td>
            <td  width="30%">
                <input class="portlet-form-input-field" type="text" name="host" value="${host}" size="30" maxlength="200" />*
            </td>
            <td  width="20%">Port</td>
            <td  width="30%">
                <input class="portlet-form-input-field" type="text" name="port" value="7512" size="30" maxlength="200" />*
            </td>
        </tr>
        <tr>
            <td>Login</td>
            <td>
                <input class="portlet-form-input-field" type="text" name="login" value="${login}" size="20" maxlength="200" />*
            </td>
            <td>Password</td>
            <td>
                <input class="portlet-form-input-field" type="password" name="pass" value="" />*
            </td>
        </tr>

        <tr>
            <td colspan="4" align="left">*: Cannot be left empty.<br></td>
        </tr>
        <tr>
            <td colspan="4" align="center"><b>Information</b></td>
        </tr>
        <tr><td colspan="4"><br></td></tr>
        <tr><td><lpds:submit actionID="guse" actionValue="doInformation" cssClass="portlet-form-button" txt="button.inform" tkey="true" /></td></tr>
        <tr><td>Owner:</td> <td colspan="3">${nfo.owner}</td></tr>
        <tr><td>Desc:</td> <td colspan="3">${nfo.desc}</td></tr>
        <tr><td>Start Date:</td> <td colspan="3">${nfo.sDate}</td></tr>
        <tr><td>End Date:</td> <td colspan="3">${nfo.eDate}</td></tr>

        <tr>
            <td colspan="4" align="center"><b>Change password</b></td>
        </tr>
        <tr><td colspan="4"><br></td></tr>
        <tr><td><lpds:submit actionID="guse" actionValue="doChangePass" cssClass="portlet-form-button" txt="button.changepass" tkey="true" /></td></tr>
        <tr><td>New Password</td> <td colspan="3"><input class="portlet-form-input-field" type="password" name="newPass" /></td></tr>
        <tr><td>Confirm Password</td> <td colspan="3"><input class="portlet-form-input-field" type="password" name="confNewPass" /></td></tr>

        <tr>
            <td colspan="4" align="center"><b>Destroy</b></td>
        </tr>
        <tr><td colspan="4"><br></td></tr>
        <tr><td><lpds:submit actionID="guse" actionValue="doDestroy" cssClass="portlet-form-button" txt="button.destroy" tkey="true" /></td></tr>

    </table>
    <div align="center"><lpds:submit actionID="guse" actionValue="doGoCredentialList" cssClass="portlet-form-button" txt="button.back" tkey="true" /></div>
    <b><i>Message:</i></b> <font color="990033">${msg}</font>
</form>