<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURL">
    <portlet:param value="doUpload" name="guse"/>
</portlet:actionURL>
<portlet:actionURL var = "pURLback">
    <portlet:param value="doGoCredentialList" name="guse"/>
</portlet:actionURL>


<form method="post" action="${pURLback}" >
    <input type="hidden" name="guse" id="guse" value="doUpload">    
    <lpds:submit actionID="guse" actionValue="doGoCredentialList" cssClass="portlet-form-button" txt="Back to list" tkey="true" />
</form>

<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0"  width="100%" >
<tr valign="top">
<td style="width:100%">
<table class="portlet-frame">
	<tr bgcolor="#CCCCFF">
    	    <td colspan=4 align=center><b>Upload to MyProxy server</b></td>

	</tr>
</table>
</td>
</tr>


<tr valign="top">
<td style="width:100%">
<form method="post" action="${pURL}" enctype="multipart/form-data">
<table class="portlet-frame">
    <tr>
        <td  style="background-color: white" style="width:100%" colspan="4">

            <span  dir="ltr"  class="portlet-msg-info">Select keyfile (encrypted <i>userkey.pem</i>):</span>
        </td>
    </tr>
    <tr>
        <td  style="width:15%">Key file:</td>
        <td  style="width:35%"><input  class="portlet-form-button" type="file" name="keyfile" /></td>
        <td  style="width:15%">Keyfile password:</td>
        <td  style="width:35%"><input  class="portlet-form-input-field" type="password" name="keyFilePass" size="20" maxlength="200" />*</td>
    </tr>
    <tr>
        <td  style="background-color: white" style="width:100%" colspan="4">
            <span  dir="ltr"  class="portlet-msg-info">Select certificate file (<i>usercert.pem</i>):</span>
        </td>
    </tr>
    <tr>
        <td  style="width:15%">Certificate file:</td>
        <td  style="width:35%"><input  class="portlet-form-button" type="file" name="certfile" /></td>
        <td  style="width:50%" colspan="2"></td>
    </tr>
    <tr>
        <td  style="background-color: white" style="width:100%" colspan="4">
            <span  dir="ltr"  class="portlet-msg-info">Select myproxy upload properties:</span>
        </td>
    </tr>
    <tr>
        <td  style="width:15%">hostname</td>
        <td  style="width:35%"><input  class="portlet-form-input-field" type="text" name="host" value="" size="30" maxlength="200" />*</td>
        <td  style="width:15%">port</td>
        <td  style="width:35%"><input  class="portlet-form-input-field" type="text" name="port" value="7512" size="30" maxlength="200" />*</td>
    </tr>
    <tr>
        <td  style="width:15%">login</td>
        <td  style="width:35%"><input  class="portlet-form-input-field" type="text" name="login" value="" size="20" maxlength="200" />*,**</td>
        <td  style="width:15%">password</td>
        <td  style="width:35%"><input  class="portlet-form-input-field" type="password" name="pass" size="20" maxlength="200" />*,**</td>
    </tr>
    <tr>
        <td  style="width:15%">lifetime (hours)</td>
        <td  style="width:35%"><input  class="portlet-form-input-field" type="text" name="lifetime" value="10" size="30" maxlength="200" />*</td>
        <td  style="width:25%">Use DN as login</td>
        <td  style="width:25%"><input  class="portlet-form-field" type='checkbox' name='dnlogin' value='dnlogin'   />**</td>
    </tr>
    <tr bgcolor="#ffffff">
        <td colspan=4 align=left>*: Cannot be left empty.<br>
                **: Either <i>Login</i> and <i>Password</i> <b>or</b> <i>Use DN	as login</i> must be set.</td>
    </tr>
    <tr bgcolor="#ffffff">
        <td colspan=4 align=center><lpds:submit actionID="guse" actionValue="doUpload" cssClass="portlet-form-button" txt="Upload" tkey="true" /></td>
    </tr>
</table>
</form>
</td>
</tr>


    <tr valign="top">

<td style="width:100%">
<table class="portlet-frame">
	<tr bgcolor="#EFEFFF"><td>
	    <b><i>Message:</i></b> <font color="990033">${msg}</font>
	</td></tr>
    </table></td>
</tr>

</table>

