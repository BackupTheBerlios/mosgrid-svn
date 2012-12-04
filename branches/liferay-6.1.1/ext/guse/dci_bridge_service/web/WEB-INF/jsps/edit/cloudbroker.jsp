<%-- 
    Document   : Edit CloudBroker
    Created on : Jan 18, 2012, 10:17:15 AM
    Author     : Akos Hajnal
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<table class="newdata" >
    <caption><f:message key="caption.cloudbroker.edit" /></caption>
    <form id="editForm" method="post" action="conf?editing=${item.name}">
    <tr>
        <th class="ln0"><f:message key="cloudbroker.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="pcloudbrokername" size="25" value="${item.name}" /></td>
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
        <th class="ln0"><f:message key="cloudbroker.url" /></th>
        <td class="ln0"><input class="ln1" type="text" name="pcloudbrokerurl" size="60" value="${item.cloudbroker.url}" /></td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="cloudbroker.user" /></th>
        <td class="ln0"><input class="ln1" type="text" name="pcloudbrokeruser" size="60" value="${item.cloudbroker.user}" /></td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="cloudbroker.password" /></th>
        <td class="ln0"><input class="ln1" type="password" name="pcloudbrokerpassword" size="60" value="${item.cloudbroker.password}" /></td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="cloudbroker.softwares" />, <f:message key="cloudbroker.resources" /></th>
        <td class="ln0">

            <h2><f:message key="cloudbroker.softwares" /></h2>
            <ul style="list-style: none">
                <c:forEach var="sw" items="${item.cloudbroker.software}">
                <li><b><c:out value="${sw.name}" /></b> 
                    <ul>
                        <c:forEach var="exec" items="${sw.executable}">
                        <li><f:message key="cloudbroker.executable" />: <i><c:out value="${exec.name}" /></i>
                        </li>
                        </c:forEach>
                    </ul>
                </li>
                </c:forEach>
            </ul>

            <br/>
            <h2><f:message key="cloudbroker.resources" /></h2>
            <ul style="list-style: none">
            <c:forEach var="res" items="${item.cloudbroker.resource}">
                 <li><b><c:out value="${res.name}" /></b>

                 <ul style="list-style:square">
                        <c:forEach var="ins" items="${res.instancetype}">
                        <li><f:message key="cloudbroker.instancetype" />: <i><c:out value="${ins.name}" /></i></li>
                        </c:forEach>
                 </ul>

                 <ul style="list-style:circle">
                         <c:forEach var="reg" items="${res.region}">
                        <li><f:message key="cloudbroker.region" />: <i><c:out value="${reg.name}" /></i></li>
                        </c:forEach>
                 </ul>
                 <br/>
            </c:forEach>
            </ul>

        </td>
    </tr>



    <jsp:include page="submit_button.jsp" />
</form>
</table>
</f:bundle>