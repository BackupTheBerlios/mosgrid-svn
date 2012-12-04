<%--
List ${data} konfiguralt csoportok/gridek/vo-k/tipusok
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<center>
<div class="edit_list">
<form id="edit" method="post" action="conf?t=${param['t']}" onsubmit="return validateEditForm()">
     <f:message key="new.${sessionScope['menu']}.name" />
 <select name="list" onchange="submitEditForm()">
     <option></option>
 <c:forEach var="t" items="${data}" >
     <c:choose>
         <c:when test="${param['list']==t.name}"><option selected="true">${t.name}</option></c:when>
         <c:otherwise><option>${t.name}</option></c:otherwise>
     </c:choose>
 </c:forEach>
 </select>
 </div>
 </form>
 <c:if test="${param['list']!=null}">
     <br />
     <jsp:include page="edit/${sessionScope['menu']}.jsp" />
 </c:if>
</center>
</f:bundle>