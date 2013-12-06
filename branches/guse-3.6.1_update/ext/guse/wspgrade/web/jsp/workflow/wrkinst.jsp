<!--
Workfflow details main page
-->
<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<portlet:resourceURL var="ajaxURL" />
<script>
    var ajaxURL="${ajaxURL}";
    var loadingconf_txt='<msg:getText key="portal.config.loadingconfig" />';
</script>

<msg:help id="helptext" tkey="help.wrkinst" img="${pageContext.request.contextPath}/img/help.gif" />

<div id="rwlist" style="position:relative;">

<script>
function getConfForm(pParam){
        comParams=pParam;
//kommunikacio

	var url= ajaxURL+'&'+pParam;
	request=GetXmlHttpObject(openConf);
	request.open("POST",url, true);
	request.send("");
}

function openConf(){
try{
        if((request.readyState == 4)&&(request.status == 200)){
            var resp =  request.responseText;
	    
	    var opts=resp.split("<!-- div id -->");
            if(opts.length==2){
    		document.getElementById(opts[0]).innerHTML=opts[1];
                ReRunJS(opts[0]);
            }
            else{
            	    document.getElementById("popup1").innerHTML=resp;
                ReRunJS("popup1");
            }
        } 
//	else {alert("A problem occurred with communicating between the XMLHttpRequest object and the server program.");}
    } 
    catch (err) { alert("It does not appear that the server is available for this application. Please try again very soon. \nError: "+err.message);}
}


</script>


<portlet:defineObjects/>
<portlet:actionURL var="pURL" portletMode="VIEW" />
<form method="post" action="${pURL}"> 
    <input type="hidden" name="workflow" id="workflow" value="${workflow.workflowID}">
    <input type="hidden" name="guse" id="action">
    <input type="hidden" name="rtid" id="rtid">    

<div id="div_abort" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_abort_txt">Do you really suspend <c:out value="${workflow.workflowID}" escapeXml="true"  /> workflow <div id="div_abort_instance"></div> instance?<br>
    <lpds:submit actionID="action" actionValue="doAbort" cssClass="portlet-form-button" txt="button.suspend" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_abort')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>

<div id="div_delete" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_delete_txt">Do you really delete <c:out value="${workflow.workflowID}" escapeXml="true"  /> workflow <div id="div_delete_instance"></div> instance?<br>
    <lpds:submit actionID="action" actionValue="doDeleteInstance" cssClass="portlet-form-button" txt="button.delete" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_delete')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>

<div id="div_submit" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_submit_txt">Do you really ReSubmit <c:out value="${workflow.workflowID}" escapeXml="true"  /> workflow <div id="div_submit_instance"></div> instance?<br>
    <lpds:submit actionID="action" actionValue="doReSubmit" cssClass="portlet-form-button" txt="button.submit" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_submit')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>

<div id="div_rescue" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_rescue_txt">Do you really resume <c:out value="${workflow.workflowID}" escapeXml="true"  /> workflow <div id="div_rescue_instance"></div> instance?<br>
    <lpds:submit actionID="action" actionValue="doRescue" paramID="workflow" paramValue="${workflow.workflowID}" cssClass="portlet-form-button" txt="button.resume" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_rescue')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>

