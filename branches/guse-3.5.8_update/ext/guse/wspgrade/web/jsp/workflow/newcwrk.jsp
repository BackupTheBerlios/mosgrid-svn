<!-- 
Uj konkret workflow letrehozasa 
-->

<jsp:include page="/jsp/core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>
<portlet:actionURL var="pURL">
    <portlet:param name="guse" value="doNew" />
</portlet:actionURL>
<portlet:renderURL var="r2URL"/>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<jsp:useBean id="dt" class="java.util.Date" />

<table class="portlet-pane" cellspacing="1" cellpadding="1" border="0" width="100%" >
<tr>
<td style="width:100%">

    <table width="100%">
	<tr> 
	    <td class="portlet-section-body" colspan="3">
		<form method="post" action="${r2URL}"> 
		    <input type="submit" class="portlet-form-button" value="<msg:getText key="button.refresh" />" />
		</form>	     
	    </td>
	</tr>
    <form method="post" action="${pURL}"> 
	<tr> 
	    <td class="portlet-section-body" rowspan="3"><msg:getText key="text.newcwrk.createfrom" /> </td>
	    <td class="portlet-section-body"><input type="radio" name="ptyp" value="graf" checked="true" onClick="document.getElementById('pawkf').disabled=true;document.getElementById('pcwkf').disabled=true;document.getElementById('pgraf').disabled=false;"><msg:getText key="text.newcwrk.graph" /></td>
	    <td class="portlet-section-body">
		<select name="pgraf" id="pgraf">
		    <c:forEach var="tmp" items="${grafs}">
                <option id="<c:out value="${tmp.workflowID}" escapeXml="true" />"><c:out value="${tmp.workflowID}" escapeXml="true" /></option>
		    </c:forEach>
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.createwf.wflist1" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	</tr>
	<tr> 
	    <td class="portlet-section-body"><input type="radio" name="ptyp" value="abst" onClick="document.getElementById('pawkf').disabled=false;document.getElementById('pcwkf').disabled=true;document.getElementById('pgraf').disabled=true;"><msg:getText key="text.newcwrk.template" /></td>
	    <td class="portlet-section-body">
		<select name="pawkf" id="pawkf" disabled="true">
		    <c:forEach var="tmp" items="${awkfs}">
		      <option id="<c:out value="${tmp.workflowID}" escapeXml="true" />"><c:out value="${tmp.workflowID}" escapeXml="true" /></option>
		    </c:forEach>
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.createwf.wflist2" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	</tr>
	<tr> 
	    <td class="portlet-section-body"><input type="radio" name="ptyp" value="conct" onClick="document.getElementById('pawkf').disabled=true;document.getElementById('pgraf').disabled=true;document.getElementById('pcwkf').disabled=false;"><msg:getText key="text.newcwrk.concrate" /></td>
	    <td class="portlet-section-body">
		<select name="pcwkf" id="pcwkf" disabled="true">
		    <c:forEach var="tmp" items="${wkfs}">
		      <option id="<c:out value="${tmp.workflowID}" escapeXml="true" />"><c:out value="${tmp.workflowID}" escapeXml="true" /></option>
		    </c:forEach>
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.createwf.wflist3" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	</tr>
	<tr><td class="portlet-section-body" colspan="3" style="border-top:2px solid;"></td></tr>
	<tr> 
	    <td class="portlet-section-body"><msg:getText key="text.newcwrk.newname" />: </td>
	    <td class="portlet-section-body" colspan="2"><input type="text" name="pNewWkfName">
		<msg:toolTip id="ctmpdiv" tkey="portal.createwf.wfname" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	</tr>
	<tr> 
	    <td class="portlet-section-body"><msg:getText key="text.newcwrk.newdescription" />: </td>
	    <td class="portlet-section-body" colspan="2"><textarea name="pNewWkfDesc">${dt.year+1900}-${dt.month+1}-${dt.date}</textarea>
		<msg:toolTip id="ctmpdiv" tkey="portal.createwf.wfdesc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	</tr>
	<tr> 
	    <td class="portlet-section-body"><msg:getText key="text.newcwrk.type" />: </td>
	    <td class="portlet-section-body" colspan="2">
		<select name="pNewType">
		    <c:forEach var="wt" items="${wftypes}">
                <option><c:out value="${wt}" escapeXml="true" /></option>
		    </c:forEach>
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.createwf.wftype" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	</tr>
	<tr> 
	    <td class="portlet-section-body" colspan="3"><input type="submit" class="portlet-form-button" value="<msg:getText key="button.ok" />"></td>
	</tr>
    </table>
</td></tr>

</form>    

<tr>
<td style="width:100%">
    <table>
        <tr>
            <td class="portlet-section-body"><div class="bold"><msg:getText key="text.global.0" />: </div></td>
            <td class="portlet-section-body">${nw} <msg:getText key="${msg}" /> </td>
        </tr>
    </table>

</td></tr>
</table>

