

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

<portlet:actionURL var = "pURL"/>
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
			


		</td>
	</tr>

</table>
<lpds:submit actionID="action" actionValue="doSubmit" cssClass="portlet-form-button" txt="submit!" tkey="true"/>



</form>


