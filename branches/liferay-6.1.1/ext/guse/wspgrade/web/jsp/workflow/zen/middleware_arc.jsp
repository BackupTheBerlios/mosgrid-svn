<%--
arc job
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<%-- vo lista --%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.5" />:</div>
    <div class="jobconfig-data">
            <select title='<msg:getText key="config.job.grid" />' ${egrid} class="portlet-form-button" name="job_grid" id="job_grid">
            <c:forEach var="tmp0" items="${grids}">
                <c:choose>
                    <c:when test="${tmp0==sgrid}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
                    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
    </div>
</div>

<%-- all job panel --%>
<jsp:include page="/jsp/workflow/zen/useall.jsp" />
<jsp:include page="/jsp/workflow/zen/myjob.jsp" />
