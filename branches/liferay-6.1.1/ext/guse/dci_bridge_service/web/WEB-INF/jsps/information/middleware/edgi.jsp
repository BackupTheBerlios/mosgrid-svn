<%--
view edgi vo
Glite ${item} selected VO config
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<table class="newdata" >
    <caption><f:message key="caption.edgi.edit" /></caption>
    <tr>
        <th class="ln0"><f:message key="new.edgi.name" /></th>
        <td class="ln0">${item.name}</td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="new.edgi.url" /></th>
        <td class="ln0">${item.edgi.url}</td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="edgi.apps" /></th>
        <td class="ln0">

            <ul style="list-style: none">
            <c:forEach var="app" items="${item.edgi.job}">
                <li><b><c:out value="${app.name}" /></b> <f:message key="edgi.app" />
                    <ul>
                    <c:forEach var="vo" items="${app.vo}">
                        <li><f:message key="edgi.app.vo" />: <i><c:out value="${vo.name}" /></i>

                            <ul>
                            <c:forEach var="ce" items="${vo.ce}">
                                <li><f:message key="edgi.app.ce" />: <c:out value="${ce}" /></li>
                            </c:forEach>
                            </ul>
                        </li>
                    </c:forEach>
                    </ul>
                </li>
            </c:forEach>
            </ul>

        </td>
    </tr>
</table>
</f:bundle>