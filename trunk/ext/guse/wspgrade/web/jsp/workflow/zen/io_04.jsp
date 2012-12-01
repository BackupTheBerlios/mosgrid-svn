<%-- nem csatorna input --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<div class="ioconfigtable">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.240" />:</div>
	<div class="ioconfig-data">

    <table style="text-align:left;">
		<div class="page">
		    <div class='menuInput'  >
			<c:choose>
			    <c:when test="${tmp.externalName!='' && tmp.externalName!='N/A'}">
				<c:set var="imgurl" value="local.png.selected.png" />
				<c:set var="display_local" value="block" />
			    </c:when>
			    <c:otherwise>
				<c:set var="imgurl" value="local.png" />
				<c:set var="display_local" value="none" />
			    </c:otherwise>
			</c:choose>
	    		<a id="ita0_${tmp.id}" href="javascript:inputtypepanelhide('${tmp.id}','upload');"><img id="it0_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/${imgurl}' title='<msg:getText key="text.io.25" />' /></a>
		    
		    <c:set var="display_remote" value="none" />
		    <c:set var="display_value" value="none" />
		    <c:set var="display_sql" value="none" />

		    <c:if test="${iworkflow=='null'}">
                <c:choose>
                    <c:when test="${jobgridtype=='gemlca'}">
                        <c:choose>
                            <c:when test="${tmp.remoteName!='' && tmp.remoteName!='N/A'}">
                                <c:set var="imgurl" value="gemlca-default.png.selected.png" />
                                <c:set var="display_remote" value="block" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="imgurl" value="gemlca-default.png" />
                            </c:otherwise>
                        </c:choose>
                        <a id="ita1_${tmp.id}" href="javascript:inputtypepanelhide('${tmp.id}','remote');" onclick="inputEnabled('${tmp.id}','1');"><img id="it1_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/${imgurl}' title='Default' /></a>
                    </c:when>
                    <c:when test="${jobgridtype=='boinc' || jobgridtype=='gae' || jobgridtype=='local' || jobgridtype=='arc' || jobgridtype=='lsf' || jobgridtype=='pbs' || jobgridtype=='edgi'}"></c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${tmp.remoteName!='' && tmp.remoteName!='N/A'}">
                                <c:set var="imgurl" value="remote.png.selected.png" />
                                <c:set var="display_remote" value="block" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="imgurl" value="remote.png" />
                            </c:otherwise>
                        </c:choose>
                        <a id="ita1_${tmp.id}" href="javascript:inputtypepanelhide('${tmp.id}','remote');"><img id="it1_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/${imgurl}' title='<msg:getText key="text.io.26" />' /></a>
                    </c:otherwise>
                </c:choose>

                <c:choose>
        		    <c:when test="${tmp.value!='' && tmp.value!='N/A'}">
    				<c:set var="display_value" value="block" />
    				<c:set var="imgurl" value="constans.png.selected.png" />
        		    </c:when>
            	    <c:otherwise>
                	<c:set var="imgurl" value="constans.png" />
    			    </c:otherwise>
        		</c:choose>
            	<a id="ita2_${tmp.id}" href="javascript:inputtypepanelhide('${tmp.id}','constans');"><img id="it2_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/${imgurl}' title='<msg:getText key="text.io.5" />' /></a>

    			<c:choose>
        		    <c:when test="${tmp.sqlurl!='' && tmp.sqlurl!='N/A'}">
                    	<c:set var="display_sql" value="block" />
                        <c:set var="imgurl" value="sql.png.selected.png" />
    			    </c:when>
        		    <c:otherwise>
                    	<c:set var="imgurl" value="sql.png" />
                    </c:otherwise>
    			</c:choose>
        		<a id="ita3_${tmp.id}" href="javascript:inputtypepanelhide('${tmp.id}','sql');"><img id="it3_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/${imgurl}' title='<msg:getText key="text.io.28" />'/></a>
            </c:if>
        </div><%-- class="menuInput"--%>
        </div><%-- class="page"--%>
    </table>
        <div id="jobinputtype_${tmp.id}_upload" style="display:${display_local}">
        <center>
    		<msg:getText key="text.io.21" />:
        	<c:out value="${tmp.externalName}" escapeXml="true"  />
		
    		<c:if test="${tmp.labelExternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelExternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
        	<c:if test="${tmp.descExternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descExternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
            <br />
            <msg:getText key="text.io.24" />
    		<div id="upload_input${tmp.id}" style="float:left;display:block;">
        	    <input title='<msg:getText key="config.input.file" />' type="file" ${tmp.enabledInputType} name="input_${tmp.id}_file" id="input_${tmp.id}_file" onchange="inputEnabled('${tmp.id}','0')" />
            </div>
    		<c:if test="${tmp.labelExternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelExternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
        	<c:if test="${tmp.descExternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descExternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
        </center>
        </div>

