<%--
    Job configure
--%>
<%@taglib  prefix="p" uri="http://java.sun.com/portlet_2_0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<p:resourceURL var="ajaxURL" />
<div class="jobheader">
    <div style="display:table-row;margin-top:1px;float:left;" >
	<div class="jobconfig-header"><msg:getText key="text.global.1" />: </div>
	<div class="jobconfig-data"><c:out value="${jobname}" escapeXml="true"  /></div>
    </div>
    <div style="display:table-row;margin-top:1px;float:left;" >
	<div class="jobconfig-header"><msg:getText key="text.global.2" />: </div>
	<div class="jobconfig-data"><c:out value="${jobtxt}" escapeXml="true"  /></div>
    </div>
</div>
<ul id="nav">
	<c:choose>
	<c:when test="${(param.m=='GetJobView' ||param.m==null) && param.smsg==null }">
	<li class="shadownoborder borderradius config-gradient">
	    <span>
        	<img src="${pageContext.request.contextPath}/imgs/configure/job.png" /><br/>
        	[<msg:getText key="text.job.global.3" />]
    	    </span>
	</li>
	</c:when>
	<c:otherwise>
	<li class="shadow borderradius config-gradient">
    	    <a id="t1" href="#config-window" onclick="javascript:getConfForm('m=GetJobView&job=${jobname}');">
	        <img src="${pageContext.request.contextPath}/imgs/configure/job.png" /><br/>
        	[<msg:getText key="text.job.global.3" />]
	    </a>
	</li>
	</c:otherwise>
	</c:choose>
				    
	<c:choose>
	<c:when test="${param.m=='GetIOView' && param.smsg==null }"><c:set var="flag" value=" current" /> 
	    <li class="shadownoborder borderradius config-gradient">
	    <span>
    		<img src="${pageContext.request.contextPath}/imgs/configure/io.png" /><br/>
    		[<msg:getText key="text.job.global.4" />]	
    	    </span>
	    </li>
	</c:when>
	<c:otherwise>
	    <li class="shadow borderradius config-gradient">
	   	    <a id="t2" href="#config-window" onclick="javascript:getConfForm('m=GetIOView&job=${jobname}');">
        	<img src="${pageContext.request.contextPath}/imgs/configure/io.png" /><br/>
        	[<msg:getText key="text.job.global.4" />]	
	    </a>
	    </li>
	</c:otherwise>
	</c:choose>

	<c:choose>
	<c:when test="${param.m=='GetDescriptionView' }">
	    <li class="shadownoborder borderradius config-gradient">
	    <span>
        	<img src="${pageContext.request.contextPath}/imgs/configure/desc.png" /><br/>
        	[<msg:getText key="text.job.global.5" />]
    	    </span>
	</li>	
	</c:when>
	<c:otherwise>
	    <li class="shadow borderradius config-gradient">
	       	    <a id="t3" href="#config-window" onclick="javascript:getConfForm('m=GetDescriptionView&job=${jobname}');">
        	<img src="${pageContext.request.contextPath}/imgs/configure/desc.png" /><br/>
        	[<msg:getText key="text.job.global.5" />]	
	    </a>
	</li>
	</c:otherwise>
	</c:choose>
    
	<c:choose>
	<c:when test="${param.m=='GetHistoryView' }">
	    <li class="shadownoborder borderradius config-gradient">
	    <span>
                <img src="${pageContext.request.contextPath}/imgs/configure/desc.png" /><br/>
                [<msg:getText key="text.job.global.6" />]
            </span>
	    </li>
	</c:when>
	<c:otherwise>
	    <li class="shadow borderradius config-gradient">
	    <a id="t4" href="#config-window" onclick="javascript:getConfForm('m=GetHistoryView&job=${jobname}');">
                <img src="${pageContext.request.contextPath}/imgs/configure/desc.png" /><br/>
                [<msg:getText key="text.job.global.6" />]
            </a>
	    </li>
	</c:otherwise>
	</c:choose>
    </li>
</ul>





<%-- tartalom --%>
<div id="configcontents" class="borderradius shadownoborder">
<c:choose>
    <c:when test="${param.m=='GetJobView' && param.smsg==null }">
	<div class="config-gradient-invert borderradius" style="display:block;width:100%;height:60px;float:left">
	    <a style="float:right" href="${ajaxURL}&helptext=help.job" onclick="return false;" class="ajaxhelp" >
		<img src="${pageContext.request.contextPath}/img/help.gif" />
	    </a>
	</div>
        <jsp:include page="/jsp/workflow/zen/job.jsp" />
    </c:when>
    <c:when test="${param.m=='GetIOView' && param.smsg==null }">
	<div class="config-gradient-invert borderradius" style="display:block;width:100%;height:60px;float:left">
	    <a style="float:right" href="${ajaxURL}&helptext=help.io" onclick="return false;" class="ajaxhelp" >
		<img src="${pageContext.request.contextPath}/img/help.gif" />
	    </a>
	</div>
        <jsp:include page="/jsp/workflow/zen/io.jsp" />
    </c:when>
    <c:when test="${param.m=='GetDescriptionView' }">
	<div class="config-gradient-invert borderradius" style="display:block;width:100%;height:60px;float:left">
	    <a style="float:right" href="${ajaxURL}&helptext=help.description" onclick="return false;" class="ajaxhelp" >
		<img src="${pageContext.request.contextPath}/img/help.gif" />
	    </a>
	</div>
        <jsp:include page="/jsp/workflow/zen/description.jsp" />
    </c:when>
    <c:when test="${param.m=='GetHistoryView' }">
	<div class="config-gradient-invert borderradius" style="display:block;width:100%;height:60px;float:left">
	    <a style="float:right" href="${ajaxURL}&helptext=help.help" onclick="return false;" class="ajaxhelp" >
		<img src="${pageContext.request.contextPath}/img/help.gif" />
	    </a>
	</div>
        <jsp:include page="/jsp/workflow/zen/history.jsp" />
    </c:when>
    <c:when test="${param.smsg!=null}">
	<div class="config-gradient-invert borderradius" style="display:block;width:100%;height:60px;float:left">
	    <a style="float:right" href="${ajaxURL}&helptext=help.job" onclick="return false;" class="ajaxhelp" >
		<img src="${pageContext.request.contextPath}/img/help.gif" />
	    </a>
	</div>
        <center><msg:getText key="portal.config.saveingconfig" /></center>
    </c:when>
</c:choose>
</div>



