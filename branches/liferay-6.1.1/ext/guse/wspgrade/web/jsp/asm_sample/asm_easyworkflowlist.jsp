<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/tooltip.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/graph.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/props/props.js"></script>
<link type="text/css" href="${pageContext.request.contextPath}/props/form.css" rel="stylesheet"/>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<portlet:renderURL var="rURL" />

<script>
<!-- 
//    user='root';
    workflow='${wrkdata.workflowID}';
    sjob='';
    var sForm=1;
    var portalID="${portalID}";
    var storageID="<msg:surl dest="storage" url="${storageID}" />";
    var userID="${userID}";
    var workflowID="${wrkdata.workflowID}";
    var confID="${confID}";
    var jobID="";
    var vJob="";
    var callflag=0;
    var fileUploadErrorFlag=0;
    var formid=0;    
//    var sid0=document.cookie.split("=");
//-->
 
</script>

<portlet:defineObjects/>

<portlet:actionURL var="pURL" action="doSubmit" portletMode="VIEW" />
<form method="post" action="${pURL}">
<input type="hidden" name="action" id="action" value="doSubmit">
<table>
	<tr>
		<td>
			Get workflows from :
		</td>
		<td>
			<select id="owner" name="owner">
			   <c:forEach var="own" items="${owners}">
				<option>
				 ${own}
				</option>
			   </c:forEach>
			</select>
		</td>
		<td>
			<lpds:submit actionID="action" actionValue="doGetWorkflowsFromRepository" cssClass="portlet-form-button" txt="Get Workflows!" tkey="true"/>
		</td>
	</tr>
</table>


<table>

<tr>
	<td>
		WorkflowId
	</td>
	
	<td>
		type
	</td>
	
</tr>
	    <c:forEach var="wf" items="${WorkflowList}">
    <tr>
        <td>
		<input type="radio" name="impItemId" value="${wf.id}" /> 

        </td>
	<td>
		${wf.workflowID}
        </td>
	<td>
		${wf.workflowType}
        </td>
	

    </tr>

	    </c:forEach>
		<td>
			<lpds:submit actionID="action" actionValue="doImportWorkflow" cssClass="portlet-form-button" txt="Import selected workflow!" tkey="true"/>
		</td>
</table>


<table>

<tr>
	<td>
		WorkflowId
	</td>
	
	<td>
		type
	</td>
	
</tr>
	    <c:forEach var="wf" items="${availablewfs}">
    <tr>
        <td>
		<input type="radio" name="concrete_wf_id" value="${wf.workflowID}" /> 

        </td>
	<td>
		${wf.workflowID}
        </td>
	<td>
		${wf.workflowType}
        </td>
	

    </tr>

	    </c:forEach>
		<td>
			<lpds:submit actionID="action" actionValue="doGetInput" cssClass="portlet-form-button" txt="Get Input Text for Selected Workflow!" tkey="true"/>
		</td>
</table>

<table>
	<tr>
		<td>
			<input type="text" id="cmd_line" name="cmd_line" value="${command_line_text}"/> 
		</td>
	</tr>
	<tr>	<td>
		<lpds:submit actionID="action" actionValue="doSetInput" cssClass="portlet-form-button" txt="Set Input Text for Selected Workflow!" tkey="true"/>
			

    </div> 
		</td>
	</tr>

</table>
<lpds:submit actionID="action" actionValue="doSubmit" cssClass="portlet-form-button" txt="submit!" tkey="true"/>



</form>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<portlet:defineObjects/>
<style type="text/css">
<!--
    @import '${pageContext.request.contextPath}/css/style.css';
-->
</style>

<jsp:useBean id="dt" class="java.util.Date" />


<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>
<msg:help id="helptext" tkey="help.easyworkflowlist" img="${pageContext.request.contextPath}/img/help.gif" />
	

<div id="rwlist" style="position:relative;">

<portlet:actionURL var="pURL" action="doSubmit" portletMode="VIEW" />
<%--<portlet:actionURL var="eURL" action="doExport" portletMode="VIEW" />--%>

