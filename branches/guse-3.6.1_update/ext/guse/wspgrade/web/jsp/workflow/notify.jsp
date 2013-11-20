<jsp:include page="/jsp/core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<portlet:renderURL var="rURL" />


<form method="post" action="${pURL}"> 
    <input type="hidden" name="workflow" id="workflow">
    <input type="hidden" name="action" id="action">
</form>

<table>
    <tr>
	<td><b><msg:getText key="text.notify.email.settings" />:</b></td>
    </tr>
</table>

<portlet:defineObjects/>
<portlet:actionURL var = "pURL">
    <portlet:param name="guse" value="doSave"/>
</portlet:actionURL>

<form method="post" action="${pURL}">

<table>
    <tr>
	<td width="170"><msg:getText key="text.notify.email.enab" />:</td>
        <td class="portlet-section-body">
	    <!--<input id="email_enab" type="checkbox" name="email_enab" ${value_email_enab} onclick="" />-->
	    <select id="email_enab" name="email_enab" onchange="">
		<option value="0" ${value_email_enab0}><msg:getText key="text.notify.disable" /></option>
		<option value="1" ${value_email_enab1}><msg:getText key="text.notify.enable" /></option>
	    </select>
	    <msg:toolTip id="ctmpdiv" tkey="portal.notify.email.enab" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</td>
    </tr>
    <tr>
	<td><msg:getText key="text.notify.email.addr" />:</td>
        <td class="portlet-section-body">
            <input id="email_addr" type="text" name="email_addr" size="50" value="<c:out value="${value_email_addr}" escapeXml="true" />" />
	    <msg:toolTip id="ctmpdiv" tkey="portal.notify.email.addr" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</td>
    </tr>
    <tr>
	<td><msg:getText key="text.notify.email.subj" />:</td>
        <td class="portlet-section-body">
            <input id="email_subj" type="text" name="email_subj" size="50" value="<c:out value="${value_email_subj}" escapeXml="true" />" />
	    <msg:toolTip id="ctmpdiv" tkey="portal.notify.email.subj" img="${pageContext.request.contextPath}/img/tooltip.gif" />
        </td>
    </tr>
</table>

<br />

<table>
    <tr>
	<td><b><msg:getText key="text.notify.wfchg.settings" />:</b></td>
    </tr>
</table>

<table>
    <tr>
	<td width="170"><msg:getText key="text.notify.wfchg.enab" />:</td>
        <td class="portlet-section-body">
	    <select id="wfchg_enab" name="wfchg_enab" onchange="">
		<option value="0" ${value_wfchg_enab0}><msg:getText key="text.notify.disable" /></option>
		<option value="1" ${value_wfchg_enab1}><msg:getText key="text.notify.enable" /></option>
	    </select>
	    <msg:toolTip id="ctmpdiv" tkey="portal.notify.wfchg.enab" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</td>
    </tr>
    <tr>
	<td width="170" class="portlet-section-body" valign="top"><msg:getText key="text.notify.wfchg.mess" />:</td>
        <td class="portlet-section-body" valign="top">
            <textarea id="wfchg_mess" name="wfchg_mess" cols="50" rows="17" ><c:out value="${value_wfchg_mess}" escapeXml="true" /></textarea>
	</td>
        <td class="portlet-section-body" valign="top">
    	    <msg:toolTip id="ctmpdiv" tkey="portal.notify.wfchg.mess" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    <br />key list:
	    <br />#now#
<%--	    <br />#user# --%>
	    <br />#portal#
	    <br />#workflow#
	    <br />#instance#
	    <br />#oldstatus#
	    <br />#newstatus#
	</td>
    </tr>
</table>

<br />

<table>
    <tr>
	<td><b><msg:getText key="text.notify.quota.settings" />:</b></td>
    </tr>
</table>

<table>
    <tr>
	<td width="170"><msg:getText key="text.notify.quota.enab" />:</td>
        <td class="portlet-section-body">
	    <select id="quota_enab" name="quota_enab" onchange="">
		<option value="0" ${value_quota_enab0}><msg:getText key="text.notify.disable" /></option>
		<option value="1" ${value_quota_enab1}><msg:getText key="text.notify.enable" /></option>
	    </select>
	    <msg:toolTip id="ctmpdiv" tkey="portal.notify.quota.enab" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</td>
    </tr>
    <tr>
	<td class="portlet-section-body" valign="top"><msg:getText key="text.notify.quota.mess" />:</td>
        <td class="portlet-section-body" valign="top">
            <textarea id="quota_mess" name="quota_mess" cols="50" rows="17" ><c:out value="${value_quota_mess}" escapeXml="true" /></textarea>
	</td>
        <td class="portlet-section-body" valign="top">
    	    <msg:toolTip id="ctmpdiv" tkey="portal.notify.quota.mess" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    <br />key list:
	    <br />#now#
<%--	    <br />#user# --%>
	    <br />#portal#
	    <br />#quota#
	    <br />#usedquota#
	    <br />#quotapercentmax#
	    <br />#quotapercent#
	</td>
    </tr>
</table>


<table>
    <tr>
        <td>
	    <input class="portlet-form-button" type="submit" name="gs_action=doSave" value="<msg:getText key="button.save" />" />
        </td>
    </tr>
</table>

</form>

<table class="portlet-pane" cellspacing="1" cellpadding="1" border="0" width="100%" >
<tr><td>
    <table style="padding-top:15px;">
        <tr>
            <td><div class="bold"><msg:getText key="text.global.0" />: </div></td>
            <td><div id="msg"><msg:getText key="${notifymsg}" /></div></td>
        </tr>
    </table>
</td></tr>
</table>
