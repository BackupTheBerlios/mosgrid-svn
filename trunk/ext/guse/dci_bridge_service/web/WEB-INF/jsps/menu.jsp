

<%--
@call index.jsp

DCI Bridge comfig menu

String[] ${dcis} name of the supported middleware
String[] ${menu} name of the middleware actions
String[] ${dmenu} name of the DCI-Bridge actions
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<div id="header">
	<ul id="primary">

    <c:choose>
        <c:when test="${(sessionScope['menu']==null) || (param['menu']=='system') || (sessionScope['menu']=='system')}">
            <li><span><img src="imgs/middlewares/system.png" /><br /><f:message key="name.system" /></span>
                    <ul id="secondary">
                    <c:forEach var="t" items="${dmenu}">
                        <li>
                        <c:choose>
                            <c:when test="${t==param['t']}">
                                <span><f:message key="action.${t}" /></span>
                            </c:when>
                            <c:otherwise>
                                <a href="conf?t=${t}"><f:message key="action.${t}" /></a>
                            </c:otherwise>
                        </c:choose>
                        </li>
                    </c:forEach>
                    </ul>
            </li>
        </c:when>
        <c:otherwise>
            <li><a href="conf?menu=system"><img src="imgs/middlewares/system.png" /><br /><f:message key="name.system" /></a></li>
        </c:otherwise>
    </c:choose>

    <c:forEach var="dci" items="${dcis}">
        <c:choose>
            <c:when test="${(param['menu']==dci) || (sessionScope['menu']==dci)}">
                <li><span> <img src="imgs/middlewares/${dci}.png" /><br /><f:message key="name.${dci}" /></span>
                    <ul id="secondary">
                    <c:forEach var="t" items="${menu}">
                        <li>
                        <c:choose>
                            <c:when test="${t==param['t']}">
                                <span><f:message key="action.${t}" /></span>
                            </c:when>
                            <c:otherwise>
                                <a href="conf?t=${t}"><f:message key="action.${t}" /></a>
                            </c:otherwise>
                        </c:choose>
                        </li>
                    </c:forEach>
                    </ul>
                </li>
            </c:when>
            <c:otherwise>
                <li><a href="conf?menu=${dci}"><img src="imgs/middlewares/${dci}.png" /><br /><f:message key="name.${dci}" /></a></li>
            </c:otherwise>
        </c:choose>
    </c:forEach>

</ul>
</div>
</f:bundle>
