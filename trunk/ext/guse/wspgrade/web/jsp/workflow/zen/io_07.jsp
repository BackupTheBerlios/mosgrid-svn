<%-- remote output --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<div class="ioconfigtable">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.10" />:</div>
	<div class="ioconfig-data">
		<input title='<msg:getText key="config.input.remotelogical" />' type="text" name="output_${tmp.id}_remote" id="output_${tmp.id}_remote" value="<c:out value="${tmp.remoteName}" escapeXml="true"  />" />
		<c:if test="${tmp.labelRemoteName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelRemoteName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descRemoteName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descRemoteName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</div>    	
    </div>
    <div style="display:table-row;margin-top:1px;float:left;">
        <c:choose>
            <c:when test="${jobgridtype=='glite'}">
                <div class="jobconfig-header"><msg:getText key="text.io.11" />:</div>
                <div class="ioconfig-data">
                    <input title='<msg:getText key="config.input.remotehost" />' type="text"  name="output_${tmp.id}_remotehost" id="output_${tmp.id}_remotehost" value="<c:out value="${tmp.remoteHost}" escapeXml="true"  />" />
                    <c:if test="${tmp.labelRemoteHost!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelRemoteHost}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
                    <c:if test="${tmp.descRemoteHost!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descRemoteHost}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
                </div>
            </c:when>
        </c:choose>
    </div>
</div>
