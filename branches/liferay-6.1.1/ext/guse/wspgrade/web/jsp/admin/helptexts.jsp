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
<portlet:actionURL var="pURL" >
    <portlet:param name="guse" value="admin_mod_portaltxt" />
</portlet:actionURL>

<portlet:resourceURL var="ajaxURL">
    <portlet:param name="d" value="content" />
</portlet:resourceURL>


<portlet:resourceURL var="imageURL">
    <portlet:param name="ptype" value="Image" />
</portlet:resourceURL>
<portlet:resourceURL var="flashURL">
    <portlet:param name="ptype" value="Flash" />
</portlet:resourceURL>
<portlet:resourceURL var="mediaURL">
    <portlet:param name="ptype" value="Media" />
</portlet:resourceURL>

<div class="FullCenter">


<script language="javascript" type="text/javascript" src="${pageContext.request.contextPath}/tinymce/jscripts/tiny_mce/tiny_mce.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.js"></script>
<script language="javascript" type="text/javascript">
<%--
        tinyMCE.init({
	                mode : "textareas",
	                theme : "advanced",
	                plugins : "devkit,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras",
	                theme_advanced_buttons1_add_before : "save,newdocument,separator",
	                theme_advanced_buttons1_add : "fontselect,fontsizeselect",
	                theme_advanced_buttons2_add : "separator,insertdate,inserttime,preview,separator,forecolor,backcolor,advsearchreplace",
	                theme_advanced_buttons2_add_before: "cut,copy,paste,pastetext,pasteword,separator,search,replace,separator",
	                theme_advanced_buttons3_add_before : "tablecontrols,separator",
	                theme_advanced_buttons3_add : "emotions,iespell,media,advhr,separator,print,separator,ltr,rtl,separator,fullscreen",
	                theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops,|,cite,abbr,acronym,del,ins,|,visualchars,nonbreaking",
	                theme_advanced_toolbar_location : "top",
	                theme_advanced_toolbar_align : "left",
	                theme_advanced_path_location : "bottom",
	                content_css : "example_full.css",
	                plugin_insertdate_dateFormat : "%Y-%m-%d",
	                plugin_insertdate_timeFormat : "%H:%M:%S",
            		extended_valid_elements : "hr[class|width|size|noshade],font[face|size|color|style],span[class|align|style]",
	                external_link_list_url : "${pageContext.request.contextPath}/tinymce/example_link_list.js",
	                external_image_list_url : "${pageContext.request.contextPath}/tce?ptyp=Image",
	                flash_external_list_url : "${pageContext.request.contextPath}/tce?ptyp=Flash",
	                media_external_list_url : "${pageContext.request.contextPath}/tce?ptyp=Media",
//	                file_browser_callback : "fileBrowserCallBack",
	                theme_advanced_resize_horizontal : false,
	                theme_advanced_resizing : true,
	                nonbreaking_force_tab : true,
	                apply_source_formatting : true
	        });
--%>
    	tinyMCE.init({
            mode : "textareas",
            theme : "advanced",
            plugins : "devkit,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras",
	                theme_advanced_buttons1_add_before : "save,newdocument,separator",
	                theme_advanced_buttons1_add : "fontselect,fontsizeselect",
	                theme_advanced_buttons2_add : "separator,insertdate,inserttime,preview,separator,forecolor,backcolor,advsearchreplace",
	                theme_advanced_buttons2_add_before: "cut,copy,paste,pastetext,pasteword,separator,search,replace,separator",
	                theme_advanced_buttons3_add_before : "tablecontrols,separator",
	                theme_advanced_buttons3_add : "emotions,iespell,media,advhr,separator,print,separator,ltr,rtl,separator,fullscreen",
	                theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops,|,cite,abbr,acronym,del,ins,|,visualchars,nonbreaking",
	                theme_advanced_toolbar_location : "top",
	                theme_advanced_toolbar_align : "left",
	                theme_advanced_path_location : "bottom",
	                content_css : "${pageContext.request.contextPath}/tinymce/examples/example_full.css",
	                plugin_insertdate_dateFormat : "%Y-%m-%d",
	                plugin_insertdate_timeFormat : "%H:%M:%S",
            		extended_valid_elements : "hr[class|width|size|noshade],font[face|size|color|style],span[class|align|style]",
	                external_link_list_url : "${pageContext.request.contextPath}/tinymce/example_link_list.js",
	                external_image_list_url : "${imageURL}",
	                flash_external_list_url : "${flashURL}",
	                media_external_list_url : "${mediaURL}",
	                theme_advanced_resize_horizontal : false,
	                theme_advanced_resizing : true,
	                nonbreaking_force_tab : true,
	                apply_source_formatting : true
        });

    </script>

<form action="${pURL}" method="post">
<table style="margin-left:10px">
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
	<textarea id="content" name="content" cols="100" rows="50">${def}</textarea>
	</td>
    </tr>
    <tr>	    	
	<td colspan="2"></td>
    </tr>
</table>	    
<input type="submit">
</form>
