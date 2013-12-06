<%-- 
gt2,4 middleware config
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>


<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.5" />:</div>
    <div class="jobconfig-data">
            <select title='<msg:getText key="config.job.grid" />' ${egrid} class="portlet-form-button" name="job_grid" id="job_grid" onChange="getRemoteSelectOptions('mw='+document.getElementById('job_gridtype').value+'&j='+document.getElementById('job_grid').value+'&m=GetResource')">
            <c:forEach var="tmp0" items="${grids}">
                <c:choose>
                    <c:when test="${tmp0==sgrid}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
                    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
            <c:if test="${job.labelGrid!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelGrid}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descGrid!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descGrid}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>


<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.6" />:</div>
    <div class="jobconfig-data">
            <select title='<msg:getText key="config.job.resource" />' ${eresource} class="portlet-form-button" name="job_resource" id="job_resource" onChange="getRemoteSelectOptions('mw='+document.getElementById('job_gridtype').value+'&g='+document.getElementById('job_grid').value+'&j='+document.getElementById('job_resource').value+'&m=GetData');">
            <c:forEach var="tmp0" items="${resources}"  >
                <c:choose>
                    <c:when test="${tmp0==sresource}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
                    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
            <c:if test="${job.labelResource!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelResource}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descResource!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descResource}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>


<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.7" />:</div>
    <div class="jobconfig-data">
            <select  title='<msg:getText key="config.job.jobmanager" />'${ejobmanager} class="portlet-form-button" name="job_jobmanager" id="job_jobmanager" >
            <c:forEach var="tmp0" items="${rdata}">
                    <c:choose>
                    <c:when test="${tmp0==sdata}"><option selected="true">${tmp0}</option></c:when>
                    <c:otherwise><option>${tmp0}</option></c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
            <c:if test="${job.labelJobManager!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelJobManager}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descJobManager!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descJobManager}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>


<%-- all job panel --%>
<jsp:include page="/jsp/workflow/zen/useall.jsp" />
<jsp:include page="/jsp/workflow/zen/myjob.jsp" />
