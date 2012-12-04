<%-- 
    Document   : midleware_gemlca
    Created on : 2010.05.20., 0:02:42
    Author     : krisztian
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>


<table width="100%"  style="text-align:left;">
    <tr>
    	<th class="prop-text" width="30%"><msg:getText key="text.job.Gemlcarepo" />:</th>
        <td>
            <select ${egrid} class="portlet-form-button" name="job_grid" id="job_grid" onChange="javascript:gemlcaUI(true,'${jobID}');"><%--gemlcaUI(true,'${jobID}'); --%>
                <option value="-">-</option>
            <c:forEach var="tmp0" items="${grids}">
            <c:choose>
                <c:when test="${tmp0==sgrid}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
                <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
            </c:choose>
            </c:forEach>
            </select>
		    <msg:toolTip id="cicc" tkey="config.job.gemlcarepo" img="${pageContext.request.contextPath}/img/tooltip.gif" />
        	<c:if test="${job.labelGrid!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelGrid}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descGrid!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descGrid}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
        </td>
    </tr>
    <tr><td colspan="2"><div class="iodline" /></td></tr>

<c:choose>
<c:when test="${gmsg==null}">

    <tr>
	<th class="prop-text" width="30%"><msg:getText key="text.job.GemlcaLegacyCode" />:</th>
	<td>
        <div id="iconmenu">
        <select ${eresource} class="portlet-form-button" name="job_resource" id="job_resource" onChange="javascript:document.getElementById('job_params').value='';gemlcaUI(true,'${jobID}');">
	    <c:forEach var="legacyCode" items="${resources}"  >
         
		<c:choose>
		    <c:when test="${legacyCode==sresource}">
                <c:choose>
                    <c:when test="${fn:startsWith(legacyCode,'gusedelimit---')==true}">
                        <c:set var="delimiter" value="${fn:substring(legacyCode,11,-1)}" scope="page" />
                        <option disabled="true" style="color:red" value="">${delimiter}</option>
                    </c:when>
                    <c:otherwise>
                            <option selected="true" value="${legacyCode}">${legacyCode}</option>
                    </c:otherwise>
                </c:choose>
            </c:when>
		    <c:otherwise>
                <c:choose>
                    <c:when test="${fn:startsWith(legacyCode,'gusedelimit---')==true}">
                        <c:set var="delimiter" value="${fn:substring(legacyCode,11,-1)}" scope="page" />
                        <option disabled="true" style="color:red" value="">${delimiter}</option>
                    </c:when>
                    <c:otherwise>
                            <option value="${legacyCode}">${legacyCode}</option>
                    </c:otherwise>
                </c:choose>
             </c:otherwise>
		</c:choose>
	    </c:forEach>
	    </select>
        </div>
            <msg:toolTip id="cicc" tkey="config.job.gemlcalc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    <c:if test="${job.labelResource!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelResource}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	    <c:if test="${job.descResource!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descResource}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</td>
    </tr>
    <tr><td colspan="2"><div class="iodline" /></td></tr>
    <tr>
	<th class="prop-text" width="30%"><msg:getText key="text.job.6" />:</th>
	<td><select ${ejobmanager} class="portlet-form-button" name="job_jobmanager" id="job_jobmanager" >
	    <c:forEach var="tmp0" items="${rdata}">
		<c:choose>
		    <c:when test="${tmp0==sdata}"><option selected="true">${tmp0}</option></c:when>
		    <c:otherwise><option>${tmp0}</option></c:otherwise>
		</c:choose>
	    </c:forEach>
	    </select>
		    <msg:toolTip id="cicc" tkey="config.job.gemlcaresource" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                    <c:if test="${job.labelJobManager!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelJobManager}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	    <c:if test="${job.descJobManager!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descJobManager}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</td>
    </tr>
    <tr><td colspan="2"><div class="iodline" /></td></tr>

<%-- GEMLCA parameters  --%>

    <tr>
        <th class="prop-text" width="30%" >GEMLCA file parameters:</th>
        <td>${inpnmb} input ${oupnmb} output port(s)</td>
    </tr>
    <tr><td colspan="2"><div class="iodline" /></td></tr>
    <tr>
        <td colspan="0"><b><msg:getText key="text.job.gemlcaparameters" />:</b> </td>
    </tr>
    <tr><td colspan="2"><div class="iodline" /></td></tr>
    <c:forEach var="tmp0" items="${gparams}">
          <%--  <c:choose>
                <c:when test="${tmp0['input']=='true'}">
                    Input
                </c:when>
                <c:otherwise>
                    Output
                </c:otherwise>
            </c:choose>	--%>
        <c:choose>
            <c:when test="${tmp0['file']=='true'}">
                <input type="hidden" name="job_gparam${tmp0['nbr']}" id="job_gparam${tmp0['nbr']}" value="<c:out value="${tmp0['svalue']}" />"  ><!--File-->
            </c:when>
            <c:otherwise>
            <tr>
                <td width="30%">${tmp0['friendlyName']} (${tmp0['value']}):</td>
                <td><input type="text" ${eparams} name="job_gparam${tmp0['nbr']}" id="job_gparam${tmp0['nbr']}" value="<c:out value="${tmp0['svalue']}" />" onkeyup="this.value = this.value.replace(' ', '')" onblur="javascript:gpcheck('${tmp0['nbr']}','${tmp0['value']}');" ></td>
            </tr>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    <tr>
        <td colspan="0"><br><H2><msg:getText key="please.configure.jobio" /></H2><br></td>
    </tr>
</c:when>
<c:otherwise>
    <tr>
        <td colspan="0">
            <br><msg:getText key="${gmsg}" /><br>
            <input type="hidden" name="job_resource" id="job_resource" value="">
            <input type="hidden" name="job_jobmanager" id="job_jobmanager" value="">
        </td>
    </tr>
</c:otherwise>
</c:choose>
</table>
<input type="hidden" name="job_params" id="job_params" value="<c:out value="${params}" escapeXml="true" />">
<div style="display:none;"><input type="file" name="job_${jobID}_binary" id="job_${jobID}_binary" /></div>



