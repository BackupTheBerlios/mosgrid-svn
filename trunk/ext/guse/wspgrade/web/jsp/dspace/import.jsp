<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>


<portlet:actionURL var = "pURLdown">
    <portlet:param value="doRepositoryDownload" name="guse"/>
</portlet:actionURL>

<script type="text/javascript">
    function wfimp_jenabled(checkboxid, textid) {
    if(document.getElementById(checkboxid).checked) {
    document.getElementById(textid).value = "";
    document.getElementById(textid).disabled = false;
    } else {
    document.getElementById(textid).value = "";
    document.getElementById(textid).disabled = true;
    }
    }
</script>

<form method="post" action="${pURLdown}" >
        <table border="1" width="410px">
        <tr>
            <td class="portlet-section-body">

            <tr>
                <td class="portlet-section-body" width="200px">
                    <b>DSpace Handle :</b>
                </td>
                <td class="portlet-section-body">
                    <input type="text" name="dspaceHandle"  value="" />
                </td>
                <td class="portlet-section-body">
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="dspace.handle.help" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>

            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfimport.8" /> :
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_grafName" type="text" name="wfimp_newGrafName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_grafCheckBox" type="checkbox" value="OFF" onclick="wfimp_jenabled('wfimp_grafCheckBox', 'wfimp_grafName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfimport.graf" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>
            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfimport.9" /> :
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_abstName" type="text" name="wfimp_newAbstName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_abstCheckBox" type="checkbox" value="OFF" onclick="wfimp_jenabled('wfimp_abstCheckBox', 'wfimp_abstName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfimport.abst" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>
            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfimport.10" /> :
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_realName" type="text" name="wfimp_newRealName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_realCheckBox" type="checkbox" value="OFF" onclick="wfimp_jenabled('wfimp_realCheckBox', 'wfimp_realName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfimport.real" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>
        </table>
        <br/>
        <input type="submit" value="Import from DSpace" />
</form>
<br/>
<b><i>Message:</i></b> <font color="red"> ${msg}</font>
<br/><br/>
<iframe width="100%" height="800" frameborder="0" src="${dspaceurl}/handle/${dspacecoll}"></iframe>