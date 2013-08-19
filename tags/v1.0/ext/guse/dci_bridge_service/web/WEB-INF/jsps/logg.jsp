<%-- 
View LoggEvents
List<LoggBean> ${loggs} logg events
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >
<table class="newdata">
    <tr>
        <th> <f:message key="head.index" /> </th>
        <th> <f:message key="head.level" /> </th>
        <th> <f:message key="head.timestamp" /> </th>
        <th> <f:message key="head.info" /> </th>
    </tr>
<c:forEach var="t" items="${loggs}" varStatus="ln">
    <c:choose>
        <c:when test="${ln.index%2==0}"><c:set var="class" value="ln1" /></c:when>
        <c:otherwise><c:set var="class" value="ln0" /></c:otherwise>
    </c:choose>
    <tr>
        <td class="${class}">${ln.index}</td>
        <td class="${class}">${t.level}</td>
        <td class="${class}">${t.timestamp0}</td>
        <td class="${class}">${t.info}</td>
    </tr>
</c:forEach>
</table>
</f:bundle>