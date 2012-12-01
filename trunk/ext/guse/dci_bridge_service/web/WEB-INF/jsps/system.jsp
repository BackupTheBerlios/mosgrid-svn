<%--
Rendszer konfiguracio
boolean ${status} Rendszer Status
Configure ${config}
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h2>General Settings</h2>

<form method="post" action="settings">
<table border="1">
    <tr>
        <td>Service status</td>
        <td>
            <c:choose>
                <c:when test="${status}">
                    <input type="radio" name="status" value="0"> Inactive<br />
                    <input type="radio" name="status" value="1" checked> Active<br />
                </c:when>
                <c:otherwise>
                    <input type="radio" name="status" value="0" checked> Inactive<br />
                    <input type="radio" name="status" value="1" > Active<br />
                </c:otherwise>
            </c:choose>

        </td>
    </tr>

    <tr>
        <td>Work dir(if not set = catalina home)</td>
        <td><input type="text" name="workdir" size="150" value="${config.path}"></td>
    </tr>

    <tr>
        <td>URL of Meta broker wsdl</td>
        <td><input type="text" name="metabroker" size="150" value="${config.metabroker}"></td>
    </tr>

    <tr>
        <td colspan="2" align="center"><input type="submit" value="save"></td>
    </tr>
</table>
</form>
