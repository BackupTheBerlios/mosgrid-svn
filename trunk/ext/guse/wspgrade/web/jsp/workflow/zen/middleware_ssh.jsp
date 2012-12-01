<%--
ssh middleware config (pbs, lsf)
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<input type="hidden" name="job_grid" id="job_grid" value="${sgrid}" />


<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.6" />:</div>
    <div class="jobconfig-data">
            <%-- getRemoteSelectOptions('g='+this.value+'&m=GetSshUserForHost')--%>
            <select title='<msg:getText key="config.job.grid" />' ${egrid} class="portlet-form-button" name="job_grid" id="job_grid" onChange="getRemoteSelectOptions('mw='+document.getElementById('job_gridtype').value+'&j='+this.value+'&m=GetResource');">
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
    <div class="jobconfig-header"><msg:getText key="text.job.7" />:</div>
    <div class="jobconfig-data">
            <select title='<msg:getText key="config.job.resource" />' ${eresource} class="portlet-form-button" name="job_resource" id="job_resource" >
            <c:forEach var="tmp0" items="${resources}"  >
                <c:choose>
                    <c:when test="${tmp0==sresource}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
                    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
            <msg:toolTip id="cicc" tkey="config.job.resource" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <c:if test="${job.labelResource!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelResource}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descResource!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descResource}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>

<input type="hidden" value="-" name="job_jobmanager" id="job_jobmanager" >
<%--<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="publickey.useraccount" />:</div>
    <div class="jobconfig-data">
            <select title='<msg:getText key="config.job.jobmanager" />' ${ejobmanager} class="portlet-form-button" name="job_jobmanager" id="job_jobmanager" >
            <c:forEach var="tmp0" items="${rdata}">
                    <c:choose>
                    <c:when test="${tmp0==sdata}"><option selected="true">${tmp0}</option></c:when>
                    <c:otherwise><option>${tmp0}</option></c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
            <msg:toolTip id="cicc" tkey="config.job.jobmanager" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <c:if test="${job.labelJobManager!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelJobManager}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descJobManager!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descJobManager}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>--%>


<%-- all job panel --%>
<jsp:include page="/jsp/workflow/zen/useall.jsp" />
<jsp:include page="/jsp/workflow/zen/myjob.jsp" />
