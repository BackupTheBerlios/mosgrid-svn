<%--
@call index.jsp
monitor oldal
List<Middleware> ${queues} kivalasztott middleware sorkezeloi
List<Job> ${queue_data} Sorban levo Jobok
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<f:message key="item.queue" />
<form id="edit" method="post" action="conf">
<table class="newdata">
    <tr>
        <th><f:message key="monitor.queue.name" /></th>
        <th><f:message key="monitor.queue.size" /></th>
        <th><f:message key="monitor.queue.action" /></th>
    </tr>
    <c:forEach var="t" items="${queues}" varStatus="ln">
        <c:choose>
            <c:when test="${ln.index%2==0}"><c:set var="class" value="ln1" /></c:when>
            <c:otherwise><c:set var="class" value="ln0" /></c:otherwise>
        </c:choose>
    <tr>
        <c:if test="${t.name==param['pqueue']}"><c:set var="class" value="lna" /></c:if>
        <td class="${class}">${t.name}</td>
        <td class="${class}">${t.size}</td>
        <td class="${class}">
            <a href="conf?t=info&amp;pqueue=${t.name}"> <f:message key="monitor.queue.view" /> </a>
        </td>
    </tr>
</c:forEach>
</table>
</form>

<table class="newdata">
    <caption><f:message key="monitor.queue.job" /></caption>
    <c:forEach var="t" items="${queue_data}" varStatus="ln">
        <c:choose>
            <c:when test="${ln.index%2==0}"><c:set var="class" value="ln0" /></c:when>
            <c:otherwise><c:set var="class" value="ln1" /></c:otherwise>
        </c:choose>
    <tr>
        <td class="${class}">${ln.index}</td>
        <td class="${class}">${t.id}</td>
        <td class="${class}">${t.resource}</td>
        <td class="${class}">
            <a href=""></a>
        </td>
    </tr>            
    </c:forEach>
</table>


</f:bundle>