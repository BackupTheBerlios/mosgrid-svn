<%--
Uj service tipus felvitele
--%>

<%@taglib prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >
<p:defineObjects />
<p:actionURL var="nstURL">
    <p:param name="guse" value="newservicetype" />
</p:actionURL>

<form method="post" action="${nstURL}">
<table border="1">
    <caption><f:message key="caption.newtype" /></caption>
    <tr>
        <th><f:message key="head.name" /></th>
        <td><input type="text" name="pname" /></td>
    </tr>
    <tr>
        <th><f:message key="head.desc" /></th>
        <td>
            <textarea rows="5" cols="50" name="ptxt" ></textarea>
        </td>
    </tr>
    <tr> <td colspan="2"><input type="submit" value="<f:message key="action.save" />"></tr>
</table>
</form>
</f:bundle>