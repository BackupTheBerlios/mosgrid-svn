<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="java.io.File" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURL">
    <portlet:param value="doUpload" name="guse"/>
</portlet:actionURL>
<portlet:actionURL var = "pURLback">
    <portlet:param value="doGoCredentialList" name="guse"/>
</portlet:actionURL>

<script language="javascript" type="text/javascript">
    function rbtnChecked(val){
        var rbtn = val;
        //var divs = ["SAMLdiv", "bSAM", "nSAM", "P12div", "bP12", "pP12","PEMdiv", "kPEM", "pPEM", "cPEM","MyProxyP12PEM", "Appletdiv" ];
        var divs = ["P12div", "bP12", "pP12","PEMdiv", "kPEM", "pPEM", "cPEM","MyProxyP12PEM" ];

        if(rbtn.toString().substring(0, 3) == "SAM"){
            divVis(divs, "SAM");

        }
        else if(rbtn.toString().substring(0, 3) == "P12"){
            divVis(divs, "P12");
        }else if(rbtn.toString().substring(0, 3) == "PEM"){
            divVis(divs, "PEM");
        }
        document.getElementById("BeforeChoosediv").style.display = "none";
        document.getElementById("AfterChoosediv").style.display = "block";
    }

    function divVis(arr, id){
        var divArr = arr;
        var divId = id;

        for(var i = 0; i < divArr.length; ++i){
            if(divArr[i].indexOf(divId, 0) != -1){
                document.getElementById(divArr[i]).style.display = "block";
            }else{
                document.getElementById(divArr[i]).style.display = "none";
            }
        }
    }

    function showApplet(){
        document.getElementById("Appletdiv").style.display = "block";
        document.getElementById("nSAM").style.display = "none";
    }

    function help(val){
        if(val.value == "Help"){
            document.getElementById("Helpdiv").style.display = "block";
            document.getElementById("HelpBtn").value="Hide";
        }
        else if (val.value == "Hide"){
            document.getElementById("Helpdiv").style.display = "none";
            document.getElementById("HelpBtn").value="Help";
        }

    }

    function closeApplet(){
            document.getElementById("Appletdiv").style.display = "none";
    }


</script>


<form method="post" action="${pURLback}" >
    <input type="hidden" name="guse" id="guse" value="doUpload">
    <lpds:submit actionID="guse" actionValue="doGoCredentialList" cssClass="portlet-form-button" txt="button.back" tkey="true" />
    <%--<div id="Helpdiv" style ="display: none;">
        <jsp:include page="Help.html"/>
    </div>
    <input type="button" id="HelpBtn" name="Help" style="float: right" onclick="help(this);" value ="Help"/>--%>
</form>
<br />

<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0"  width="100%" >
    <tr valign="top">
        <td style="width:100%">
            <form method="post" action="${pURL}" enctype="multipart/form-data" name="dataForm">

                <div id="BeforeChoosediv" style="background-color: white" style="width:100%;" colspan="4">
                    <span  dir="ltr"  class="portlet-msg-info">
                        Select authentication type
                    </span>
                </div>
                <div id="AfterChoosediv" style="background-color: white" style="display: block; width: 100%;" colspan="4">
                    <span  dir="ltr"  class="portlet-msg-info">
                        Selected authentication type
                    </span>
                </div>
                <br />
                <%--<div id="SAMLdiv" style ="display: block; width: 100%;">
                    <div style ="width: 15%; float: left" align="left">
                        <input type="radio" id="SAML" name="auth" value="SAML"
                               onclick="rbtnChecked(this.value)"/>SAML Assertion
                    </div>
                    <div id="bSAM" style ="width: 30%; float: left; display: none" align="right">
                        <label>Browse assertion:</label>
                        <input  class="portlet-form-button" type="file" name="samlFile" />
                    </div>
                    <div id="nSAM" style ="width: 25%; float: left; display: none" align="right">
                        <label>New assertion:</label>
                        <input type="button" id="GenerateBtn" name="Generate" onclick="showApplet();" value ="Generate"/>
                    </div>
                </div>
                <!--br /-->
                <div id="Appletdiv" style ="display: none; width: 100%; float: left">
                    <jsp:include page="ETDAppletGUI.html" />
                    <input type="button" id="CloseBtn" style="float: left;" name="Close" onclick="closeApplet();" value ="Close Applet"/>
                </div>--%>
                <div id="P12div" style ="display: block; width: 100%">
                    <div style ="width: 15%; float: left" align="left">
                        <input type="radio" id="P12" name="auth" value="P12"
                               onclick="rbtnChecked(this.value)"/>PKCS12 Certificate
                    </div>
                    <div id="bP12" style ="width: 30%; float: left; display: none;" align="right">
                        <label>Browse   PKCS12:</label>
                        <input  class="portlet-form-button" type="file" name="p12File" />
                    </div>
                    <div id="pP12" style ="width: 25%; float: left; display: none;" align="right">
                        <label>Passphrase:</label>
                        <input  class="portlet-form-button" type="password" name="p12FilePass" />
                    </div>
                </div>
                <!--br /-->
                <div id="PEMdiv" style="display: block; width: 100%">
                    <div style ="width: 15%; float: left" align="left">
                        <input type="radio" id="PEM" name="auth" value="PEM"
                               onclick="rbtnChecked(this.value)"/>PEM key & certificate
                    </div>
                    <div id="kPEM" style ="width: 30%; float: left; display: none" align="right">
                        <label>Key:</label>
                        <input  class="portlet-form-button"  type="file" name="keyFile" />
                    </div>
                    <div id="pPEM" style ="width: 25%; float: left; display: none" align="right">
                        <label>Passphrase:</label>
                        <input  class="portlet-form-button" type="password" name="keyFilePass" />
                    </div>
                    <div id="cPEM" style ="width: 30%; float: left; display: none" align="center">
                        <label>Cert:</label>
                        <input  class="portlet-form-button" type="file" name="certFile" />
                    </div>
                </div>
                <br />
                <br />
                <div id="MyProxyP12PEM" style="display: none">
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
                                    <input  class="portlet-form-field" type='checkbox' name='RFCEnabled' value='RFCEnabled'/>**
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
                </div>
                <br />
                <br />
                <div style="float: left">
                    <lpds:submit actionID="guse" actionValue="doUpload" cssClass="portlet-form-button" txt="button.upload" tkey="true" />
                </div>
            </form>
        </td>
    </tr>
    <tr valign="top">
        <td style="width:100%">
            <table class="portlet-frame">
                <tr><td>
                        <b><i>Message:</i></b> <font color="990033">${msg}</font>
                    </td></tr>
            </table>
        </td>
    </tr>
</table>
