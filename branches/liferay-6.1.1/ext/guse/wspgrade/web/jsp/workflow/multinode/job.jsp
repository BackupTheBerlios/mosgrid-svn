<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- JOB tulajdonsag szerkesztes UI -->




<%@ page contentType="text/html;charset=Shift_JIS" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<script>
<!--
    var jobID="${jobID}";     
    
//-->
 		    
</script> 

<input type="hidden" id="pgridtype" value="${sgridtype}">

<div>
<tablewidth="100%" class="window-main">
    <tr>
	<td width="20%" class="kback"><div class="bold">Phase's name: </div></td>
	<td width="80%" class="kback">${jobname}</td>
    </tr>
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.global.2" />: </div></td>
	<td width="80%" class="kback">${jobtxt}</td>
    </tr>
</table>
<tablewidth="100%">
    <tr>
	<td>
	    <font class="clink">Phase definition &nbsp;&nbsp;</font>
	    <a href="javascript:getConfForm('m=GetIOView')"  class="clink" style="color:#ffffff">Flow Connectors </a> &nbsp;&nbsp;
	</td>
    </tr>
</table>

<div  style="position:relative;border:1px solid #ad2e27; width:100%; height:400px;overflow:auto;">
        
    <table width="100%" "><caption class="khead">WF properies</caption>
    <tr><td><msg:help id="helptext" tkey="help.phase.phase" img="${pageContext.request.contextPath}/img/help.gif" /></td></tr>
   
    
<%--    <c:choose>
    <c:when test="${error==null}">--%>
    <tr>
	<td width="20%"><msg:getText key="text.phase.projectlist" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.projectlist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <select name="job_project" id="job_project" onChange="javascript:document.getElementById('div_wait').style.display='block';setRemoteParamDOMValue('job_project,job_subsystem,job_component');getConfForm('m=GetJobView&job=${jobID}');">
            <option></option>    
	    <c:forEach var="tmp0" items="${projects}">
		<c:choose>
		    <c:when test="${tmp0.name==sproject}"><option selected="true" value="${tmp0.name}">${tmp0.displayName}</option></c:when>
		    <c:otherwise><option value="${tmp0.name}">${tmp0.displayName}</option></c:otherwise>
		</c:choose>    
	    </c:forEach>
	    </select>    
	</td>
    </tr> 
    
    <tr>
	<td width="20%"><msg:getText key="text.phase.subsystem" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.subsystem" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <input type="text" name="job_subsystem" id="job_subsystem" size="30" maxlength="150" value="${ssubsystem}" onblur="javascript: document.getElementById('div_wait').style.display='block'; setRemoteParamDOMValue('job_subsystem,job_component'); getConfForm('m=GetJobView&job=${jobID}');" >
            <select name="job_ssubsystem" id="job_ssubsystem" onChange="javascript: document.getElementById('job_subsystem').value=document.getElementById('job_ssubsystem').value; document.getElementById('div_wait').style.display='block'; setRemoteParamDOMValue('job_subsystem,job_component'); getConfForm('m=GetJobView&job=${jobID}');">
            <option></option>    
	    <c:forEach var="tmp0" items="${subsystems}">
		<c:choose>
		    <c:when test="${tmp0.name==ssubsystem}"><option selected="true" value="${tmp0.name}">${tmp0.name} (${tmp0.displayName})</option></c:when>
		    <c:otherwise><option value="${tmp0.name}">${tmp0.name} (${tmp0.displayName})</option></c:otherwise>                                        
		</c:choose>    
	    </c:forEach>
	    </select>    
	</td>
    </tr>     

    <tr>
	<td width="20%"><msg:getText key="text.phase.component" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.component" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <input type="text" name="job_component" id="job_component" size="30" maxlength="150" value="${scomponent}" onblur="javascript: document.getElementById('div_wait').style.display='block';setRemoteParamDOMValue('job_component');getConfForm('m=GetJobView&job=${jobID}');" >            
            <select name="job_scomponent" id="job_scomponent" onChange="javascript: document.getElementById('job_component').value=document.getElementById('job_scomponent').value; document.getElementById('div_wait').style.display='block';setRemoteParamDOMValue('job_component');getConfForm('m=GetJobView&job=${jobID}');">
            <option></option>    
	    <c:forEach var="tmp0" items="${components}">
		<c:choose>
		    <c:when test="${tmp0.name==scomponent}"><option selected="true" value="${tmp0.name}">${tmp0.name} (${tmp0.displayName})</option></c:when>
		    <c:otherwise><option value="${tmp0.name}">${tmp0.name} (${tmp0.displayName})</option></c:otherwise>                    
		</c:choose>    
	    </c:forEach>
	    </select>    
	</td>
    </tr>      

    <tr>
	<td width="20%"><msg:getText key="text.phase.configuration" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.configuration" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <input type="text" name="job_configuration" id="job_configuration" size="30" maxlength="150" value="${sconfiguration}">
            <select name="job_sconfiguration" id="job_sconfiguration" onChange="javascript: document.getElementById('job_configuration').value=document.getElementById('job_sconfiguration').value; ">
            <option></option>    
	    <c:forEach var="tmp0" items="${configurations}">
		<c:choose>
		    <c:when test="${tmp0.name==sconfiguration}"><option selected="true" value="${tmp0.name}">${tmp0.name} (${tmp0.displayName})</option></c:when>
		    <c:otherwise><option value="${tmp0.name}">${tmp0.name} (${tmp0.displayName})</option></c:otherwise>
                </c:choose>    
	    </c:forEach>
	    </select>    
	</td>
    </tr> 
