<%-- meglevo gUSE service szerkesztese
List<GuseServiceTypeBean> ${styps} rendelkezesre allo service tipusok
List<GuseServiceCommunicationBean> ${coms} rendelkezesre allo communikacios tipusok
GuseServiceBean ${sessionScope.serviceitem} szerkezstesre kivalsztaott szerviz
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
    <caption><f:message key="caption.editservice" /></caption>
    <tr>
        <th><f:message key="head.type" /></th>
        <td>
            <select name="pstyp">
                <c:forEach var="t" items="${styps}" >
                <c:choose >
                    <c:when test="${t.id==sessionScope.serviceitem.typ.id}">
                        <option value="${t.id}" selected="true"><c:out value="${t.sname}" escapeXml="true" /></option>
                    </c:when>
                    <c:otherwise>
                        <option value="${t.id}"><c:out value="${t.sname}" escapeXml="true" /></option>
                    </c:otherwise>
                </c:choose>
                </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <th><f:message key="head.face" /></th>
        <td>
            <select name="pcom">
            <c:forEach var="t" items="${coms}" >
                <c:set var="fl" value="0" />
                <c:forEach var="i" items="${sessionScope.serviceitem.com}" >
                    <c:set var="fl" value="1" />
                </c:forEach>
                <c:choose >
                    <c:when test="${fl==1}">
                        <option value="${t.id}" selected="true"><c:out value="${t.cname}" escapeXml="true" /></option>
                    </c:when>
                    <c:otherwise>
                        <option value="${t.id}"><c:out value="${t.cname}" escapeXml="true" /></option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <th><f:message key="head.url" /></th>
        <td><input name="purl" type="text" value="<c:out value="${sessionScope.serviceitem.url}" escapeXml="true" />" /></td>
    </tr>
    <tr>
        <th><f:message key="head.iurl" /></th>
        <td><input name="piurl" type="text"  value="<c:out value="${sessionScope.serviceitem.iurl}" escapeXml="true" />" /></td>
    </tr>
    <tr>
        <th><f:message key="head.purl" /></th>
        <td><input name="psurl" type="text" value="<c:out value="${sessionScope.serviceitem.surl}" escapeXml="true" />" /></td>
    </tr>
    <tr>
        <th><f:message key="head.status" /></th>
        <td>
            <c:choose>
                <c:when test="${sessionScope.serviceitem.state}">
                    <input checked="true" type="radio" name="pstatus" value="true" /><f:message key="text.active" />
                    <input type="radio" name="pstatus" value="false" /><f:message key="text.inactive" />
                </c:when>
                <c:otherwise>
                    <input type="radio" name="pstatus" value="true" /><f:message key="text.active" />
                    <input checked="true" type="radio" name="pstatus" value="false" /><f:message key="text.inactive" />
                </c:otherwise>
            </c:choose>
        </td>
    </tr>

    <tr> <td colspan="2"><input type="submit" value="<f:message key="action.save" />"></tr>
</table>
</form>
</f:bundle>