<div id="jobinputtype_${tmp.id}_remote" style="display:${display_remote}">
<center>
    <c:choose>
        <c:when test="${jobgridtype=='gemlca'}">
            <msg:getText key="config.input.gemlcadefault" />
            <input title='<msg:getText key="config.input.remote" />' type="hidden" ${tmp.enabledInputType} name="input_${tmp.id}_remote" id="input_${tmp.id}_remote" value="<c:out value="${tmp.remoteName}" escapeXml="true"  />" onchange="inputEnabled('${tmp.id}','1')"  />
        </c:when>
        <c:when test="${jobgridtype=='boinc' || jobgridtype=='gae' || jobgridtype=='local' || jobgridtype=='arc' || jobgridtype=='lsf' || jobgridtype=='pbs'}"></c:when>
        <c:otherwise>
            <input size="50" title='<msg:getText key="config.input.remote" />' type="text" ${tmp.enabledInputType} name="input_${tmp.id}_remote" id="input_${tmp.id}_remote" value="<c:out value="${tmp.remoteName}" escapeXml="true" />" onchange="inputEnabled('${tmp.id}','1')"  /> <br />
            <input title='<msg:getText key="config.input.remotecopy" />' type="checkbox" ${tmp.enabledInputType} name="input_${tmp.id}_remotecopy" id="input_${tmp.id}_remotecopy" checked="true" disabled="true" value=""> <msg:getText key="text.io.100" />:
            <c:if test="${tmp.labelRemoteName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelRemoteName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${tmp.descRemoteName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descRemoteName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
        </c:otherwise>
    </c:choose>
</center>
</div>

<div id="jobinputtype_${tmp.id}_constans" style="display:${display_value}">
<center>
    <input title='<msg:getText key="config.input.value" />' type="text" ${tmp.enabledInputType}  name="input_${tmp.id}_value" id="input_${tmp.id}_value" value="<c:out value="${tmp.value}" escapeXml="true" />" onchange="inputEnabled('${tmp.id}','2')">
    <c:if test="${tmp.labelValue!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelValue}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /> </c:if>
    <c:if test="${tmp.descValue!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descValue}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /> </c:if>
</center>
</div>


<table width="100%"align="right" id="jobinputtype_${tmp.id}_sql" style="float:right;display:${display_sql}">
					<tr>
					    <td>
						<msg:getText key="text.io.29" />:
						<input type="text" ${tmp.enabledInputType} name="input_${tmp.id}_sqlurl" id="input_${tmp.id}_sqlurl" size="45" value="<c:out value="${tmp.sqlurl}" escapeXml="true"  />" onchange="inputEnabled('${tmp.id}','3')" /> 
					    </td>
					    <td>
						<c:if test="${tmp.labelSqlurl!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelSqlurl}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
						<c:if test="${tmp.descSqlurl!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descSqlurl}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
					    </td>
					</tr>
					<tr>
					    <td>
						<msg:getText key="text.io.30" />
						<input type="text" ${tmp.enabledInputType} name="input_${tmp.id}_sqluser" id="input_${tmp.id}_sqluser" size="20" value="<c:out value="${tmp.sqluser}" escapeXml="true"  />" onchange="inputEnabled('${tmp.id}','3')"/>
					    </td>
					    <td>
						<c:if test="${tmp.labelSqluser!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelSqluser}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
						<c:if test="${tmp.descSqluser!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descSqluser}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
					    </td>
					</tr>    
					<tr>
					    <td>
						<msg:getText key="text.io.31" />
						<input type="password" ${tmp.enabledInputType} name="input_${tmp.id}_sqlpass" id="input_${tmp.id}_sqlpass" size="20" value="<c:out value="${tmp.sqlpass}" escapeXml="true"  />" onchange="inputEnabled('${tmp.id}','3')"/>
					    </td>
					    <td>
						<c:if test="${tmp.labelSqlpass!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelSqlpass}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
						<c:if test="${tmp.descSqlpass!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descSqlpass}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
					    </td>
					</tr>        
					<tr>
					    <td>
						<msg:getText key="text.io.32" />
						SELECT <input type="text" ${tmp.enabledInputType} name="input_${tmp.id}_sqlselect" id="input_${tmp.id}_sqlselect"  size="45" value="<c:out value="${tmp.sqlselect}" escapeXml="true"  />" onchange="inputEnabled('${tmp.id}','3')"/>
					    </td>
					    <td>
						<c:if test="${tmp.labelSqlselect!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelSqlselect}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
						<c:if test="${tmp.descSqlselect!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descSqlselect}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
					    </td>
					</tr>
</table>
				    


</div>    	
    </div>


</div>


