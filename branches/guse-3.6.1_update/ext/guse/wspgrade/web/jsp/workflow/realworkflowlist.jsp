<jsp:include page="/jsp/core.jsp" />
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<portlet:defineObjects/>

<jsp:useBean id="dt" class="java.util.Date" />


<msg:help id="helptext" tkey="help.realworkflowlist" img="${pageContext.request.contextPath}/img/help.gif" />
	
<script>
popUP_OK="<msg:getText key="text.io.14.yes" />";
popUP_NO="<msg:getText key="text.io.14.no" />";

function submitText(pWorkflow,pURL){
    var content="<form method=\"post\" action=\""+pURL+"\">";
    content=content+"<msg:getText key="text.realworkflowlist.2" /> "+pWorkflow+" <msg:getText key="text.realworkflowlist.3" /><br />";
    content=content+"<msg:getText key="text.realworkflowlist.4" /><input type=\"text\" name=\"submittext\" size=\"30\" value=\"${dt.year+1900}-${dt.month+1}-${dt.date} ${dt.hours}:${dt.minutes}\"/><br />";
    content=content+"<msg:getText key="text.notify.wfchg.type" />:";
    content=content+"<select id=\"wfchg_type\" name=\"wfchg_type\">";
    content=content+"<option value=\"\" ${value_wfchg_type0}><msg:getText key="text.notify.wfchg.type.not" /></option>";
    content=content+"<option value=\"chg\" ${value_wfchg_type1}><msg:getText key="text.notify.wfchg.type.chg" /></option>";
    content=content+"<option value=\"end\" ${value_wfchg_type2}><msg:getText key="text.notify.wfchg.type.end" /></option>";
    content=content+"</select>";
    
    content =content+"<input type=\"image\" style=\"width:70px;float:left;display:block;text-align:center;\" src=\""+webapp+"/imgs/accept_64.png\" value=\""+popUP_OK+">";
    content=content+"<img src=\""+webapp+"/imgs/accept_64.png\" /><br/>";
    content=content+"<a href=\"#\" style=\"width:70px;float:left;display:block;text-align:center;\" onclick=\"TINY.box.hide();\">";
    content=content+"<img src=\""+webapp+"/imgs/remove_64.png\" /><br/>";content=content+"</a>";

    content=content+"</form>";
    return content;
}
</script>

<div id="rwlist" style="position:relative;">

<portlet:actionURL var="pURL" portletMode="VIEW" />

<form method="post" action="${pURL}"> 
<input type="hidden" name="workflow" id="workflow">
<input type="hidden" name="guse" id="action" value="doSubmit">

<%-- doExport hivasi parameterek --%>
<input type="hidden" name="workflowID" id="e_workflowID">
<input type="hidden" name="storageID"  id="e_storageID">
<input type="hidden" name="wfsID"      id="e_wfsID">
<input type="hidden" name="exporttext" id="e_text">
<input type="hidden" name="typ"        id="e_typ">
<%-- doExport hivasi parameterek --%>


<div id="div_submit" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_submit_txt">
    </div>
</div>

<div id="div_deleteallinstance" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_deleteallinstance_txt">
    <msg:getText key="text.realworkflowlist.0" /><div id="div_delete_workflow_allinstance"></div> <msg:getText key="text.realworkflowlist.1" /><br>
    <lpds:submit actionID="action" actionValue="doDeleteAllInstance" cssClass="portlet-form-button" txt="button.delete" tkey="true" />
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_deleteallinstance')" value="<msg:getText key="button.cancel" />" />
    </div>
</div>
		

<div id="div_abort" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_abort_txt">
	<msg:getText key="text.realworkflowlist.5" /> <div id="div_abort_workflow"></div> <msg:getText key="text.realworkflowlist.6" /><br>
	<lpds:submit actionID="action" actionValue="doAbortAll" cssClass="portlet-form-button" txt="button.abortall" tkey="true"/>
	<input type="button" class="portlet-form-button" onclick="javascript:hide('div_abort')" value="<msg:getText key="button.cancel" />" />
    </div>
</div>



<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0"  width="100%" >
    <tr>
    <td style="width:100%">

	<table width="100%" >
    	    <tr>
		<td class="portlet-section-body" width="20%"><msg:getText key="text.realworkflowlist.name" /></td>
		<td class="portlet-section-body" width="10%"><msg:getText key="text.realworkflowlist.type" /></td>
		<td class="portlet-section-body" width="5%"><msg:getText key="text.realworkflowlist.submit" /></td>
		<td class="portlet-section-body" width="5%"><msg:getText key="text.realworkflowlist.run" /></td>
		<td class="portlet-section-body" width="5%"><msg:getText key="text.realworkflowlist.finish" /></td>
		<td class="portlet-section-body" width="5%"><msg:getText key="text.realworkflowlist.error" /></td>
		<td class="portlet-section-body" width="5%"><msg:getText key="text.realworkflowlist.suspended" /></td>
		<td class="portlet-section-body" width="50%"><center><msg:getText key="text.realworkflowlist.action" /></center></td>
	    </tr>
	    <c:forEach var="awkf" items="${requestScope.rWorkflowList}">
	    <tr>
		<td class="portlet-section-body"><div class="bold"><c:out value="${awkf.workflowID}" escapeXml="true" /></div></td>
		<td class="portlet-section-body"><div class="bold">${awkf.workflowType}</div></td>
		<td class="portlet-section-body"><div class="submitted">${awkf.submittedStatus}</div></td>
		<td class="portlet-section-body"><div class="running">${awkf.runningStatus}</div></td>
		<td class="portlet-section-body"><div class="finished">${awkf.finishedStatus}</div></td>
		<td class="portlet-section-body"><div class="error">${awkf.errorStatus}</div></td>
		<td class="portlet-section-body"><div class="suspended">${awkf.suspendStatus}</div></td>
                
            <c:choose>
            <c:when test="${awkf.workflowType=='zen'}">                
                
		<td class="portlet-section-body">