<%--   <form method="post" action="${pURL}" onsubmit="javascript:alert(document.getElementById('action').value);">   --%>
 <form method="post" action="/download" name="upform" id="downloadform">				
    <input type="hidden" name="portalID"    id="portalID" value="${portalID}">
    <input type="hidden" name="userID"      id="userID" value="${userID}">
    <input type="hidden" name="workflowID"  id="workflowID" value="awkf.workflowID">
    <input type="hidden" name="downloadType" id="downloadType" value="inputs_$awkf.einstanceID">
    <input type="hidden" name="outputLogType" value="all">
 </form>
 
<form method="post" action="${pURL}" > 
<input type="hidden" name="workflow" id="workflow">
<input type="hidden" name="action" id="action" value="doSubmit">

<%-- doExport hivasi parameterek --%>
<input type="hidden" name="workflowID" id="e_workflowID">
<input type="hidden" name="storageID"  id="e_storageID">
<input type="hidden" name="wfsID"      id="e_wfsID">
<input type="hidden" name="exporttext" id="e_text">
<input type="hidden" name="typ"        id="e_typ">
<%-- doExport hivasi parameterek --%>


<div id="div_submit" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_submit_txt">
    <msg:getText key="text.easyworkflowlist.2" /> <div id="div_submit_workflow"></div> <msg:getText key="text.easyworkflowlist.3" /><br>
     <br /><msg:getText key="text.notify.wfchg.type" />:
     <select id="wfchg_type" name="wfchg_type" onchange="">
	<option value="" ${value_wfchg_type0}><msg:getText key="text.notify.wfchg.type.not" /></option>
	<option value="chg" ${value_wfchg_type1}><msg:getText key="text.notify.wfchg.type.chg" /></option>
	<option value="end" ${value_wfchg_type2}><msg:getText key="text.notify.wfchg.type.end" /></option>
     </select>
     <msg:toolTip id="ctmpdiv" tkey="portal.notify.wfchg.type" img="${pageContext.request.contextPath}/img/tooltip.gif" /><br />
    <lpds:submit  actionID="action" actionValue="doSubmit" cssClass="portlet-form-button" txt="button.submit" tkey="true" />
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_submit')" value="<msg:getText key="button.cancel" />" />
    </div>
</div>

<div id="div_delete" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_delete_txt">
    <msg:getText key="text.easyworkflowlist.0" /><div id="div_delete_workflow"></div> <msg:getText key="text.easyworkflowlist.1" /><br>
    <lpds:submit actionID="action" actionValue="doDelete" cssClass="portlet-form-button" txt="button.delete" tkey="true" />
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_delete')" value="<msg:getText key="button.cancel" />" />
    </div>
</div>

<div id="div_abort" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_abort_txt">
	<msg:getText key="text.easyworkflowlist.5" /> <div id="div_abort_workflow"></div> <msg:getText key="text.easyworkflowlist.6" /><br>
	<lpds:submit actionID="action" actionValue="doAbortAll" cssClass="portlet-form-button" txt="button.abortall" tkey="true"/>
	<input type="button" class="portlet-form-button" onclick="javascript:hide('div_abort')" value="<msg:getText key="button.cancel" />" />
    </div>
</div>

<div id="div_rescue" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_rescue_txt"><msg:getText key="text.easyworkflowlist.resume" /><br>
    <lpds:submit actionID="action" actionValue="doRescue" cssClass="portlet-form-button" txt="button.resume" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_rescue')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>

<div id="div_wait" class="shape" style="display:none;position:fixed;left:30%;top:40%" >
    <div class="hdn_txt" id="div_wait_txt">
	 <br><br><br><msg:getText key="please.wait" /><br><br><br>
    </div>
</div>

<input type="hidden" name="rtid" id="rtid">

