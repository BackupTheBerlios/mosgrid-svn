
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html" %>
<portlet:actionURL var="pURL" portletMode="VIEW" >
</portlet:actionURL>


<portlet:defineObjects/>
<%@ taglib uri="/sztaki" prefix="lpds"%>

<jsp:useBean id="fileNames" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="path" class="java.lang.String" scope="request"/>
<jsp:useBean id="fileName" class="java.lang.String" scope="request"/>
<jsp:useBean id="userId" class="java.lang.String" scope="request"/>
<jsp:useBean id="isDirectory" class="java.lang.String" scope="request"/>
<jsp:useBean id="status" class="java.lang.String" scope="request"/>

<form method="post" portletMode="VIEW" action="${pURL}">
 <input type="hidden" name="action" id="action" value=""/>
 <input type="hidden" name="itemName" id="action" value=""/>
 <input type="hidden" name="fromPage" id="action" value=""/>

<table>
	<tr bgcolor="#ffffff"> <td> <table>
        <tr>
	<tr>
		<% if(isDirectory.equals("1")) { %>
			<td>Directory to download:<td>
		<% } else { %>
			<td>File to download:<td>
		<% } %>
		
		<% String fullFileName2 = path + "/" + fileName;%>
		<td><%=fullFileName2%><td>
	</tr>

	<% String link2 = "${pageContext.request.contextPath}/tmp/" + userId + "/fileDownloadDir/" + fileName + ".tar";%>
      
	<tr> 
	<td>Download</td>
         <td align=left>
                <img src="${pageContext.request.contexPath}/img/download.gif" onClick="self.location='<%=link2%>';return true"
                 onMouseOver="self.status='Downloading...'; return true" onMouseOut="self.status=''; return true"/>
     </td> </tr>
   

	</table> </td> </tr>

</table>


<table>
	<tr><td>
 <lpds:submit  actionID="action" actionValue="doGoBack" paramID="itemName" paramValue='<%=fileName%>' param0ID="fromPage" param0Value="delete" cssClass="portlet-form-button" txt="Back" tkey="false" />
        </td></tr>
</table>

<table>
  <tr bgcolor="#EFEFFF"><td>
    <b><i>Message:</i></b>&nbsp;&nbsp;<font color="990033"> </font>
  </td></tr>
</table>

</form>
