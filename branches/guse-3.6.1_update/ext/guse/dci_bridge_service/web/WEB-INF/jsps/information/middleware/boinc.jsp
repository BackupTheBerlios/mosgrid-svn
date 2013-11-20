<%-- 
edit boinc grid
Boinc ${item} kivalasztott boinc grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="editdata" >
    <tr>
        <th><f:message key="new.boinc.name" /></th>
        <td class="ln0">${item.name}</td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.id" /></th>
        <td class="ln1">${item.boinc.id}</td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.wsdl" /></th>
        <td class="ln0">${item.boinc.wsdl}</td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.job.name" /></th>
        <th><f:message key="new.boinc.job.state" /></th>
    </tr>
    <c:forEach var="t" items="${item.boinc.job}" varStatus="ln">
        <c:choose>
            <c:when test="${ln.index%2==0}">
                <c:set var="css" value="ln0" />
            </c:when>
            <c:otherwise>
                <c:set var="css" value="ln1" />
            </c:otherwise>
        </c:choose>
    <tr>
        <td class="${css}">${t.name}</td>
        <td class="${css}">${t.state}</td>
    </tr>
    </c:forEach>
</table>
</f:bundle>