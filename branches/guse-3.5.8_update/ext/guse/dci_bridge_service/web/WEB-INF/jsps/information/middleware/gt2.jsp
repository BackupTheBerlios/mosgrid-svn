<%--
edit gt2 grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

            <table class="editdata" >
                <tr>
                    <th><f:message key="new.gt2.name" /></th>
                    <td class="ln0">${item.name}</td>
                </tr>
                <tr>
                    <th><f:message key="new.gt2.accessdata" /></th>
                    <td class="ln1">${item.gt2.accessdata}</td>
                </tr>
                <tr>
                    <th><f:message key="new.gt2.type" /></th>
                    <td class="ln0">${item.gt2.type}</td>
                </tr>
                <tr>
                    <th><f:message key="new.gt2.site" /></th>
                    <th><f:message key="new.gt2.jobmanager" /></th>
                </tr>
                <c:forEach var="t" items="${item.gt2.resource}" varStatus="ln">
                    <c:forEach var="j" items="${t.jobmanager}" varStatus="lne">
                    <tr>
                        <td>${t.host}</td>
                        <td>${j}</td>
                    </tr>
                    </c:forEach>
                </c:forEach>
            </table>
</f:bundle>