<%@ page import="javax.portlet.PortletURL" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<portlet:defineObjects/>
<%--
<% String webappURL=renderResponse.encodeURL(renderRequest.getContextPath()); %>
<c:set var="localURL" value="${webappURL}" scope="request" />
 --%>
<link href="${pageContext.request.contextPath}/css/portal30.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/css/portlet.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/props/form.css" rel="stylesheet"type="text/css"/>
<link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" type="text/css" />


<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/tooltip.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/props/props.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/designe.js"></script>
<script language="javascript" src="${pageContext.request.contextPath}/designe.js"> </script>
<script type="text/javascript" src="${pageContext.request.contextPath}/tinybox.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/excanvas.js"></script>

<portlet:resourceURL var="ajaxURL" />

<script>
    var webapp="${pageContext.request.contextPath}";
    var ajaxURL="${ajaxURL}";
</script>