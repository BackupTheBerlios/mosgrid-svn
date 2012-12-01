<%--
Portal message manager
@use:  
@inputs 
    name:msgs type:java.util.Vector<hu.sztaki.lpds.pgportal.portlets.admin.MessageBean>

--%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<portlet:defineObjects/>
<portlet:actionURL var="pURL">
    <portlet:param name="guse" value="admin_mod_portaltxt" />
</portlet:actionURL>
<portlet:actionURL var="aURL">
    <portlet:param name="guse" value="get_portaltxt" />
</portlet:actionURL>
<portlet:resourceURL var="ajaxURL">
    <portlet:param name="d" value="content" />
</portlet:resourceURL>

<div class="FullCenter">


<script language="javascript" type="text/javascript" src="${pageContext.request.contextPath}/tinymce/jscripts/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.js"></script>

<script language="javascript" type="text/javascript">
	tinyMCE.init({mode : "textareas",theme : "simple"});
</script>

<form action="${pURL}" method="post">
<table>
    <tr>
	<td><input type="radio" name="ptyp" id="ptyp0" value="0" checked="true"> <msg:getText key="text.deftexts.0" /> </td>
	<td>    
	    <select id="ptkey" name="ptkey" onchange="javascript:getNativeText('${ajaxURL}&j='+document.getElementById('ptkey').value);tinyMCE.setContent(document.getElementById('content').value);">
	    <c:forEach var="row" items="${msgs}" varStatus="ls">
		<option value="${row.tkey}">${row.desc}(${row.tkey})</option> 
		<c:if test="${ls.index==0}">
		    <c:set var="def" value="${row.txt}" />
		</c:if>
	    </c:forEach>
	    </select>
	</td>
    </tr>
    <tr>	    
	<td><input type="radio" name="ptyp" id="ptyp1" value="1"> <msg:getText key="text.deftexts.1" /> </td>
	<td>
	    <msg:getText key="text.deftexts.2" />:<input type="text" name="pntkey" id="pntkey"><br />
	    <msg:getText key="text.deftexts.3" />:<input type="text" name="pndesc" id="pndesc">
	</td>    
    </tr>
    <tr>	    
	<td colspan="2">
	<textarea id="content" name="content" cols="50" rows="5">${def}</textarea>
	</td>
    </tr>
    <tr>	    	
	<td colspan="2"></td>
    </tr>
</table>	    
<input type="submit">
</form>