dtmp<!-- div id -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>
<msg:help id="helptext" tkey="help.descvalue" img="${pageContext.request.contextPath}/img/help.gif" />

<table >
    <caption>Actual values</caption>
    <c:forEach var="item" items="${keys}">
	<c:if test="${item.value!=''}">
    <tr>
	<td style="border: 2px solid #ff0000">${fn:substringAfter(item.key,".key")}</td>
	<td style="border: 2px solid #ff0000"><c:out value="${item.value}" escapeXml="true" /></td>
    </tr>
	</c:if>
    </c:forEach>
</table>
