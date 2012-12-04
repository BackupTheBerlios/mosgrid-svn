<%-- 
List<String> ${sessionScope.grids} elerheto VO-k listaja
--%>
<%@taglib  prefix="p" uri="http://java.sun.com/portlet_2_0" %>
<script src="${pageContext.request.contextPath}/js/ajax.js"> </script>
<p:actionURL var="pURL" >
    <p:param name="guse" value="doNew" />
</p:actionURL>
<jsp:include page="/WEB-INF/jsp/resourceadmin/smallist.jsp" />

<form method="post" action="${pURL}" >
    <div id="glite-config">
        <jsp:include page="/WEB-INF/jsp/resourceadmin/middlewares/glite_config.jsp" />
    </div>
    <input type="submit" value="Save" />
</form>