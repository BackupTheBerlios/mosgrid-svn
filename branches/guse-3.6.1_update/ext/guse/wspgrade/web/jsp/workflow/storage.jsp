<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>



<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<portlet:renderURL var="rURL" />

<portlet:actionURL var="pURL">
    <portlet:param name="guse" value="doDelete" />
</portlet:actionURL>


<form method="post" action="${rURL}">
    <input type="submit" class="portlet-form-button" value="<msg:getText key="button.refresh" />" />
</form>

<portlet:resourceURL var="rURL" />

<form method="post" action="${pURL}"> 
    <input type="hidden" name="workflow" id="workflow">
    <input type="hidden" name="action" id="action" >
</form>

    <table class="kback">
	<tr>
	    <td class="khead"><msg:getText key="text.storage.0" />:</td>
	    <td>${userQuota} Byte(s)</td>
	</tr>
	<tr>
	    <td class="khead"><msg:getText key="text.storage.1" />:</td>
	    <td>${userUseQuota} Byte(s)</td>
	</tr>
	<tr>
	    <td class="khead"><msg:getText key="text.storage.2" />:</td>
	    <td>${userQuotaPercent} %</td>
	</tr>
    </table>
    
    <br />
    <br />

    <table width="100%"  class="kback">
	<tr>
	    <th class="khead"><msg:getText key="text.storage.3" /></th>
	    <th class="khead"><msg:getText key="text.storage.4" /></th>
	    <th class="khead"><msg:getText key="text.storage.5" /></th>
	    <th class="khead"><msg:getText key="text.storage.6" /></th>
	</tr>
	<c:forEach var="tmp" items="${workflows}" varStatus="p">
	    <c:choose>
		<c:when test="${(p.index%2)==1}">
		    <c:set var="color" value="kline1" />
		</c:when>
		<c:otherwise>
		    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>		
	    <tr>
            <td class="${color}">
                <div class="color" style="font-weight:bold">
                    <c:out value="${tmp.workflowID}"  escapeXml="true" />
                </div>
            </td>
		<td class="${color}">${tmp.size}</td>
		<td class="${color}">
		    <table width="100%">
		    <tr><td>
		    <table width="100%">
			<tr>
    			    <td width="40%" align="center" style="background-color:${color}"><msg:getText key="text.storage.7" /></td>
    			    <td width="10%" align="center" style="background-color:${color}"><msg:getText key="text.storage.8" /></td>
			    <td width="50%" align="center" style="background-color:${color}"><msg:getText key="text.storage.9" /></td>
			</tr>
		    </table>
		    </td></tr><tr><td>
	    	    <div style="position:relative;width:100%;height:100px;overflow:auto;" id="pp" >
		    <table width="100%"> 
		    <c:forEach var="tmp0" items="${tmp.allRuntimeInstance}" varStatus="ip">
		        <c:choose>
			    <c:when test="${(ip.index%2)==1}">
    				<c:set var="icolor" value="#7eab7e" />
			    </c:when>
			    <c:otherwise>
				<c:set var="icolor" value="#9fcd9f" />
			    </c:otherwise>
			</c:choose>		
			<tr>
			<c:if test="${tmp0.value.text!='null'}">		
    			    <td width="40%" style="background-color:${icolor}">${tmp0.value.text}</td>
    			    <td width="10%" style="background-color:${icolor}"> ${tmp0.value.size}</td>
			    <td width="20%" align="center" style="background-color:${icolor}">
    		    <form method="post" action="${rURL}" name="upform">
				    <input type="hidden" name="workflowID"   value="${tmp.workflowID}">
				    <input type="hidden" name="downloadType" value="outputs_${tmp0.key}">
				    <input type="submit" value="<msg:getText key="button.outputs" />" class="portlet-form-button">
				</form>
			    </td>
			    <td width="30%" align="center" style="background-color:${icolor}">
    		    <form method="post" action="${rURL}" name="upform">
				    <input type="hidden" name="workflowID"   value="${tmp.workflowID}">
				    <input type="hidden" name="downloadType" value="inputs_${tmp0.key}">
				    <input type="hidden" name="instanceType" value="one_${tmp0.key}">
				    <input type="submit" value="<msg:getText key="button.instance" />" class="portlet-form-button">
				</form>
			    </td>
			</c:if>
			</tr>    
		    </c:forEach>
		    </table>
		    </div>
		    </td></tr>
		    </table>
		</td>
		<td class="${color}" align="center">
		    <table>
		    <tr>
		    <td>
		     <form method="post" action="${rURL}" name="upform">
			<input type="hidden" name="workflowID"   value="${tmp.workflowID}">
			<input type="hidden" name="downloadType" value="all">
			<input type="hidden" name="instanceType" value="all">
			<input type="hidden" name="outputLogType" value="all">
			<input type="hidden" name="exportType"    value="proj">
			<input type="submit" value="<msg:getText key="button.all" />" class="portlet-form-button">
		    </form>
		    </td>

            <td>
		     <form method="post" action="${rURL}" name="upform">
			<input type="hidden" name="portalID"     value="${portalID}">
			<input type="hidden" name="wfsID"        value="${tmp.wfsID}">
			<input type="hidden" name="userID"       value="${userID}">
			<input type="hidden" name="workflowID"   value="${tmp.workflowID}">
			<input type="hidden" name="downloadType" value="all">
			<input type="hidden" name="instanceType" value="all">
			<input type="hidden" name="outputLogType" value="none">
			<input type="submit" value="<msg:getText key="button.allwithoutlogs" />" class="portlet-form-button">
		    </form>
		    </td>
		    <td>	
		     <form method="post" action="${rURL}" name="upform">
			<input type="hidden" name="workflowID"   value="${tmp.workflowID}">
			<input type="hidden" name="downloadType" value="inputs">
			<input type="submit" value="<msg:getText key="button.inputs" />" class="portlet-form-button">
		    </form>	
		    </td>
		    <td>
		     <form method="post" action="${rURL}" name="upform">
			<input type="hidden" name="userID"       value="${userID}">
			<input type="hidden" name="workflowID"   value="${tmp.workflowID}">
			<input type="hidden" name="downloadType" value="outputs_all">
			<input type="submit" value="<msg:getText key="button.outputs" />" class="portlet-form-button">
		    </form>
		    </td>	
		    </tr>
		    </table>
		</td>
	    </tr>
	</c:forEach>
    </table>
