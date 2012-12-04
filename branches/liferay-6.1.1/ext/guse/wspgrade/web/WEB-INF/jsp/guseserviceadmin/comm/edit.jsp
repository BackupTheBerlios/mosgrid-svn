<%-- Communikacios tipus szerkesztese
GuseServiceCommunicationBean ${sessionScope.comitem} szerkesztesre kivalsztaott peldany

--%>

<%@taglib prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >

<p:defineObjects />
<p:actionURL var="ncURL">
    <p:param name="guse" value="newcommtype" />
</p:actionURL>

<form method="post" action="${ncURL}">
<table border="1">
    <caption><f:message key="caption.editcom" /></caption>
    <tr>
        <th><f:message key="head.name" /></th>
        <td><input type="text" name="pname" value="<c:out value="${sessionScope.comitem.cname}" escapeXml="true" />" /></td>
    </tr>
    <tr>
        <th><f:message key="head.desc" /></th>
        <td>
            <textarea rows="5" cols="50" name="ptxt"><c:out value="${sessionScope.comitem.txt}" escapeXml="true" /></textarea>
        </td>
    </tr>
    <tr> <td colspan="2"><input type="submit" value="modosit"></tr>
</table>
</form>
</f:bundle>