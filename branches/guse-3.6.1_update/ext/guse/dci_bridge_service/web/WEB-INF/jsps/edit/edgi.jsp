<%--
edit edgi vo
Glite ${item} selected VO config
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<table class="newdata" >
    <caption><f:message key="caption.edgi.edit" /></caption>
    <form id="editForm" method="post" action="conf?editing=${item.name}">
    <tr>
        <th class="ln0"><f:message key="new.edgi.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="pedginame" size="25" value="${item.name}" /></td>
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
        <th class="ln0"><f:message key="new.edgi.url" /></th>
        <td class="ln0"><input class="ln1" type="text" name="pedgiurl" size="60" value="${item.edgi.url}" /></td>
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



    <jsp:include page="submit_button.jsp" />
</form>
</table>
</f:bundle>