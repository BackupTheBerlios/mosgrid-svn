<%--
TestManager portlet nyito oldal
List<FolderBean> ${folders} megjelenitendo konyvtarlista
--%>
<%@taglib  prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="testmanager"  >

<style>
.guse-tree-menu{
    display:block;
    float:left;
    width:200px;

}

.guse-close-folder{
    display:block;
    float:left;
    width:200px;
    background-image:url("${pageContext.request.contextPath}/img/test/mappa.gif");
}

</style>

<div class="guse-tree-menu">
<c:forEach var="t" items="${folders}">
    <div class="guse-close-folder">
        <img id="img_${t.id}" src="${pageContext.request.contextPath}/img/test/mappa.gif" alt="folder" />
        ${t.title}
        <div id="sub_${t.id}" class="guse-submenu"></div>
</div>
</c:forEach>
</div>
</f:bundle>