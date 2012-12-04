<%--
List<GuseServiceBean> ${sessionScope.services} elerheto gUSE serrvize-ek
GuseServiceBean ${sessionScope.serviceitem} modositando szerviz
GuseServiceBean ${sessionScope.auth} jogosultsag allitas ezen a szervizen
--%>

<%@taglib prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >

<p:defineObjects />
<p:renderURL var="pURL">
    <p:param name="guse-render" value="import" />
</p:renderURL>
<form method="post" action="${pURL}">
    <table>
        <caption><f:message key="caption.copy" /></caption>
        <tr>
            <td><f:message key="head.copyfrom" /></td>
            <td>
                <select name="psrc">
                <c:forEach var="t" items="${sessionScope.services}">
                    <option>${t.url}</option>
                </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <td><f:message key="head.copyto" /></td>
            <td>
                <select name="pdst">
                <c:forEach var="t" items="${sessionScope.services}">
                    <option>${t.url}</option>
                </c:forEach>
                </select>
            </td>
        </tr>
    </table>
    <input type="submit" value="<f:message key="button.copy" />">
</form>

</f:bundle>