<%--
    <tr>
	<td width="30%"><msg:getText key="text.phase.service" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.service" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <input type="text" name="job_service" id="job_service" value="${service}" />              
            <select name="job_services" id="job_services" onChange="javascript:document.getElementById('job_service').value=this.value">
                <option value="">Existing services..</option> 
	    <c:forEach var="tmp0" items="${services}">		
		    <option value="${tmp0.key}">${tmp0.key}</option>
	    </c:forEach>
            </select>
	</td>
    </tr> 
--%>
    <tr>
	<td width="20%"><msg:getText key="text.phase.node" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.node" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <select name="job_node" id="job_node" >
            <option></option>    
	    <c:forEach var="tmp0" items="${nodes}">
		<c:choose>
		    <c:when test="${tmp0.key==snode}"><option selected="true" value="${tmp0.key}">${tmp0.key}</option></c:when>
		    <c:otherwise><option value="${tmp0.key}">${tmp0.key}</option></c:otherwise>
		</c:choose>    
	    </c:forEach>
	    </select>    
	</td>
    </tr>     





<%--
    <tr>
	<td width="30%"><div class="iodline"/> </td>
	<td>
            <div class="iodline"/>
	</td>
    </tr> 
    
    <tr>
	<td width="30%">Test <msg:getText key="text.phase.subsystem" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.tsubsystem" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <input type="text" name="job_tsubsystem" id="job_tsubsystem" value="${tsubsystem}">
	</td>
    </tr>     

    <tr>
	<td width="30%">Test <msg:getText key="text.phase.component" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.tcomponent" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <input type="text" name="job_tcomponent" id="job_tcomponent" value="${tcomponent}">
	</td>
    </tr>      

    <tr>
	<td width="30%">Test <msg:getText key="text.phase.configuration" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.tconfiguration" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <input type="text" name="job_tconfiguration" id="job_tconfiguration" value="${tconfiguration}">
	</td>
    </tr> 
--%>  





    <tr>
	<td width="20%"><msg:getText key="text.phase.script" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.phase.script" img="${pageContext.request.contextPath}/img/tooltip.gif" />
    <textarea name="job_script" id="job_script" rows="10" cols="50"><c:out value="${script}"/>
</textarea>  
	</td>
    </tr>       
    </table>
      
    <input type="button" class="portlet-form-button" onclick="javascript:setRemoteParamDOMValue('job_project,job_subsystem,job_component,job_configuration,job_node,job_script');closediv('cdata');" value="Save">

<%--
    </c:when>
    <c:otherwise>
    <tr>
        <td width="30%">Error: </td>
        <td>           
            <p style="color:#ff0000"><b><msg:getText key="${error}"/></b></p>
        </td>
    </tr>     
    
    </c:otherwise>
    </c:choose>
--%>    
    

    
   
    
    
    
<input type="button" class="portlet-form-button" onclick="closediv('cdata');" value="<msg:getText key="button.quit" />">
    
    
</div>

</div>

<div id="div_wait" class="shape" style="display:none;position:fixed;left:30%;top:40%" >
    <div class="hdn_txt" id="div_wait_txt">
	 <br><br><br><msg:getText key="please.wait" /><br><br><br>
    </div>
</div>
   