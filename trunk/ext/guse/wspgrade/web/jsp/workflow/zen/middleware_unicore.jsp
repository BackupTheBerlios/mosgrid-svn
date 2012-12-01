<%--
unicore job
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<%-- vo lista --%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.5" />:</div>
    <div class="jobconfig-data">
            <select  title='<msg:getText key="config.job.grid" />' ${egrid} class="portlet-form-button" name="job_grid" id="job_grid"
                    onChange="getRemoteSelectOptions('j='+document.getElementById('job_grid').value+'&m=GetUnicoreIDBTools');">
            <c:forEach var="tmp0" items="${grids}">
                <c:choose>
                    <c:when test="${tmp0==sgrid}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
                    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
    </div>
</div>

<input type="hidden" name="job_resource" id="job_resource" value="${sgrid}">

<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header">Tools:</div>
    <div class="jobconfig-data">
        <select ${ejobmanager} class="portlet-form-button" name="job_jobmanager" id="job_jobmanager" >
        <c:forEach var="tmp0" items="${idbToolList}">
                <c:choose>
                <c:when test="${tmp0==jobmanager}"><option selected="true">${tmp0}</option></c:when>
                <c:otherwise><option>${tmp0}</option></c:otherwise>
            </c:choose>
        </c:forEach>
        </select>
    </div>
</div>

<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header">Execute parser:</div>
    <div class="jobconfig-data">
        <input type="checkbox" class="portlet-form-button" name="job_jobparser" id="job_jobparser" value="${jobparser}">
    </div>
</div>

<%-- all job panel --%>
<jsp:include page="/jsp/workflow/zen/useall.jsp" />

<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header">Copy job names to tools:</div>
    <div class="jobconfig-data">
        <input type="checkbox" class="portlet-form-button" name="job_jobnamestotools" id="job_jobnamestotools" value="${jobname}">
        <msg:toolTip id="cicc" tkey="config.job.usealljob" img="${pageContext.request.contextPath}/img/tooltip.gif" />
    </div>
</div>

<jsp:include page="/jsp/workflow/zen/myjob.jsp" />

