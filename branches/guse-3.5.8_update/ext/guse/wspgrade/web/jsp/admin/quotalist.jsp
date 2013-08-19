<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<portlet:actionURL var = "pURLdef">
    <portlet:param value="doSaveDef" name="guse"/>
</portlet:actionURL>
<portlet:actionURL var = "pURLsave">
    <portlet:param value="doSaveUsers" name="guse"/>
</portlet:actionURL>

<form method="post" action="${pURLdef}" >
    Set quota for portal users: <input type="text" name="defquota" maxsize="5" maxlenght="5" value="${defquota}" onkeyup="this.value = this.value.replace(/[^0-9]/g, '')"/> MB
    <input type="submit" value="Save" />
</form>
<br/>
<div><b><i>Message:</i></b> <font color="990033"> ${msg} ${msgerr}</font></div>
<br/>
<div>  
    <form method="post" action="${pURLsave}" >
        <table>
            <tr>
                <td> User Name </td>
                <td> Screen Name </td>
                <td> User ID </td>
                <td> Quota </td>
            </tr>
            <c:forEach var="user" items="${users}">
                <tr>
                    <c:choose>
                        <c:when test="${user.name=='Summa: '}" >
                            <td colspan="3"><b>${user.name}</b> </td>
                            <td><b>${user.quota} MB</b></td>
                        </c:when>
                        <c:otherwise>
                            <td>${user.name} </td>
                            <td>${user.sname} </td>
                            <td>${user.id} </td>
                            <td><input type="text" name="q${user.id}" value="${user.quota}" lenght="5" size="5" onkeyup="this.value = this.value.replace(/[^0-9]/g, '')"/> MB</td>
                        </c:otherwise>
                        </c:choose>            
                </tr>
            </c:forEach>
        </table>
        <input type="submit" value="Save" />
    </form>
</div>
