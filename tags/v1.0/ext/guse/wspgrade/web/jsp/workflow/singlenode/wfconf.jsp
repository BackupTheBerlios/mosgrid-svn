<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- WF tulajdonsag szerkesztes UI -->

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

    <table width="100%" "><caption class="khead">WF properies</caption>
    <tr><td><msg:help id="helptext" tkey="help.plugin.WFProp" img="${pageContext.request.contextPath}/img/help.gif" /></td></tr>
    
    <tr>
        <td width="30%"><msg:getText key="text.plugin.multipl" />: </td>
        <td>           
            <input type="text" id="wfp_wfmultipl" name="wfp_wfmultipl" maxlength="3" size="3" onkeyup="this.value = this.value.replace(/[^0-9]/g, '')" value="<c:out value="${wfmultipl}"  /> ">
            <msg:toolTip id="cicc" tkey="help.plugin.wfmultipl" img="${pageContext.request.contextPath}/img/tooltip.gif" />
        </td>
    </tr>    

    <tr>
        <td width="30%"><msg:getText key="text.plugin.startDate" />: </td>
        <td>           
            <input name="wfp_startDate" id="wfp_startDate" value="${startDate}" maxlength="10" size="10" onkeyup="this.value = this.value.replace(/[^0-9/]/g, '')"> <input type=button value="select" onclick="javascript:displayDatePicker('wfp_startDate');">
            <msg:toolTip id="cicc" tkey="help.plugin.startDate" img="${pageContext.request.contextPath}/img/tooltip.gif" />
        </td>
    </tr>
    <tr>
        <td width="30%"><msg:getText key="text.plugin.endDate" />: </td>
        <td>           
            <input name="wfp_endDate" id="wfp_endDate" value="${endDate}" maxlength="10" size="10" onkeyup="this.value = this.value.replace(/[^0-9/]/g, '')" > <input type=button value="select" onclick="javascript:displayDatePicker('wfp_endDate');">
            <msg:toolTip id="cicc" tkey="help.plugin.endDate" img="${pageContext.request.contextPath}/img/tooltip.gif" />
        </td>
    </tr>  

    <tr>
        <td width="30%"><msg:getText key="text.plugin.defaultHM" />: </td>
        <td>           
            <select name="wfp_defstarth" id="wfp_defstarth" onchange="settime('h');">
            <c:forEach var="hour" items="${hours}">
                <c:choose>
                <c:when test="${hour==defstarth}">
                    <option selected>${hour}</option>
                </c:when>
                <c:otherwise>
                    <option>${hour}</option>
                </c:otherwise>
                </c:choose>                
            </c:forEach>           
            </select> 
            <select name="wfp_defstartm" id="wfp_defstartm" onchange="settime('m');">
            <c:forEach var="min" items="${mins}">
                <c:choose>
                <c:when test="${min==defstartm}">
                    <option selected>${min}</option>
                </c:when>
                <c:otherwise>
                    <option>${min}</option>
                </c:otherwise>
                </c:choose>                             
            </c:forEach>           
            </select>             
            <msg:toolTip id="cicc" tkey="help.plugin.def" img="${pageContext.request.contextPath}/img/tooltip.gif" />
        </td>
    </tr>  

<c:forEach var="day" items="${days}">
    <tr>
        <td width="30%"><msg:getText key="text.day.${day['i']}" />: </td>
        <td>           
            enabled:<input type="checkbox" name="wfp_e${day.i}" id="wfp_e${day.i}" ${day['e']}> default:<input type="checkbox" name="wfp_d${day.i}" id="wfp_d${day.i}" onchange="setenable(${day.i})" ${day['d']}>
            <select name="wfp_h${day.h}" id="wfp_h${day.i}" ${day['di']}>
            <c:forEach var="hour" items="${hours}">
                <c:choose>
                <c:when test="${hour==day.h}">
                    <option selected>${hour}</option>
                </c:when>
                <c:otherwise>
                    <option>${hour}</option>
                </c:otherwise>
                </c:choose>                              
            </c:forEach>           
            </select> 
            <select name="wfp_m${day.i}" id="wfp_m${day.i}" ${day['di']}>
            <c:forEach var="min" items="${mins}">
                <c:choose>
                <c:when test="${min==day.m}">
                    <option selected>${min}</option>
                </c:when>
                <c:otherwise>
                    <option>${min}</option>
                </c:otherwise>
                </c:choose>                            
            </c:forEach>           
            </select>             
            <msg:toolTip id="cicc" tkey="help.plugin.endDate" img="${pageContext.request.contextPath}/img/tooltip.gif" />
        </td>
    </tr>  
</c:forEach>     
 
</table>
    <input type="button" class="portlet-form-button" onclick="javascript:setRemoteParamDOMValue('wfp_wfmultipl,wfp_startDate,wfp_endDate,wfp_defstarth,wfp_defstartm${save}');closediv('cdata');" value="Save">    
    <input type="button" class="portlet-form-button" onclick="javascript:closediv('cdata');" value="<msg:getText key="button.quit" />">
</div>


 

  
  