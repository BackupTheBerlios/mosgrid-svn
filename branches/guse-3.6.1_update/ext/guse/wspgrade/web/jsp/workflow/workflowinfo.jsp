<!-- 
    Workflow konfigracios hibak megjelenitesere szolgalo informacios oldal
-->
<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>


<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<msg:help id="helptext" tkey="help.workflowinfo" img="${pageContext.request.contextPath}/img/help.gif" />


<portlet:defineObjects/>
<portlet:renderURL var="backURL" portletMode="VIEW" >
    <portlet:param name="render" value="main" />
</portlet:renderURL>

<form method="post" action="${backURL}">
 <input type="submit" value="Back" /> 
</form> 

<table width="100%" class="darktable">
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.workflowinfo.0" />: </div></td>
    <td width="80%" class="kback"><c:out value="${wrkdata.workflowID}" escapeXml="true" /></td>
    </tr>
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.workflowinfo.1" />: </div></td>
	<td width="80%" class="kback"><c:out value="${wrkdata.txt}" escapeXml="true" /></td>
    </tr>
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.workflowinfo.2" />: </div></td>
	<td class="kback"><c:out value="${wrkdata.graf}" default="--" /></td>
    </tr>
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.workflowinfo.3" />: </div></td>
	<td class="kback"><c:out value="${wrkdata.template}" default="--" /></td>
    </tr>
</table>

<br />

<table class="kback"> 
    <c:set var="p" value="0"/>
    <c:forEach var="tmp" items="${errors}" varStatus="ln">
	<c:if test="${ln.index==0}">
	    <tr> <th><msg:getText key="text.workflowinfo.4" /></th> <th><msg:getText key="text.workflowinfo.5" /> </th> <th><msg:getText key="text.workflowinfo.6" /> </th> </tr> 
	    <c:set var="p" value="1"/>
	</c:if>
	<c:choose>
	    <c:when test="${(ln.index%2)==1}">
	        <c:set var="color" value="kline1" />
	    </c:when>
	    <c:otherwise>
	        <c:set var="color" value="kline0" />
	    </c:otherwise>
	</c:choose>
		<tr> 
            <td class="${color}"><c:out value="${tmp.jobName}" escapeXml="true" /> </td>
            <td class="${color}"><c:out value="${tmp.portID}" escapeXml="true" /> </td>
            <td class="${color}"><msg:getText key="${tmp.errorID}" /></td>
        </tr>
    </c:forEach> 
</table> 

<c:if test="${p==0}">
    <b><msg:getText key="text.workflowinfo.confsucces" /></b>
</c:if>