<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0"  width="100%" >
    <tr>
    <td style="width:100%">

	<table width="100%" >
    	    <tr>
		<td class="portlet-section-body" width="22%"><msg:getText key="text.easyworkflowlist.name" /></td>
		<td class="portlet-section-body" width="5%"><msg:getText key="text.easyworkflowlist.status" /></td>
		<td class="portlet-section-body" width="55%"><msg:getText key="text.easyworkflowlist.action" /></td>
	    </tr>
	    <c:forEach var="awkf" items="${requestScope.appWorkflowList}">
	    <tr>
		<td class="portlet-section-body"><div class="bold">${awkf.workflowID}</div></td>
		
		<c:choose>
		    <c:when test="${awkf.einstance!=null}">
			 <c:choose>
			    <c:when test="${awkf.einstance.status==2}">
				<td class="submitted">submitted</td>
				<td class="portlet-section-body">
				    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
		    		</td>
			    </c:when>
			    <c:when test="${awkf.einstance.status==5}">
				<td class="running">running</td>
				<td class="portlet-section-body">
				    <input type="submit" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';document.getElementById('rtid').value='${awkf.einstanceID}';document.getElementById('action').value='doInstanceDetails';" value="<msg:getText key="button.shortdetails" />" />
				    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
				</td>
			    </c:when>
			    <c:when test="${awkf.einstance.status==6}">
				<td class="finished">finished</td>
				<td class="portlet-section-body">
		    		    <input type="submit" value="Configure" onclick="javascript:document.getElementById('div_wait').style.display='block';document.getElementById('workflow').value='${awkf.workflowID}';document.getElementById('action').value='doConfigure';" class="portlet-form-button"/>
	    		    	    <lpds:submit actionID="action" actionValue="doWorkflowInfo" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.info" tkey="true"/>
				    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_submit_workflow').innerHTML='${awkf.workflowID}';hide('div_submit','${awkf.workflowID}',event)" value="<msg:getText key="button.submit" />" />
				    <c:choose>
					<c:when test="${awkf.runningStatus==0 && awkf.submittedStatus==0}">	
					    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_workflow').innerHTML='${awkf.workflowID}';hide('div_delete','${awkf.workflowID}',event)" value="<msg:getText key="button.delete" />" />
					</c:when>
					<c:otherwise>	
					    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
					</c:otherwise>
				    </c:choose>
				    <input type="button" value="<msg:getText key="button.outputs" />" class="portlet-form-button" onclick="javascript:getout('<msg:surl dest='storage' url='${awkf.storageID}'/>','${awkf.workflowID}','${awkf.einstanceID}');">
				</td>
			    </c:when>
			    <c:when test="${awkf.einstance.status==23}">
				<td class="error">running/error</td>
				<td class="portlet-section-body">
				    <input type="submit" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';document.getElementById('rtid').value='${awkf.einstanceID}';document.getElementById('action').value='doInstanceDetails';" value="<msg:getText key="button.shortdetails" />" />
				    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
				</td>
			    </c:when>                                                        
			    <c:when test="${awkf.einstance.status==7}">
				<td class="error">error</td>
				<td class="portlet-section-body">
                                    <input type="submit" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';document.getElementById('rtid').value='${awkf.einstanceID}';document.getElementById('action').value='doInstanceDetails';" value="<msg:getText key="button.shortdetails" />" />
		    		    <input type="submit" value="Configure" onclick="javascript:document.getElementById('div_wait').style.display='block';document.getElementById('workflow').value='${awkf.workflowID}';document.getElementById('action').value='doConfigure';" class="portlet-form-button"/>
	    		    	    <lpds:submit actionID="action" actionValue="doWorkflowInfo" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.info" tkey="true"/>
				    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_rescue','${awkf.workflowID}',event)" value="<msg:getText key="button.resume" />" />		
				    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_submit_workflow').innerHTML='${awkf.workflowID}';hide('div_submit','${awkf.workflowID}',event)" value="<msg:getText key="button.submit" />" />
				    <c:choose>
					<c:when test="${awkf.runningStatus==0 && awkf.submittedStatus==0}">	
					    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_workflow').innerHTML='${awkf.workflowID}';hide('div_delete','${awkf.workflowID}',event)" value="<msg:getText key="button.delete" />" />
					</c:when>
					<c:otherwise>	
					    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
					</c:otherwise>
				    </c:choose>
				    
				</td>
			    </c:when>
			    <c:when test="${awkf.einstance.status==22}">
				<td class="error">suspended</td>
				<td class="portlet-section-body">
				    <input type="submit" value="Configure" onclick="javascript:document.getElementById('div_wait').style.display='block';document.getElementById('workflow').value='${awkf.workflowID}';document.getElementById('action').value='doConfigure';" class="portlet-form-button"/>
	    		    	    <lpds:submit actionID="action" actionValue="doWorkflowInfo" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.info" tkey="true"/>
				    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_rescue','${awkf.workflowID}',event)" value="<msg:getText key="button.resume" />" />		
				    <c:choose>
					<c:when test="${awkf.runningStatus==0 && awkf.submittedStatus==0}">	
					    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_workflow').innerHTML='${awkf.workflowID}';hide('div_delete','${awkf.workflowID}',event)" value="<msg:getText key="button.delete" />" />
					</c:when>
					<c:otherwise>	
					    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
					</c:otherwise>
				    </c:choose>
				    
				</td>
			    </c:when>
			    <c:when test="${awkf.einstance.status==37}">
				<td class="error">big workflow</td>
				<td class="portlet-section-body">
				    <input type="submit" value="Configure" onclick="javascript:document.getElementById('div_wait').style.display='block';document.getElementById('workflow').value='${awkf.workflowID}';document.getElementById('action').value='doConfigure';" class="portlet-form-button"/>
	    		    	    <lpds:submit actionID="action" actionValue="doWorkflowInfo" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.info" tkey="true"/>
				    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_workflow').innerHTML='${awkf.workflowID}';hide('div_delete','${awkf.workflowID}',event)" value="<msg:getText key="button.delete" />" />
            			</td>
			    </c:when>
			    <c:otherwise>
				<td class="portlet-section-body"><div>init ${awkf.einstance.status==22}</div></td>
				<td class="portlet-section-body">
				    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
				</td>
			    </c:otherwise>					    
			 </c:choose>
		    </c:when>
		    <c:otherwise>
		    <td class="portlet-section-body">--</td>
		     <td class="portlet-section-body">
			<input type="submit" value="Configure" onclick="javascript:document.getElementById('div_wait').style.display='block';document.getElementById('workflow').value='${awkf.workflowID}';document.getElementById('action').value='doConfigure';" class="portlet-form-button"/>
		        <lpds:submit actionID="action" actionValue="doWorkflowInfo" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.info" tkey="true"/>
			<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_submit_workflow').innerHTML='${awkf.workflowID}';hide('div_submit','${awkf.workflowID}',event)" value="<msg:getText key="button.submit" />" />
			<c:choose>
			    <c:when test="${awkf.runningStatus==0 && awkf.submittedStatus==0}">	
				<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_workflow').innerHTML='${awkf.workflowID}';hide('div_delete','${awkf.workflowID}',event)" value="<msg:getText key="button.delete" />" />
			    </c:when>
			    <c:otherwise>	
				<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
			    </c:otherwise>
			</c:choose>
		     </td>
		    </c:otherwise>	 
		</c:choose>
    		
		
	
		
	    </tr>
	    <tr>
		<td class="portlet-section-body" colspan="6" style="border-bottom:2px solid">&nbsp;&nbsp;${awkf.txt}</td>    
	    </tr>

	    </c:forEach>

	</table>

    </td>
    </tr>

	<lpds:submit actionID="action" actionValue="doList" cssClass="portlet-form-button" txt="button.refresh" tkey="true"/>
	
    <tr>
    <td style="width:100%">
	<table class="padding-top:15px;">
    	    <tr>    
        	<td class="portlet-section-body"><div class="bold"><msg:getText key="text.global.0" />: </div></td>
        	<td class="portlet-section-body"><div id="msg"><msg:getText key="${msg}" /></div></td>
    	    </tr>
	</table>
    </td>
    </tr>

</form>
    
</table>

</div>
