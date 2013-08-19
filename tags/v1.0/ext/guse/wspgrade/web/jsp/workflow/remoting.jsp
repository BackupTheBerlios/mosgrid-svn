<jsp:include page="/jsp/core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<portlet:defineObjects/>
<portlet:actionURL var="pURL" portletMode="VIEW" />


<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<msg:help id="helptext" tkey="help.remoting" img="${pageContext.request.contextPath}/img/help.gif" />

<div id="rwlist" style="position:relative;">


<form action="${pURL}" method="POST">

<table class="kback">
    <tr>
	<td><msg:getText key="text.remoting.0" /></td>
	<td>
	    <select name="remotingworkflow" id="remotingworkflow">
	    <c:forEach var="wkf" items="${workflows}">
		<c:if test="${wkf.remoting==''}">
            <option><c:out value="${wkf.workflowID}" escapeXml="true" /> </option>
		</c:if>
    	    </c:forEach>
	    </select>
	    <msg:toolTip id="ctmpdiv" tkey="portal.remote.wflist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</td>
    </tr>

    <tr>
	<td><msg:getText key="text.remoting.1" /></td>
	<td><textarea name="remotingtext"></textarea> </td>
        <td>
	    <msg:toolTip id="ctmpdiv" tkey="portal.remote.keytext" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</td>
    </tr>
    <tr>
	<td colsan="2">
	    <input type="submit" class="portlet-form-button" value="<msg:getText key="button.set" />">
	</td>

    </tr>
</table>
</form>

<br />
<br />
<table width="100%" class="kback">

    <c:forEach var="wkf" items="${workflows}" varStatus="p">
        <c:choose>
    	    <c:when test="${(p.index%2)==1}">
		<c:set var="color" value="kline1" />
	    </c:when>
	    <c:otherwise>
		<c:set var="color" value="kline0" />
	    </c:otherwise>
	</c:choose>		
    
	<tr>
	    
	    <c:choose>
	        <c:when test="${wkf.remoting!=''}">
    		    <td class="${color}">
			<table>
                <tr><td><div class="bold"><c:out value="${wkf.workflowID}" escapeXml="true" /></div></td></tr>
                <tr><td>&nbsp;<c:out value="${wkf.txt}" escapeXml="true" /></td></tr>
			</table>
		    </td>
		    <td class="${color}">
			<msg:getText key="text.remoting.2" /> 
			<input type="button" class="portlet-form-button" id="remotingbutton_${wkf.workflowID}" onclick="javascript:hideremotekey('${wkf.workflowID}')" value="<msg:getText key="button.wievkey" />">
		    </td>
		    <td class="${color}">
                <div id="remoting_<c:out value="${wkf.workflowID}" escapeXml="true" />" style="display:none">
                    <textarea name="remotingtext_<c:out value="${wkf.workflowID}" escapeXml="true" />" id="remotingtext_<c:out value="${wkf.workflowID}" escapeXml="true" />" ><c:out value="${wkf.remoting}" escapeXml="true" /></textarea>
			    <msg:toolTip id="ctmpdiv" tkey="portal.remote.keytext" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			    <form action="${pURL}" method="POST">	
                <input type="hidden" name="remotingdelete" value="<c:out value="${wkf.workflowID}" escapeXml="true" />">
				<input type="submit" class="portlet-form-button" value="<msg:getText key="button.delete" />">
			    </form>
			</div>
		    </td>
		</c:when>
	    </c:choose>
	</tr>
    </c:forEach>
</table>

</div>

<%--
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<portlet:defineObjects/>
<portlet:actionURL var="pURL" portletMode="VIEW" />

<form action="${pURL}" method="POST">
<table width="100%" class="kback">
    <tr>
	<td><msg:getText key="text.remoting.0" /></td>
	<td>Remote Status</td>
	<td><msg:getText key="text.remoting.1" /></td>
    </tr>	
    <c:forEach var="wkf" items="${workflows}" varStatus="p">
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
		<table>
    		    <tr><td><div class="bold">${wkf.workflowID}</div></td></tr>
    		    <tr><td>&nbsp;${wkf.txt}</td></tr>				    
		</table>
	    </td>
	    
	    <c:choose>
	        <c:when test="${wkf.remoting==''}">
		    <td class="${color}">Not definited remote key</td>
		    <td class="${color}"><textarea name="remotingtext_${wkf.workflowID}"></textarea></td>
		</c:when>
		<c:otherwise>
		    <td class="${color}">
			<msg:getText key="text.remoting.2" /> 
			<input type="button" class="portlet-form-button" id="remotingbutton_${wkf.workflowID}" onclick="javascript:hideremotekey('${wkf.workflowID}')" value="View Key">
		    </td>
		    <td class="${color}">
			<div id="remoting_${wkf.workflowID}" style="display:none">
			    <textarea name="remotingtext_${wkf.workflowID}" id="remotingtext_${wkf.workflowID}" >${wkf.remoting}</textarea>
			    <input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('remotingtext_${wkf.workflowID}').value=''" value="Clear">
			</div>
		    </td>
		</c:otherwise>
	    </c:choose>
	</tr>
    </c:forEach>
	<tr><td colspan="3"><input type="submit" class="portlet-form-button" value="Set"></td>
</table>
</form>
--%>