<!--
{"incompleted","init","submitted","waiting","scheduled","running","finished","error","hold","migrating","term is false"}
-->			
		    <lpds:submit actionID="action" actionValue="doConfigure" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.configure" tkey="true"/>
		    <lpds:submit actionID="action" actionValue="doWorkflowInfo" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.info" tkey="true"/>
		    <lpds:submit actionID="action" actionValue="doDetails" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.details" tkey="true"/>
            <portlet:actionURL var="submitURL">
                <portlet:param name="guse" value="doSubmit" />
                <portlet:param name="workflow" value="${awkf.workflowID}" />
            </portlet:actionURL>
		    <input type="button" class="portlet-form-button" value="<msg:getText key="button.submit" />" onclick="TINY.box.show(submitText('${awkf.workflowID}','${submitURL}'),0,300,300,1);" />
		    <c:choose>
			<c:when test="${awkf.runningStatus==0 && awkf.submittedStatus==0}">	
                <portlet:actionURL var="pDURL">
                    <portlet:param name="guse" value="doDelete" />
                    <portlet:param name="workflow" value="${awkf.workflowID}" />
                </portlet:actionURL>
                    <input type="button" class="portlet-form-button"  value="<msg:getText key="button.delete" />"
                    onclick="TINY.box.show(popUPLink('${pDURL}','    <msg:getText key="text.realworkflowlist.0" /> ${awkf.workflowID} <msg:getText key="text.realworkflowlist.1" /><br>'),0,300,300,1);" />
			</c:when>
			<c:otherwise>	
			    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${awkf.workflowID}';hide('div_abort','${awkf.workflowID}',event)" value="<msg:getText key="button.abortall" />" />
			</c:otherwise>
		    </c:choose>
<!--
		    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_workflow_allinstance').innerHTML='${awkf.workflowID}';hide('div_deleteallinstance','${awkf.workflowID}',event)" value="<msg:getText key="button.deleteallinstance" />" />
-->
		    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('workflow_${awkf.workflowID}').style.display='block';" value="<msg:getText key="button.export" />" />
				
		</td>
	    </tr>

	    <tr>
		<td colspan="8">

		    <div id="workflow_${awkf.workflowID}" style="display:none">
			    <table border="1" align="center">
			    <tr>
				<td><msg:getText key="text.realworkflowlist.etype" /></td>
				<td>
				    <select id="exporttype_${awkf.workflowID}">
					<option value="appl"><msg:getText key="text.realworkflowlist.application" /></option>
					<option value="proj"><msg:getText key="text.realworkflowlist.project" /></option>
					<option value="work"><msg:getText key="text.realworkflowlist.concrete" /></option>
				    </select>
				    <msg:toolTip id="ctmpdiv" tkey="portal.realwflist.exporttype" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>    
			    <tr>
				<td><msg:getText key="text.realworkflowlist.edesc" /></td>
				<td><textarea cols="40" id="exporttext_${awkf.workflowID}"></textarea>
				    <msg:toolTip id="ctmpdiv" tkey="portal.realwflist.exportdesc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>    
			    <tr>
			        <td colspan="2">
				    <input type="button" onclick="javascript:document.getElementById('workflow_${awkf.workflowID}').style.display='none';document.getElementById('ex_${awkf.workflowID}').disabled=false;" value="<msg:getText key="button.cancel" />">
				    <input type="submit" value="<msg:getText key="button.send" />" onclick="javascript:document.getElementById('e_text').value=document.getElementById('exporttext_${awkf.workflowID}').value;document.getElementById('e_typ').value=document.getElementById('exporttype_${awkf.workflowID}').value;document.getElementById('action').value='doExport';document.getElementById('e_workflowID').value='${awkf.workflowID}';document.getElementById('e_storageID').value='${awkf.storageID}';document.getElementById('e_wfsID').value='${awkf.wfsID}';">
				</td>
			    </tr> 
			    </table>   
		
		    </div>

		</td>
	    </tr>
            
            </c:when>
            <c:when test="">
            </c:when>
            <c:otherwise>
                <!-- ETICS -->
		<td class="portlet-section-body">			
		    <lpds:submit actionID="action" actionValue="doConfigure" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.configure" tkey="true"/>
		    <lpds:submit actionID="action" actionValue="doExportEtics" paramID="workflow" paramValue="${awkf.workflowID}" cssClass="portlet-form-button" txt="button.export" tkey="true"/>
		    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_workflow').innerHTML='${awkf.workflowID}';hide('div_delete','${awkf.workflowID}',event)" value="<msg:getText key="button.delete" />" />
		</td>                
            </c:otherwise>
            </c:choose>
 
 	    <tr>
		<td class="portlet-section-body" colspan="8" style="border-bottom:2px solid">&nbsp;&nbsp;<c:out value="${awkf.txt}" escapeXml="true" /></td>
	    </tr>
 
	    </c:forEach>

	</table>

    </td>
    </tr>

	<lpds:submit actionID="action" actionValue="doALLSubmit" cssClass="portlet-form-button" txt="button.submitall" tkey="true"/>
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
