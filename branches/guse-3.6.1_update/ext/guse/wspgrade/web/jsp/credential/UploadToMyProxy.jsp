<%-- 
    Document   : UploadtoMyProxy
    Created on : 2011.09.06., 15:36:15
    Author     : akos balasko
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

 <span  dir="ltr"  class="portlet-msg-info">
                        MyProxy upload properties:
                    </span>
                    <table width="100%">
                        <tr>
                            <td  style="width:15%">
                                <label>Host name:</label>
                            </td>
                            <td  style="width:35%">
                                <input  class="portlet-form-input-field" type="text" name="host" value="" size="30" maxlength="200" />*
                            </td>
                            <td  style="width:15%">
                                <label>Port:</label>
                            </td>
                            <td  style="width:35%">
                                <input  class="portlet-form-input-field" type="text" name="port" value="7512" size="30" maxlength="200" />*
                            </td>
                        </tr>
                        <tr>
                            <td  style="width:15%">
                                <label>Login:</label>
                            </td>
                            <td  style="width:35%">
                                <input  class="portlet-form-input-field" type="text" name="login" value="" size="20" maxlength="200" />*,**
                            </td>
                            <td  style="width:15%">
                                <label>Password:</label>
                            </td>
                            <td  style="width:35%">
                                <input  class="portlet-form-input-field" type="password" name="pass" size="20" maxlength="200" />*,**
                            </td>
                        </tr>
                        <tr>
                            <td  style="width:15%">
                                <label>Lifetime (hours)</label>
                            </td>
                            <td  style="width:35%">
                                <input  class="portlet-form-input-field" type="text" name="lifetime" value="10" size="30" maxlength="200" />*
                            </td>
                            <td  style="width:25%">
                                <label>Use DN as login</label>
                            </td>
                            <td  style="width:25%">
                                <label>
                                    <input  class="portlet-form-field" type='checkbox' name='dnlogin' value='dnlogin'/>**
                                </label>
                            </td>
                            <td  style="width:25%">
                                <label>RFC format</label>
                            </td>
                            <td  style="width:25%">
                                <label>
                                    <input  class="portlet-form-field" type='checkbox' name='RFCEnabled' value='RFCEnabled'/>
                                </label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan=4 align=left>
                                <label>*: Cannot be left empty.<br>
                                    **: Either <i>Login</i> and <i>Password</i> <b>or</b> <i>Use DN as login</i> must be set.</label>
                            </td>
                        </tr>
                        <tr>
                        </tr>
                    </table>
