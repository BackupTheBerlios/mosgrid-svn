<!-- workflow feltoltese -->
<jsp:include page="/jsp/core.jsp" />

<%@ page contentType="text/html" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<%@ page session="false" %>
<%@ page contentType="text/html" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<portlet:defineObjects/>
<portlet:actionURL var="pURL" />
<portlet:resourceURL var="rURL" />






<%/*<portletAPI:init/>*/%>

<script type="text/javascript">
    function wfup_jenabled(checkboxid, textid)
    {
        if(document.getElementById(checkboxid).checked)
        {
            document.getElementById(textid).value = "";
            document.getElementById(textid).disabled = false;
        }
        else
        {
            document.getElementById(textid).value = "";
            document.getElementById(textid).disabled = true;
        }
    }

    function getLocalUploadStatus()
    {
        var url="${rURL}";
        request=GetXmlHttpObject(setLocalUploadStatus);
        request.open("POST",url, false);
        request.send("");
    }
    function setLocalUploadStatus()
    {
        try
        {
            if((request.readyState == 4)&&(request.status == 200))
            {
                var resp =  request.responseText;
                if (resp != null)
                {
                    document.getElementById("fileupload_progress").innerHTML=resp;
                }
            }
        }
        catch (err) { alert("It does not appear that the server is available for this application. Please try again very soon. \nError: "+err.message);}
        setTimeout("getLocalUploadStatus()", 1000);
    }

</script>

<portlet:defineObjects/>
<portlet:actionURL var = "pURL"/>

<table class="portlet-pane" cellspacing="1" cellpadding="1" border="0" width="100%" >
<tr><td>

    <c:if test="${quotafull=='true'}">		
	<table>
	    <tr><td>
		<msg:getText key="text.wfupload.notavailable" />
    	    </td><td>
	</table>
    </c:if>

    <c:if test="${quotafull!='true'}">
<!-- the upload form -->

    <form method="POST" action="${pURL}" enctype="multipart/form-data">
	<table>
<!--
    	    <tr>
        	<td>
		    <msg:getText key="text.wfupload.wfitype" />:
        	</td>
        	<td>
		    <select name="wfiType" id="wfiTypeID" onchange="">
			<option value="zen" Zen><msg:getText key="text.wfupload.zen" /></option>
			<option value="dag" DAG><msg:getText key="text.wfupload.dag" /></option>
		    </select>
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfupload.wfitype" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</td>
    	    </tr>
-->
	    <br />
            <tr>
                <td>
		    <span dir="ltr"  class="portlet-msg-info"><msg:getText key="text.wfupload.0" /> : </span>
                </td>
                <td>
                    <input type="file" name="fileupload_upload" />
                    <input type="submit" name="Submit" value="<msg:getText key="button.upload" />" onclick="getLocalUploadStatus();">
                </td>
                <td>
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfupload.upfilename" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>
        </table>

        <table>
            <tr>
                <td>
            	    <msg:getText key="text.wfupload.1" />:
                </td>
            </tr>
        </table>
		    
        <table border="1" width="410px">
        <tr>
            <td class="portlet-section-body">

            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfupload.2" /> :
                </td> 
                <td class="portlet-section-body">            
                    <input id="wfup_grafName" type="text" name="newGrafName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfup_grafCheckBox" type="checkbox" value="OFF" onclick="wfup_jenabled('wfup_grafCheckBox', 'wfup_grafName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfupload.graf" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>
            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfupload.3" /> : 
                </td>                 
                <td class="portlet-section-body">
                    <input id="wfup_abstName" type="text" name="newAbstName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfup_abstCheckBox" type="checkbox" value="OFF" onclick="wfup_jenabled('wfup_abstCheckBox', 'wfup_abstName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfupload.abst" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>
            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfupload.4" /> : 
                </td>                 
                <td class="portlet-section-body">
                    <input id="wfup_realName" type="text" name="newRealName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfup_realCheckBox" type="checkbox" value="OFF" onclick="wfup_jenabled('wfup_realCheckBox', 'wfup_realName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfupload.real" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>

            </td>
        </tr>
	</table>

    </form>

    </c:if>

<tr><td>
</table>

<%-- file upload progress bar --%>

<b><msg:getText key="text.global.0" />: </b>
<div id="fileupload_progress">
    <c:choose>
        <c:when test="${full!=null}"><msg:getText key="${full}" /></c:when>
        <c:otherwise>&nbsp;</c:otherwise>
    </c:choose>
</div>
<div id="progressBar" style="display: none;">
  <div id="progressBarBoxContent"></div>
</div>
