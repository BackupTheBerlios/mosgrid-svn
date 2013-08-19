<!-- Job le�r� szerkeszt�s UI -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<br /> <br /><br /> <br />
<%-- leiro konfiguracio --%>

<div style="width:400px;display:block;float:left">
<table>
    <tr>
	<th class="jobconfig-header" width="20%"> <msg:getText key="text.description.33" />:</th>
	<td class="jobconfig-data">
	    <select title='<msg:getText key="portal.jdlrsl.command" />' name=pdesc id="desc_key" onchange="javascript:descriptionHandler();getRemoteTextareaOptions('j='+document.getElementById('desc_key').value+'&m=GetDescInfo');">
	    <option value=""></option>
	    <c:forEach var="tmp" items="${jobdesc}">
		<option value="${tmp.key}"> <c:out value="${tmp.value}" escapeXml="true"  /></option>
	    </c:forEach>
	    </select>
	</td>
    </tr>
    <tr><td colspan="2" class="jobconfig-data"><div id="desc_txt">${defmsg}</div></td></tr>
    <tr>
	<th class="jobconfig-header">
	    <msg:getText key="text.description.35" />:
	</th>
	<td class="jobconfig-data">
	    <textarea title='<msg:getText key="config.jdlrsl.value" />' disabled="true" id="desc_value" rows=5" cols="30" >${defvl}</textarea>
	</td>
    </tr>
    <tr>
	<td colspan="3">
	    <center>
	    <input type="image" src="${pageContext.request.contextPath}/imgs/add_64.png" disabled="true" id="desc_butt" onclick="javascript:setRemoteParamDOMValue('desc_key,desc_value');getConfForm('m=GetDescriptionValues')" value="<msg:getText key="button.add" />">
	    </center>
	</td>
    </tr>           
</table>    	

</div>
    
<div id="dtmp">

<msg:getText key="text.description.36" />:
<msg:help id="helptext" tkey="help.descvalue" img="${pageContext.request.contextPath}/img/help.gif" />
<table>

	<c:forEach var="item" items="${keys}">
	    <tr>
		<td class="jobconfig-header" style="border: 2px solid #ffffff">${fn:substringAfter(item.key,".key")}</td>
		<td class="jobconfig-data" style="border: 2px solid #ffffff"><c:out value="${item.value}" escapeXml="true"  /></td>
	    </tr>
	</c:forEach>
    </table>
</div>
	
