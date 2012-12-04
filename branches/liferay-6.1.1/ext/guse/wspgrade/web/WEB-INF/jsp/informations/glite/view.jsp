<%--
gLite VO-k informacios weblapjainak megjelenitese
List<String> ${glitevos} elerheto VO lista
HashMap ${props} vo konfiguracios beallitasok
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet_2_0" %>

    <style type="text/css" media="screen">@import "${pageContext.request.contextPath}/css/tabs.css";</style>
    <br />
    <br />
    <br />
    
<div id="header">
	<ul id="primary">
        <li>
            <span>
                <img src="${pageContext.request.contextPath}/imgs/midlewares/glite.png" /><br/>
                [glite]
            </span>
			<ul id="secondary">
            <c:forEach var="t" items="${glitevos}" varStatus="ln">
                <c:choose>
                    <c:when test="${t==param.vo}">
                        <li><span>${t}</span></li>
                    </c:when>
                    <c:otherwise>
                        <p:renderURL var="pURL">
                            <p:param name="vo" value="${t}" />
                        </p:renderURL>
                        <li><a href="${pURL}">${t}</a></li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
			</ul>
		</li>
	</ul>
</div>
<div id="main">
	<div id="contents">
    <c:choose>
    <c:when test="${param.vo==null}">
        Select vo
    </c:when>
    <c:otherwise>
        <jsp:include page="config.jsp" />
    </c:otherwise>
    </c:choose>
	</div>
</div>