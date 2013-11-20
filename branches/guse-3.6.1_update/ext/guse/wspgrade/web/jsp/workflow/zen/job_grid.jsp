<%--
@call job.jsp
    grides job
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="p" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>




<script>

$(document).ready(function() {
    var options = {
        target:        '#popup1',   // target element(s) to be updated with server response
        beforeSubmit:  showRequest,  // pre-submit callback
        success:       showResponse  // post-submit callback
    };

    // bind form using 'ajaxForm'
    $('#job_gridform').ajaxForm(options);
});

// pre-submit callback
function showRequest(formData, jqForm, options) {return true;}

// post-submit callback
function showResponse(responseText, statusText, xhr, $form)  {viewToolTipBT("#popup1");}

</script>


<form method="post" action="${sessionScope.ajaxSessionURL}" onsubmit="saveGridJobData(this,'${jobID}');return false;" id="job_gridform">
    <input type="hidden" name="smsg" />

<%-- midleware lista  --%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.4" />:</div>
    <div class="jobconfig-data">
                    <c:set var="gt" value="0" scope="page"/>
                    <select title='<msg:getText key="config.job.gridtype" />' ${egridtype} name="job_gridtype" id="job_gridtype" onclick="showMidleware();">
                    <c:forEach var="tmp" items="${gridtypes}" varStatus="ls">
                        <c:choose>
                            <c:when test="${sgridtype==tmp.key}">
                            <option selected="true" value="<c:out value="${tmp.key}" escapeXml="true" />" ><c:out value="${tmp.key}" escapeXml="true" /></option>
                        </c:when>
                        <c:otherwise>
                            <option value="<c:out value="${tmp.key}" escapeXml="true" />" ><c:out value="${tmp.key}" escapeXml="true" /></option>
                        </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <%--
                    <c:set var="broker" value="metabroker" />
            	    <c:choose>
                            <c:when test="${sgridtype==broker}">
                            <option selected="true" value="${broker}" >${broker}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${broker}" >${broker}</option>
                        </c:otherwise>
                        </c:choose>
                    --%>
                    </select>
                    <c:if test="${job.labelGridType!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelGridType}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
                    <c:if test="${job.descGridType!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descGridType}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>

<%-- middleware specifikus konfiguracios adatok--%>
<div id="middleware-panel" >
    <jsp:include page="${jobjsp}" />
</div>

<%-- mentes gomb--%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div style="display:table-cell;width:900px">
	<center><input type="image" src="${pageContext.request.contextPath}/imgs/accept_64.png" /></center>
    </div>
</div>

</form>