<table class="portlet-pane" cellspacing="1" cellpadding="1" border="0" width="100%" >
<tr><td>
    <lpds:submit actionID="action" actionValue="doList" cssClass="portlet-form-button" txt="button.back" tkey="true" />		
    <lpds:submit actionID="action" actionValue="doDetails" paramID="workflow" paramValue="${workflow.workflowID}" cssClass="portlet-form-button" txt="button.refreshinstances" tkey="true"/>
    <table width="100%" class="kback">
	<tr><td width="10%"><msg:getText key="text.wrkinst.0" />: </td><td><c:out value="${workflow.workflowID}" escapeXml="true"  /></td></tr>
	<tr><td width="10%"><msg:getText key="text.wrkinst.1" />: </td><td><c:out value="${workflow.txt}" escapeXml="true"  /></td></tr>
	<tr><td width="10%"><msg:getText key="text.wrkinst.2" />: </td><td><c:out value="${workflow.graf}" default="--" escapeXml="true" /></td></tr>
	<tr><td width="10%"><msg:getText key="text.wrkinst.3" />: </td><td><c:out value="${workflow.template}" default="--" escapeXml="true" /></td></tr>
    </table>
    
    <table width="100%" class="kback">
	<c:forEach var="onewrkinst" items="${workflow.allRuntimeInstance}" varStatus="ln">
	<c:if test="${onewrkinst.value.text!='null'}">
 	    <c:choose>
		<c:when test="${(ln.index%2)==1}">
	    	    <c:set var="color" value="kline1" />
		</c:when>
		<c:otherwise>
	    	    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>
	    
    	    <tr>
		<td class="${color}"><c:out value="${onewrkinst.value.text}" escapeXml="true"  /></td>
		<td class="<msg:getText key="portal.WorkflowData.status.${onewrkinst.value.status}" />"><msg:getText key="portal.WorkflowData.status.${onewrkinst.value.status}" /></td>
		<td class="${color}">
                    <c:if test="${onewrkinst.value.status!='37'}">
                        <input type="submit" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${workflow.workflowID}';document.getElementById('rtid').value='${onewrkinst.key}';document.getElementById('action').value='doInstanceDetails';" value="<msg:getText key="button.shortdetails" />" />
                    </c:if>
		    <c:if test="${onewrkinst.value.status=='5'||onewrkinst.value.status=='2'||onewrkinst.value.status=='1'||onewrkinst.value.status=='23'}">
			<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_abort_instance').innerHTML='${onewrkinst.value.text}';document.getElementById('rtid').value='${onewrkinst.key}';hide('div_abort','${workflow.workflowID}',event)" value="<msg:getText key="button.suspend" />" />		
		    </c:if>
		    <c:if test="${onewrkinst.value.status=='7'||onewrkinst.value.status=='28'}">
			<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_rescue_instance').innerHTML='${onewrkinst.value.text}';document.getElementById('rtid').value='${onewrkinst.key}';hide('div_rescue','${workflow.workflowID}',event)" value="<msg:getText key="button.resume" />" />		
		    </c:if>
		    <c:if test="${onewrkinst.value.status=='6' || onewrkinst.value.status=='7'||onewrkinst.value.status=='28'||onewrkinst.value.status=='37'}">
			<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_instance').innerHTML='${onewrkinst.value.text}';document.getElementById('rtid').value='${onewrkinst.key}';hide('div_delete','${workflow.workflowID}',event)" value="<msg:getText key="button.delete" />" />		
		    </c:if>
		</td>
    	    </tr>
	</c:if>
	</c:forEach>    
</form>
    </table>

    <table width="100%" class="kback">
        <tr>
	    
	    <td width="100%" colspan="4" style="border-bottom:solid 1px #ffffff;">
		<b><msg:getText key="text.wrkinst.instancename" /></b> ${rtid}</td>
	</tr>	    

        <tr>
	    <td width="20%"><msg:getText key="text.wrkinst.5" /></td>
	    <td width="10%"><msg:getText key="text.wrkinst.6" /></td>
	    <td width="20%"><msg:getText key="text.wrkinst.7" /></td>
	    <td width="50%">[ <msg:getText key="text.wrkinst.8" /> ]</td>	
	</tr>	    
	<c:forEach var="ajob" items="${instJobList}" varStatus="ln">
	    <c:choose>
		<c:when test="${(ln.index%2)==1}">
	    	    <c:set var="color" value="kline1" />
		</c:when>
		<c:otherwise>
	    	    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>

		<tr>
		    <td class="${color}"> ${ajob.key} </td>
		    <td colspan="2"  class="${color}">
			<table width="100%" class="kback">
			<c:forEach var="bjob" items="${ajob.value}">
			    <c:if test="${bjob.value>0}">
			    <tr>    
				<td class="<msg:getText key="portal.WorkflowData.status.${bjob.key}" />" width="25%"> <msg:getText key="portal.WorkflowData.status.${bjob.key}" /></td>
				<td class="${color}" width="25%">${bjob.value}</td>
                                <td class="${color}" width="50%"><input type="button" class="portlet-form-button" id="jobbutton_${ajob.key}${bjob.key}" value="View <msg:getText key="portal.WorkflowData.status.${bjob.key}" />" onclick="javascript:hidejobstatus('${ajob.key}','${bjob.key}')"></td>                                
			    </tr>
			    </c:if>
			</c:forEach>
			</table>
		    </td>
		    <td class="${color}">
                        <input type="button" class="portlet-form-button" id="jobbutton_${ajob.key}all" value="View all content(s)" onclick="javascript:hidejobstatus('${ajob.key}','all')">
                    </td>
    		</tr>
		<tr>
		    <td colspan="4">
			<div id="jobinsttatus_${ajob.key}"></div>
		    </td>
		</tr>
		<tr>
		    <td colspan="4">
			<div id="jobinsttatus0_${ajob.key}" style="display:none;background:#ffffff; border-left:#3e7abd solid 100px;">
			    <iframe name="formout_${ajob.key}" id="formout_${ajob.key}" scrolling="auto" frameborder="0" width="100%" height="auto"></IFRAME><br>
			    <input type="button" class="portlet-form-button" value="<msg:getText key="button.close" />" onclick="document.getElementById('jobinsttatus0_${ajob.key}').style.display='none'">
			</div>
		    </td>
		</tr>
	</c:forEach>    
    </table>

</td></tr>
</table>    

</div>
