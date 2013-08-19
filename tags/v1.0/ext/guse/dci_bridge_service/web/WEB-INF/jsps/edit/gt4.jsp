<%--
edit gt4 grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<form id="editForm" method="post" action="conf?editing=${item.name}">

<table>
    <tr>
        <td><%-- grid data--%>
            <table class="newdata" >
                <caption><f:message key="caption.gt2.edit" /></caption>
                <tr>
                    <th><f:message key="new.gt4.name" /></th>
                    <td><input type="text" name="pgt4name" size="25" value="${item.name}"/></td>
                </tr>
                <tr>
                    <th class="ln1"><f:message key="general.status" /></th>
                    <td class="ln1">
                        <c:choose>
                            <c:when test="${item.enabled}">
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
                    <th><f:message key="new.gt4.accessdata" /></th>
                    <td><input type="text" name="paccessdata" size="60" value="${item.gt4.accessdata}" /></td>
                </tr>
                <tr>
                    <th><f:message key="new.gt4.type" /></th>
                    <td><input type="text" name="ptype" size="60" value="${item.gt4.type}" /></td>
                </tr>
                <tr>
                    <th><f:message key="new.gt4.site" /></th>
                    <th><f:message key="action.delete" /></th>
                </tr>
                <c:forEach var="t" items="${item.gt4.resource}" varStatus="ln">
                    <c:forEach var="j" items="${t.jobmanager}" varStatus="lne">
                    <tr>
                        <td>${t.host}/${j}</td>
                        <td><input type="checkbox" name="pdelete_${ln.index}_${lne.index}" size="30" value="${t.host}/${j}" /></td>
                    </tr>
                    </c:forEach>
                </c:forEach>
            </table>
        </td>
        
        <td><%-- new site and jobmanaver--%>
            <table class="newdata" >
                <caption><f:message key="caption.gt4.resources.new" /></caption>
                <c:forEach var="t" begin="0" end="5" >
                <tr>
                    <th><f:message key="new.gt4.site" /></th>
                    <td>
                        <input type="text" name="phost_${t}" size="30" />
                        <input type="text" name="pjobmanager_${t}" size="30" />
                    </td>
                </tr>
                </c:forEach>
            </table>
        </td>
    </tr>
    <jsp:include page="submit_button.jsp" />
</table>

</form>

</f:bundle>