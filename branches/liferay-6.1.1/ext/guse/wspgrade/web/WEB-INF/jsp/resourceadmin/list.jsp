<%--
Eroforraslistak
String ${sessionScope.midleware} kivalasztott midleware
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet_2_0" %>
 <%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%--
Resource lista megjelenitesi link
--%>
<c:set var="rlsize" value="0" />
<c:forEach var="r" items="${resouceslist}">
    <c:set var="rlsize" value="1" />
</c:forEach>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="resource"  >

<p:actionURL var="pURL" >
    <p:param name="guse" value="doNew" />
</p:actionURL>

<p:resourceURL var="rURL" />
<script> var ajaxURL="${rURL}"; </script>

<table border="1" width="100%">
    <tr>
        <th><f:message key="head.group.${sessionScope.midleware}" /></th>
        <th><f:message key="head.actions" /></th>
    </tr>
<c:forEach var="t" items="${sessionScope.grids}" >
    <tr>
        <td><c:out value="${t}" escapeXml="true" /></td>
        <td>
            <p:renderURL var="gnURL">
                <p:param name="nres" value="${t}" />
            </p:renderURL>
            <a href="${gnURL}" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/add.png" /><br />
                [<f:message key="action.new" />]
            </a>
            <p:renderURL var="glURL">
                 <p:param name="lres" value="${t}" />
            </p:renderURL>
            <a href="${glURL}" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/type.png" /><br />
                [<f:message key="action.resources" />]
            </a>
        </td>
     </tr>
     <c:if test="${t==param.nres}">

     <tr>
         <td colspan="2">
         <p:actionURL var="anURL">
             <p:param name="guse" value="doNewResource" />
         </p:actionURL>

            <form method="post" action="${anURL}">
             <table  style="background-color:#eeeeee" border="1">
                 <caption><f:message key="catpion.new.${sessionScope.midleware}" /></caption>
                 <tr>
                     <th><f:message key="head.site.${sessionScope.midleware}" /></th>
                     <td><input type="text" name="psite" /></td>
                 </tr>
                 <tr>
                     <th><f:message key="head.job-manager.${sessionScope.midleware}" /></th>
                     <td><input type="text" name="pjman" /></td>
                 </tr>
                 <tr>
                     <td colspan="2"><input type="submit" value="<f:message key="action.submit" />"  /></td>
                 </tr>
             </table>
         </form>
         </td>
     </tr>
     </c:if>

     <c:if test="${t==param.lres}">
     <tr>
         <td colspan="2">
         <c:choose>
             <c:when test="${rlsize=='0'}">
                 <center><f:message key="text.noresource" /></center>
             </c:when>
             <c:otherwise>

             <table style="background-color:#eeeeee" border="1">
                 <caption><f:message key="caption.resourcelist" /></caption>
                 <c:forEach var="r" items="${resouceslist}">
                 <tr>
                     <td><c:out escapeXml="true" value="${r.site}" /></td>
                     <td><c:out escapeXml="true" value="${r.jobmanager}" /></td>
                     <td>
                         <p:actionURL var="dsURL">
                             <p:param name="guse" value="doDeleteResource" />
                            <p:param name="id" value="${r.id}" />
                         </p:actionURL>
                        <a href="#" class="icolink" onclick="TINY.box.show(popUPLink('${dsURL}','<f:message key="delete.msg" />'),0,300,300,1);">
                        <img src="${pageContext.request.contextPath}/imgs/serviceadmin/delete.png" /><br />
                        [<f:message key="action.delete" />]</a>
                     </td>
                 </tr>
                 </c:forEach>
             </table>
             </c:otherwise>
         </c:choose>
         </td>
     </tr>
     </c:if>

</c:forEach>

</table>
</f:bundle>
