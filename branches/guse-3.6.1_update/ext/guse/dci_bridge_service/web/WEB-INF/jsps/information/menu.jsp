<%--
@call index.jsp

DCI Bridge comfig menu

List<Middleware> ${conf} name of the superted middleware
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<div id="header">
	<ul id="primary">
    <c:forEach var="dci" items="${conf.middleware}">
        <c:if test="${dci.enabled}">
        <c:choose>
            <c:when test="${(param['menu']==dci.type) || (sessionScope['menu']==dci.type)}">
                <li><span> <img src="imgs/middlewares/${dci.type}.png" /><br /><f:message key="name.${dci.type}" /></span>
                    <ul id="secondary">
                    <c:forEach var="t" items="${dci.item}">
                        <li>
                        <c:choose>
                            <c:when test="${t==param['t']}">
                                <span>${t.name}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="information?t=${t.name}">${t.name}</a>
                            </c:otherwise>
                        </c:choose>
                        </li>
                    </c:forEach>
                    </ul>
                </li>
            </c:when>
            <c:otherwise>
                <li><a href="information?menu=${dci.type}"><img src="imgs/middlewares/${dci.type}.png" /><br /><f:message key="name.${dci.type}" /></a></li>
            </c:otherwise>
        </c:choose>
        </c:if>
    </c:forEach>
</ul>
</div>
</f:bundle>
