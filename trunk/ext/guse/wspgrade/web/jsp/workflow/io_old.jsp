<%--JOB tulajdonsag szerkesztes UI
${jobID} job neve
--%>

<%@ page contentType="text/html;charset=Shift_JIS" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<%--<msg:help id="helptext" tkey="help.job" img="${pageContext.request.contextPath}/img/help.gif" />--%>




<%-- Job tipus valaszto(wf,service,job,metabroker)--%>




	<div id='page'>
<%--		<center class="shadow"><msg:getText key="text.job.model" /> </center> --%>
		<div class='menu'>

<c:if test="${fn:length(workflows) > 0}">
    <a class="shadow" href="javascript:jobtypepanelhide('jobisworkflow');"><img id="md0" src='${pageContext.request.contextPath}/imgs/configure/workflow.png' title='Workflow|<msg:getText key="text.job.0" />' alt='' /></a>
</c:if>

<c:if test="${fn:length(servicetypes) > 0}">
    <a class="shadow" href="javascript:jobtypepanelhide('jobisservice');"><img id="md1" src='${pageContext.request.contextPath}/imgs/configure/service.png' title='Service|<msg:getText key="text.job.1" />' alt='' /></a>
</c:if>

<c:if test="${fn:length(gridtypes) > 0}">
    <a class="shadow" href="javascript:jobtypepanelhide('jobisbinary');"><img id="md2" src='${pageContext.request.contextPath}/imgs/configure/binary.png' title='Binary|<msg:getText key="text.job.2" />' alt='' /></a>
</c:if>

<c:if test="${fn:length(cloudtypes) > 0}">
    <a class="shadow" href="javascript:jobtypepanelhide('jobiscloud');"><img id="md3" src='${pageContext.request.contextPath}/imgs/configure/cloud.png' title='Cloud|<msg:getText key="text.job.3" />' alt='' /></a>
</c:if>

		</div>
	</div>


<%-- please wait  --%>

<div id="div_wait" class="shape" style="display:none;position:fixed;left:30%;top:40%" >
    <div class="hdn_txt" id="div_wait_txt">
	 <br><br><br><msg:getText key="please.wait" /><br><br><br>
    </div>
</div>


<%-- specialis konfiguracios oldalak --%>
<%--workflow--%>
<c:if test="${fn:length(workflows) > 0}">
<c:choose>
    <c:when test="${iworkflow!='null'}">
        <script>$('configmenu_workflow').style.setProperty("background-color", "red","important");</script>
        <div id="jobisworkflow" style="display:block" >
        <jsp:include page="/jsp/workflow/zen/job_workflow.jsp" />
        </div>
            <script>
        	url=$("#md0").attr("src");
        	$("#md0").attr("src",url+".selected.png");
    	    </script>
    </c:when>
    <c:otherwise>
        <div id="jobisworkflow" style="display:none" >
        <jsp:include page="/jsp/workflow/zen/job_workflow.jsp" />
        </div>
    </c:otherwise>
</c:choose>
</c:if>

<%--service--%>
<c:choose>
<c:when test="${fn:length(servicetypes) > 0}">
    <c:choose>
        <c:when test="${iservice!='null'}">
            <script>$('configmenu_service').style.setProperty("background-color", "red","important");</script>
            <div id="jobisservice" style="display:block" >
            <jsp:include page="/jsp/workflow/zen/job_service.jsp" />
            </div>
            <script>
        	url=$("#md1").attr("src");
        	$("#md1").attr("src",url+".selected.png");
    	    </script>
        </c:when>
        <c:otherwise>
            <div id="jobisservice" style="display:none" >
            <jsp:include page="/jsp/workflow/zen/job_service.jsp" />
            </div>
        </c:otherwise>
    </c:choose>
</c:when>
<c:otherwise><div id="jobisservice" style="display:none" ></div></c:otherwise>
</c:choose>

<%--cloud--%>
<c:choose>
<c:when test="${fn:length(cloudtypes) > 0}">
    <c:choose>
        <c:when test="${icloud!=null}">
            <script>$('configmenu_cloud').style.setProperty("background-color", "red","important");</script>
            <div id="jobiscloud" style="display:block" >
            <jsp:include page="/jsp/workflow/zen/job_cloud.jsp" />
            </div>
            <script>
        	url=$("#md3").attr("src");
        	$("#md3").attr("src",url+".selected.png");
    	    </script>

        </c:when>
        <c:otherwise>
            <div id="jobiscloud" style="display:none" >
            <jsp:include page="/jsp/workflow/zen/job_cloud.jsp" />
            </div>
        </c:otherwise>
    </c:choose>
</c:when>
<c:otherwise><div id="jobiscloud" style="display:none" ></div></c:otherwise>
</c:choose>



<%--middleware valasztas--%>
<c:choose>
<c:when test="${fn:length(gridtypes) > 0}">
    <c:choose>
        <c:when test="${(iservice=='null')&&(iworkflow=='null')&&(icloud==null)}">
            <div id="jobisbinary" style="display:block" class="shadow">
                <jsp:include page="/jsp/workflow/zen/job_grid.jsp" />
            </div>
            <script>
        	url=$("#md2").attr("src");
        	$("#md2").attr("src",url+".selected.png");
    	    </script>
        </c:when>
        <c:otherwise>
            <div id="jobisbinary" style="display:none"  class="shadow">
                <jsp:include page="/jsp/workflow/zen/job_grid.jsp" />
            </div>
        </c:otherwise>
    </c:choose>
</c:when>
    <c:otherwise><div id="jobisbinary" style="display:none" ></div></c:otherwise>
 </c:choose>

   