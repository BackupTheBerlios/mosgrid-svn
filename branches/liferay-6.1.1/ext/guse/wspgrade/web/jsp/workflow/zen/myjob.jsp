<%-- 
sajat feltoltesu jobok eseten
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<%-- MPI/JAVA/Seq --%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.9" />:</div>
    <div class="jobconfig-data">
            <c:choose>
    		<c:when test="${type=='MPI'}">
        	    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" class="portlet-form-button" name="job_type" value="Sequence" onclick="javascript:document.getElementById('job_nodenumber').disabled=true;"><msg:getText key="text.job.10" />&nbsp;&nbsp;
            	    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" class="portlet-form-button" name="job_type" value="Java" onclick="javascript:document.getElementById('job_nodenumber').disabled=true;"><msg:getText key="text.job.11" />&nbsp;&nbsp;
            	    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" name="job_type" value="MPI" checked="true" onclick="javascript:document.getElementById('job_nodenumber').disabled=false;"><msg:getText key="text.job.12" />&nbsp;&nbsp;
    		</c:when>
        	<c:when test="${type=='Java'}">
            	    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" class="portlet-form-button" name="job_type" value="Sequence" onclick="javascript:document.getElementById('job_nodenumber').disabled=true;"><msg:getText key="text.job.10" />&nbsp;&nbsp;
            	    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" class="portlet-form-button" name="job_type" value="Java" checked="true" onclick="javascript:document.getElementById('job_nodenumber').disabled=true;"><msg:getText key="text.job.11" />&nbsp;&nbsp;
    		    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" class="portlet-form-button" name="job_type" value="MPI" onclick="javascript:document.getElementById('job_nodenumber').disabled=false;"><msg:getText key="text.job.12" />&nbsp;&nbsp;
        	</c:when>
            <c:otherwise>
            	    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" class="portlet-form-button" name="job_type" checked="true" value="Sequence" onclick="javascript:document.getElementById('job_nodenumber').disabled=true;"><msg:getText key="text.job.10" />&nbsp;&nbsp;
    		    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" class="portlet-form-button" name="job_type" value="Java" onclick="javascript:document.getElementById('job_nodenumber').disabled=true;"><msg:getText key="text.job.11" />&nbsp;&nbsp;
        	    <input title='<msg:getText key="config.job.type" />' ${etype} type="radio" class="portlet-form-button" name="job_type" value="MPI" onclick="javascript:document.getElementById('job_nodenumber').disabled=false;"><msg:getText key="text.job.12" />&nbsp;&nbsp;
            </c:otherwise>
            </c:choose>
            <c:if test="${job.labelType!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelType}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descType!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descType}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    
    </div>
</div>

<%-- MPI node number --%>
<div style="display:table-row;margin-top:1px;float:left;">    
    <div class="jobconfig-header"><msg:getText key="text.job.13" />:</div>
    <div class="jobconfig-data">
    	    <c:choose>
        	<c:when test="${type=='MPI'}">
                    <input title='<msg:getText key="config.job.nodenumber" />' type="text" ${enodenumber}  name="job_nodenumber" id="job_nodenumber" value="<c:out value="${nodenumber}" />" onkeyup="this.value = this.value.replace(/[^0-9]/g, '')">
    		</c:when>
        	<c:otherwise>
                <input title='<msg:getText key="config.job.nodenumber" />' type="text" disabled="false"  name="job_nodenumber" id="job_nodenumber" value="<c:out value="${nodenumber}" />">
    		</c:otherwise>
            </c:choose>            
    	    <c:if test="${job.labelNodeNumber!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelNodeNumber}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descNodeNumber!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descNodeNumber}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>    
    </div>
</div>

<%-- file upload --%>
<div style="display:table-row;margin-top:1px;float:left;">    
    <div class="jobconfig-header"><msg:getText key="text.job.14" />:</div>
    <div class="jobconfig-data">
            <msg:getText key="text.job.15" />: ${binary}<br />
            <input ${ebinary} title='<msg:getText key="config.job.binary" />'  type="file" name="job_${jobID}_binary" id="job_${jobID}_binary" />
            
            <c:if test="${job.labelBinary!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelBinary}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descBinary!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descBinary}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>

<%-- jobparameter --%>    
<div style="display:table-row;margin-top:1px;float:left;">    
    <div class="jobconfig-header"><msg:getText key="text.job.16" />:</div>
    <div class="jobconfig-data">
            <input title='<msg:getText key="config.job.params" />' type="text" ${eparams}  name="job_params" id="job_params" value="<c:out value="${params}" />">
            <c:if test="${job.labelParams!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelParams}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descParams!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descParams}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>

