<%--
@author: krisztian karoczkai
configuration panel for edgi job
List<Items> ${ars} list of EDGI ARs
JobPropertyBean ${cjob} kivalasztott job
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<script>
function saveAndReload(pJob){
    setRemoteParamDOMValue('job_gridtype,job_ar,job_arapp,job_grid,job_resource,job_gliterole,job_params');
    getConfForm('m=GetJobView&job='+pJob);
}
</script>

<%-- List of AR --%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.ar" /></div>
    <div class="jobconfig-data">
        <c:set var="propertydisabled" value="" />
        <c:if test="${cjob.exeDisabled['ar']!=null}"><c:set var="propertydisabled" value="disabled=\"disabled\"" /></c:if>
        <select title='<msg:getText key="config.job.ar" />' ${propertydisabled} class="portlet-form-button" name="job_ar" id="job_ar" onchange="setRemoteParamDOMValue('job_gridtype,job_ar,job_arapp,job_grid,job_resource,job_gliterole,job_params');getConfForm('m=GetJobView&job=${jobID}');">
            <c:forEach var="tmp0" items="${ars}">*
                <c:choose>
                    <c:when test="${tmp0.name==job_ar}">
                	<option selected="true" value="${tmp0.name}">${tmp0.name}</option>
                	<c:set var="selectedItem" value="${tmp0.edgi}" />
                	<c:set var="selectedJob" value="${selectedItem.job[0]}" />
                	<c:set var="selectedVO" value="null" />
            	    </c:when>
                    <c:otherwise><option value="${tmp0.name}">${tmp0.name}</option></c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
    </div>
</div>

<%-- List of Jobs --%>
<c:if test="${selectedItem==null}" >
    <c:catch><c:set var="selectedItem" value="${ars[0].edgi}" /> </c:catch>
</c:if>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.boincjob" /></div>
    <div class="jobconfig-data">
        <c:set var="propertydisabled" value="" />
        <c:if test="${cjob.exeDisabled['arapp']!=null}"><c:set var="propertydisabled" value="disabled=\"disabled\"" /></c:if>
        <select title='<msg:getText key="config.job.arapp" />' ${propertydisabled} class="portlet-form-button" name="job_arapp" id="job_arapp" onchange="setRemoteParamDOMValue('job_gridtype,job_ar,job_arapp,job_grid,job_resource,job_gliterole,job_params');getConfForm('m=GetJobView&job=${jobID}');">
            <c:forEach var="tmp0" items="${selectedItem.job}">
                <c:choose>
                    <c:when test="${tmp0.name==job_arapp}">
                	<option selected="true" value="${tmp0.name}">${tmp0.name}</option>
                	<c:set var="selectedJob" value="${tmp0}" />
                	<c:set var="selectedVO" value="${selectedJob.vo[0]}" />
            	    </c:when>
                    <c:otherwise><option value="${tmp0.name}">${tmp0.name}</option></c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
    </div>
</div>


<%-- List of VOs --%>
 <%-- temmporary of selected VO --%>
<c:if test="${selectedJob==null}" >
    <c:catch><c:set var="selectedJob" value="${selectedItem.job[0]}" /> </c:catch>
</c:if>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.5" /></div>
    <div class="jobconfig-data">
        <c:set var="propertydisabled" value="" />
        <c:if test="${cjob.exeDisabled['grid']!=null}"><c:set var="propertydisabled" value="disabled=\"disabled\"" /></c:if>
        <select title='<msg:getText key="config.job.grid" />' ${propertydisabled} class="portlet-form-button" name="job_grid" id="job_grid" onchange="setRemoteParamDOMValue('job_gridtype,job_ar,job_arapp,job_grid,job_resource,job_gliterole,job_params');getConfForm('m=GetJobView&job=${jobID}');">
            <c:forEach var="tmp0" items="${selectedJob.vo}">
                <c:choose>
                    <c:when test="${tmp0.name==job_grid}">
                	<option selected="true" value="${tmp0.name}">${tmp0.name}</option>
                	<c:set var="selectedVO" value="${tmp0}" />
            	    </c:when>
                    <c:otherwise><option value="${tmp0.name}">${tmp0.name}</option></c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
    </div>
</div>


<%-- List of CEs --%>
<c:if test="${selectedVO=='null'}" >
    <c:catch><c:set var="selectedVO" value="${selectedJob.vo[0]}" /> </c:catch>
</c:if>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.6" /></div>
    <div class="jobconfig-data">
        <c:set var="propertydisabled" value="" />
        <c:if test="${cjob.exeDisabled['resource']!=null}"><c:set var="propertydisabled" value="disabled=\"disabled\"" /></c:if>
        <select title='<msg:getText key="config.job.resource" />' ${propertydisabled} class="portlet-form-button" name="job_resource" id="job_resource">
            <c:forEach var="tmp0" items="${selectedVO.ce}">*
                <c:choose>
                    <c:when test="${tmp0==job_resource}">
                	<option selected="true" value="${tmp0}">${tmp0}</option>
            	    </c:when>
                    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
    </div>
</div>

<%-- glite role --%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.gliterole" />:</div>
    <div class="jobconfig-data">
        <c:set var="propertydisabled" value="" />
        <c:if test="${cjob.exeDisabled['gliterole']!=null}"><c:set var="propertydisabled" value="disabled=\"disabled\"" /></c:if>
            <input title='<msg:getText key="config.job.gliterole" />' type="text" ${propertydisabled}  name="job_gliterole" id="job_gliterole" value="<c:out value="${gliterole}" />">
    </div>
</div>


<%-- all job panel --%>
<jsp:include page="/jsp/workflow/zen/useall.jsp" />
<%-- jobparameter --%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.16" />:</div>
    <div class="jobconfig-data">
        <c:set var="propertydisabled" value="" />
        <c:if test="${cjob.exeDisabled['params']!=null}"><c:set var="propertydisabled" value="disabled=\"disabled\"" /></c:if>
            <input title='<msg:getText key="config.job.params" />' type="text" ${propertydisabled}  name="job_params" id="job_params" value="<c:out value="${params}" />">
            <c:if test="${job.labelParams!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelParams}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descParams!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descParams}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>
