<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<div id="div_wait" class="shape" style="display:none;position:fixed;left:30%;top:40%" >
    <div class="hdn_txt" id="div_wait_txt">
	 <br><br><br><msg:getText key="please.wait" /><br><br><br>
    </div>
</div>


<tablewidth="100%" class="window-main">
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.global.pluginname" />: </div></td>
	<td width="80%" class="kback">${jobname}</td>
    </tr>
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.global.pluginnote" />: </div></td>
	<td width="80%" class="kback">${jobtxt}</td>
    </tr>
</table>







<div id="plugin" style="display:block">

    <table width="100%" "><caption class="khead">Plugin</caption>
    <tr><td><msg:help id="helptext" tkey="help.plugin" img="${pageContext.request.contextPath}/img/help.gif" /></td></tr>
    <tr>
	<td width="30%"><msg:getText key="text.plugin.pluginlist" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.plugin.pluginlist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <select name="job_plugin" id="job_plugin" onChange="javascript:setRemoteParamDOMValue('job_plugin');getConfForm('m=GetJobView&job=${jobID}');">
            <option></option>    
	    <c:forEach var="tmp0" items="${plugins}">
		<c:choose>
		    <c:when test="${tmp0==splugin}"><option selected="true">${tmp0}</option></c:when>
		    <c:otherwise><option>${tmp0}</option></c:otherwise>
		</c:choose>    
	    </c:forEach>
	    </select>    
	</td>
    </tr>    

    <tr>
	<td width="30%"><msg:getText key="text.plugin.versionlist" />: </td>
	<td>
            <msg:toolTip id="cicc" tkey="help.plugin.versionlist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <select name="job_version" id="job_version" onChange="javascript:setRemoteParamDOMValue('job_plugin,job_version');getConfForm('m=GetJobView&job=${jobID}');">
            <option></option>    
	    <c:forEach var="tmp0" items="${versions}">
		<c:choose>
		    <c:when test="${tmp0==sversion}"><option selected="true">${tmp0}</option></c:when>
		    <c:otherwise><option>${tmp0}</option></c:otherwise>
		</c:choose>    
	    </c:forEach>
	    </select>    
	</td>
    </tr>   

    <tr>
        <td width="30%"><msg:getText key="text.plugin.multipl" />: </td>
        <td>           
            <msg:toolTip id="cicc" tkey="help.plugin.multipl" img="${pageContext.request.contextPath}/img/tooltip.gif" />        
            <input type="text" id="job_multipl" name="job_multipl" maxlength="3" size="3" onkeyup="this.value = this.value.replace(/[^0-9]/g, '')" value="<c:out value="${multipl}"  /> ">
        </td>
    </tr>

    <tr>
        <td width="30%"><msg:getText key="text.plugin.condition" />: </td>
        <td>
            <msg:toolTip id="cicc" tkey="help.plugin.condition" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            !<input type="checkbox" name="job_ncondition" id="job_ncondition" ${sncondition} >
            <input type="text" id="job_condition" name="job_condition" size="100" value="<c:out value="${scondition}" />">
            <input type="submit" value="clear" onclick="javascript:document.getElementById('job_condition').value='';"/>
        </td>
    </tr>
    
    <c:if test="${warning!=''}">
    <tr>
        <td width="30%"><msg:getText key="text.plugin.conditionkeyswarning" />: </td>
        <td>
            ${warning}
        </td>
    </tr>    
    </c:if>    
    
    <tr>
        <td width="30%"><msg:getText key="text.plugin.conditionkeys" />: </td>
        <td>
            <msg:toolTip id="cicc" tkey="help.plugin.keylist" img="${pageContext.request.contextPath}/img/tooltip.gif" /> 
            <select name="keys" id="keys" size="5"  ondblclick="javascript:addValue('keys','job_condition');">
            <c:forEach var="keys" items="${keylists}">
                    <c:forEach var="inkey" items="${keys.keys}">
                            <option value="${keys.plugin}.${inkey.name}">${keys.plugin}.${inkey.name} (${inkey.description})</option>
                    </c:forEach>                                               
            </c:forEach>
            </select>
            <input type="submit" value="add" onclick="javascript:addValue('keys','job_condition');"/>
        </td>        
    </tr>  
    
    <tr>
        <td width="30%"><msg:getText key="text.plugin.conditionkeys" />: </td>
        <td>
            <select name="felt" id="felt" size="8" ondblclick="javascript:addValue('felt','job_condition');">
                <option value="==" selected>==</option>
                <option value="<="><=</option>
                <option value=">=">>=</option>
                <option value="<"><</option>
                <option value=">">></option>
                <option value="!=">!=</option>
                <option value=" && ">&&</option>
                <option value=" || ">||</option>
            </select>
            <input type="submit" value="add" onclick="javascript:addValue('felt','job_condition');"/>
        </td>        
    </tr>     
    <!--
    <tr>
	<td width="30%"><msg:getText key="text.plugin.n" />:</td>
	<td>
	    
	</td>    
    </tr>
    
    <tr>
	<td width="30%"><msg:getText key="text.job.180" />:</td>
	<td><select ${eserviceurl} name="job_serviceurl" id="job_serviceurl" onChange="getRemoteSelectOptions('p='+document.getElementById('job_serviceurl').value+'&j='+document.getElementById('job_servicetype').value+'&m=GetWebServiceMethod')">	    
	<c:forEach var="sty" items="${serviceurl}">
		<c:choose>
		    <c:when test="${sty.key==iserviceurl}">
    			<option selected="true" value="${sty.key}">${sty.key}</option>	
		    </c:when>
		    <c:otherwise>	
			<option value="${sty.key}">${sty.key}</option>	
		    </c:otherwise>	
		</c:choose>
	    </c:forEach>
	    </select>    
	<msg:toolTip id="cicc" tkey="config.job.serviceurl" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	<c:if test="${job.labelServiceUrl!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelServiceUrl}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	<c:if test="${job.descServiceUrl!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descServiceUrl}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</td>
    </tr>    
    <tr>
	<td width="30%"><msg:getText key="text.job.190" />: </td>
	<td><select ${eservicemethod} name="job_servicemethod" id="job_servicemethod">
	    <c:forEach var="sty" items="${servicemethod}">
		<c:choose>
		    <c:when test="${sty==iservicemethod}">
    			<option value="${sty}" selected="true">${sty}</option>	
		    </c:when>
		    <c:otherwise>	
			<option value="${sty}">${sty}</option>	
		    </c:otherwise>	
		</c:choose>
	    </c:forEach>
	    </select>    
	<msg:toolTip id="cicc" tkey="config.job.servicemethod" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	<c:if test="${job.labelServiceMethod!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelServiceMethod}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	<c:if test="${job.descServiceMethod!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descServiceMethod}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</td>
    </tr>    
    <tr>
	<td width="30%"><msg:getText key="text.job.200" />:</td>
	<td><input type="checkbox" class="portlet-form-button" name="job_useallthiss" id="job_useallthiss" value="${jobname}">
	    <msg:toolTip id="cicc" tkey="config.job.usealljob" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</td>    
    </tr>
-->
</table>
<c:choose>
<c:when test="${error==null}">
    <input type="button" class="portlet-form-button" onclick="javascript:setRemoteParamDOMValue('job_plugin,job_version,job_multipl,job_condition,job_ncondition');closediv('cdata');" value="Save">
</c:when>
<c:otherwise>
    <H2><msg:getText key="${error}" /></H2><br>
</c:otherwise>
</c:choose>
    <input type="button" class="portlet-form-button" onclick="javascript:closediv('cdata');" value="<msg:getText key="button.quit" />">
</div>


 

  
   