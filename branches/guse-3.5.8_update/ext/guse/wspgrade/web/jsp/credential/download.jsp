<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURL">
</portlet:actionURL>

<form method="post" action="${pURL}">
<input type="hidden" name="guse" id="guse" value="">

<div align="center"><b>Download from MyProxy server</b></div>
<br>

<table class="portlet-frame" width="100%">
    <tr>
         <td  style="width:20%">Hostname:</td>
         <td  style="width:30%">
            <input  class="portlet-form-input-field" type="text" name="host" value="" size="30" maxlength="200" />*
         </td>
         <td  style="width:20%">Port:</td>
         <td  style="width:30%">
            <input  class="portlet-form-input-field" type="text" name="port" value="7512" size="30" maxlength="200" />*
         </td>
   </tr>
   <tr>
           <td>Login:</td>
           <td>
              <input  class="portlet-form-input-field" type="text" name="login" value="" size="20" maxlength="200" />*
           </td>
           <td>Password:</td>
           <td>
             <input  class="portlet-form-input-field" type="password" name="pass" value="" />*
           </td>
    </tr>
   <tr>
         <td>Lifetime (hours):</td>
         <td>
           <input  class="portlet-form-input-field" type="text" name="lifetime" value="100" size="30" maxlength="200" />*
         </td>
           <td>Description:</td>
           <td>
              <textarea name="desc"></textarea>
           </td>
    </tr>
    <tr>
      <td colspan=4 align=left>*: Cannot be left empty.</td>
    </tr>
    <tr><td colspan=4 align=center>
     <lpds:submit actionID="guse" actionValue="doDownload" cssClass="portlet-form-button" txt="button.download" tkey="true" />
     <lpds:submit actionID="guse" actionValue="doGoCredentialList" cssClass="portlet-form-button" txt="button.Back" tkey="true" />
    </td>
    </tr>
</table>
<br>
<b><i>Message:</i></b> <font color="990033">${msg}</font>
</form>
