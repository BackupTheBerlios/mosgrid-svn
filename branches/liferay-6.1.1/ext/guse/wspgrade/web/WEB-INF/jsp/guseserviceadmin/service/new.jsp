<%--
Uj gUSE service deffinialasa
List<GuseServiceTypeBean> ${sessionScope.styps} rendelkezesre allo service tipusok
List<GuseServiceCommunicationBean> ${sessionScope.coms} rendelkezesre allo communikacios tipusok
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >

<p:defineObjects />
<p:actionURL var="nsURL">
    <p:param name="guse" value="newservice" />
</p:actionURL>
<form method="post" action="${nsURL}">
<table border="1">
    <caption><f:message key="caption.newservice" /></caption>
    <tr>
        <th><f:message key="head.type" /></th>
        <td>
            <select name="pstyp">
                <c:forEach var="t" items="${sessionScope.styps}" >
                    <option value="${t.id}"><c:out value="${t.sname}" escapeXml="true" /></option>
                </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <th><f:message key="head.face" /></th>
        <td>
            <select name="pcom">
            <c:forEach var="t" items="${sessionScope.coms}" >
                <option value="${t.id}"><c:out value="${t.cname}" escapeXml="true" /></option>
            </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <th><f:message key="head.url" /></th>
        <td><input name="purl" type="text"/></td>
    </tr>
    <tr>
        <th><f:message key="head.iurl" /></th>
        <td><input name="piurl" type="text"/></td>
    </tr>
    <tr>
        <th><f:message key="head.purl" /></th>
        <td><input name="psurl" type="text"/></td>
    </tr>
    <tr>
        <th><f:message key="head.status" /></th>
        <td>
            <input type="radio" name="pstatus" value="true" /><f:message key="text.active" />
            <input checked="true" type="radio" name="pstatus" value="false" /><f:message key="text.inactive" />
        </td>
    </tr>
    <tr>

    <tr> <td colspan="2"><input type="submit" value="<f:message key="action.save" />"></tr>
</table>
</form>
</f:bundle>