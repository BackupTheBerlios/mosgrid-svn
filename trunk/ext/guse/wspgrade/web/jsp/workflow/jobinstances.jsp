jobinsttatus_${job}<!-- div id -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:resourceURL var="rURL" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>
<msg:help id="helptext" tkey="help.jobinstances" img="${pageContext.request.contextPath}/img/help.gif" />

<msg:getText key="text.jobinstances.sorttype" />
<select name="t" id="t" onchange="javascript:getJobInstances('${job}','${status}',${fromindex},${range},this.value)">
    <option value="0" <c:if test="${0==sorttype}">selected</c:if>><msg:getText key="text.jobinstances.sort0typ" /></option>
    <option value="1" <c:if test="${1==sorttype}">selected</c:if>><msg:getText key="text.jobinstances.sort1typ" /></option>
</select>
<msg:getText key="text.jobinstances.sortrange" />
<select name="r" id="r" onchange="javascript:getJobInstances('${job}','${status}',${fromindex},this.value,${sorttype})">
    <option <c:if test="${10==range}">selected</c:if>>10</option>
    <option <c:if test="${15==range}">selected</c:if>>15</option>
    <option <c:if test="${20==range}">selected</c:if>>20</option>
    <option <c:if test="${50==range}">selected</c:if>>50</option>
    <option <c:if test="${80==range}">selected</c:if>>80</option>
    <option <c:if test="${100==range}">selected</c:if>>100</option>
</select>
<msg:getText key="text.jobinstances.sortfrom" />
<select name="f" id="f" onchange="javascript:getJobInstances('${job}','${status}',this.value,${range},${sorttype})">
<c:forEach var="fr" items="${from}" varStatus="ln">
    <option value="${fr}" <c:if test="${fromindex==fr}">selected</c:if> >${fr}-</option>
</c:forEach>
</select>

    <input type="button" value="Refresh" onclick="javascript:getJobInstances('${job}','${status}',0,${range},${sorttype})">
<table width="100%" class="kback" style="padding-left:40px;">
<%--
    <tr>
	<td colspan="4">${job}</td>
    </tr>	
--%>    
    <tr>
	<td><msg:getText key="text.jobinstances.0" /></td>
        <td><msg:getText key="text.jobinstances.1" /></td>
	<td><msg:getText key="text.jobinstances.2" /></td>
	<td><msg:getText key="text.jobinstances.3" /></td>
    </tr>	    
<c:forEach var="bjob" items="${jobs}" varStatus="ln">
    <c:choose>
	<c:when test="${(ln.index%2)==1}">
    	    <c:set var="color" value="kline1" />
	</c:when>
	<c:otherwise>
    	    <c:set var="color" value="kline0" />
	</c:otherwise>
    </c:choose>
    <tr>    
	<td class="${color}" width="10%">${bjob.pid} </td>
	<td class="${color}" width="30%">${bjob.resource}</td>
	<td class="<msg:getText key="portal.WorkflowData.status.${bjob.status}" />" width="10%"><msg:getText key="portal.WorkflowData.status.${bjob.status}" /></td>
        <td>
	    <c:if test="${(bjob.status==6) || (bjob.status==7)|| (bjob.status==9)}">
	    <table>
	    <tr>
		<td>
	    	    <form method="post" action="" target="" type="hidden">
		    </form>
		</td>
		<td>
		    <form method="post" action="${rURL}" target="formout_${job}">
			<input type="hidden" name="workflowID"   value="${workflow}">
			<input type="hidden" name="jobID"        value="${job}">
			<input type="hidden" name="pidID"        value="${bjob.pid}">
			<input type="hidden" name="runtimeID"    value="${rtid}">
			<input type="hidden" name="fileID"       value="gridnfo.log">
			<input type="submit" class="portlet-form-button" value="<msg:getText key="button.logg" />" onclick="document.getElementById('jobinsttatus0_${job}').style.display='block'">
		    </form>
		</td>
		<td>
	    	    <form method="post" action="${rURL}" target="formout_${job}">
			<input type="hidden" name="workflowID"   value="${workflow}">
			<input type="hidden" name="jobID"        value="${job}">
			<input type="hidden" name="pidID"        value="${bjob.pid}">
			<input type="hidden" name="runtimeID"    value="${rtid}">
			<input type="hidden" name="fileID"       value="stdout.log">
			<input type="submit" class="portlet-form-button" value="<msg:getText key="button.stdout" />" onclick="document.getElementById('jobinsttatus0_${job}').style.display='block'">
		    </form>
		</td>
		<td>
	    	    <form method="post" action="${rURL}" target="formout_${job}">
			<input type="hidden" name="workflowID"   value="${workflow}">
			<input type="hidden" name="jobID"        value="${job}">
			<input type="hidden" name="pidID"        value="${bjob.pid}">
			<input type="hidden" name="runtimeID"    value="${rtid}">
			<input type="hidden" name="fileID"       value="stderr.log">
			<input type="submit" class="portlet-form-button" value="<msg:getText key="button.stderr" />" onclick="document.getElementById('jobinsttatus0_${job}').style.display='block'">
		    </form>
		</td>
		<td>
    		    <form method="post" action="${rURL}" name="downform_${job}">
			    <input type="hidden" name="workflowID"    value="${workflow}">
			    <input type="hidden" name="jobID"         value="${job}">
			    <input type="hidden" name="pidID"         value="${bjob.pid}">
			    <input type="hidden" name="outputLogType" value="all">
			    <input type="hidden" name="downloadType"  value="joboutputs_${rtid}">
			    <input type="submit" value="<msg:getText key="button.output" />" class="portlet-form-button">
		    </form>
		</td>
	    </tr>
	    </table>	
	    </c:if>
	</td>
    </tr>
</c:forEach>

</table>
