<%-- 
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<form  id="editForm" method="post" action="conf?general=true">
<table class="newdata">
    <tr>
        <th class="ln1"><f:message key="general.pluginenabled" /></th>
        <td class="ln1">
            <c:choose>
                <c:when test="${data.enabled}">
                    <input type="radio" name="penabled" checked="true" value="1" /><f:message key="general.yes" />
                    <input type="radio" name="penabled" value="0" /><f:message key="general.no" />
                </c:when>
                <c:otherwise>
                    <input type="radio" name="penabled" value="1" /><f:message key="general.yes" />
                    <input type="radio" name="penabled" checked="true" value="0" /><f:message key="general.no" />
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="general.pluginclass" /></th>
        <td class="ln0"><input type="text" name="pclass" size="150" value="${data.plugin}"/></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="general.threadcounts" /></th>
        <td class="ln1"><input type="text" name="pthreads" size="2"  value="${data.threads}"></td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="general.proxytype" /> </th>
        <td class="ln0">
            <c:forEach var="c" items="${certs}" varStatus="ln">
                <c:set var="flag" value="false" />
                <c:forEach var="t" items="${data.certificate}" >
                    <c:if test="${t==c}" ><c:set var="flag" value="true" /></c:if>
                </c:forEach>
                <c:choose>
                    <c:when test="${flag=='true'}">
                        <input type="checkbox" checked="true" name="pcert_${ln.index}" value="${c}">${c}<br />
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="pcert_${ln.index}" value="${c}">${c}<br />
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </td>
    </tr>
    <jsp:include page="edit/submit_button.jsp" />
</table>
</form>
</f:bundle>