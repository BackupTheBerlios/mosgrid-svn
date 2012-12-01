
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html" %>
<portlet:actionURL var="pURL" portletMode="VIEW" >
</portlet:actionURL>


<portlet:defineObjects/>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<jsp:useBean id="fileBean"
             class="hu.sztaki.lpds.pgportal.portlets.file.FileBean"
             scope="request"/>
<jsp:useBean id="ownerACL" class="java.lang.String" scope="request"/>
<jsp:useBean id="groupACL" class="java.lang.String" scope="request"/>
<jsp:useBean id="otherACL" class="java.lang.String" scope="request"/>
<jsp:useBean id="fileName" class="java.lang.String" scope="request"/>
<jsp:useBean id="path" class="java.lang.String" scope="request"/>
<jsp:useBean id="isDir" class="java.lang.String" scope="request"/>

<% String fDir;
   if(isDir.equals("1"))   
   	fDir= "Directory";
   else
	fDir= "File";
%>

<form method="post" portletMode="VIEW" action="${pURL}">
 <input type="hidden" name="action" id="action" value=""/>
 <input type="hidden" name="fileName" id="fileName" value="00"/>
 <input type="hidden" name="file_arguments" id="file_arguments" value="00"/>
 <input type="hidden" name="itemName" id="itemName" value="00"/>
 <input type="hidden" name="fromPage" id="fromPage" value="00"/>
<table>
	<tr bgcolor="#CCCCFF">
		<td><b>Change Mode</b></td>
	</tr>
</table>

<table>
<tr><td><table>
        <% String fullName = "lfn:" + path + "/" + fileName; %>
        <tr><td> <b><%=fDir+" Name:"%></b> </td> <td><%=fullName%></td></tr>
        <tr><td> <b><%=fDir+" Mode:"%></b> </td> <td><%=ownerACL%></td></tr>
        <tr> <td></td> <td> <%=groupACL%> </td></tr>
        <tr> <td></td> <td> <%=otherACL%> </td></tr>
</table>
</td></tr>

<tr><td><table>
<tr><td><b>User:</b></td>

<% if (ownerACL.charAt(6) == '-') { %>
<td><input type="checkbox" name ="ownRead" id="ownRead" value="Read" /></td><td><ui:text value="Read"/></td>
<% } else { %>
    <td><input type="checkbox" name ="ownRead" id="ownRead" value="Read"  checked="true"/></td><td><ui:text value="Read"/></td>
<% } %>

<% if (ownerACL.charAt(7) == '-') { %> 
    <td><input type="checkbox" name ="ownWrite" id="ownWrite" value="Write"/></td><td><ui:text value="Write"/></td>
<% } else { %>
    <td><input type="checkbox" name ="ownWrite" id="ownWrite" value="Write" checked="true"/></td><td><ui:text value="Write"/></td>
<% } %>

<% if (ownerACL.charAt(8) == '-') { %>
    <td><input type="checkbox" name ="ownExecute" id="ownExecute" value="Execute"/></td><td><ui:text value="Execute"/></td>
<% } else { %>
    <td><input type="checkbox" name ="ownExecute" id="ownExecute" value="Execute" checked="true"/></td><td><ui:text value="Execute"/></td>
<% } %>

</tr>

<tr><td><b>Group:</b></td>
<% if (groupACL.charAt(7) == '-') { %>
    <td><input type="checkbox" name ="grRead" id="grRead" value="Read" /></td><td><ui:text value="Read"/></td>
<% } else { %>
    <td><input type="checkbox" name ="grRead" id="grRead" value="Read" checked="true"/></td><td><ui:text value="Read"/></td>
<% } %>   

<% if (groupACL.charAt(8) == '-') { %>
    <td><input type="checkbox" name ="grWrite" id="grWrite" value="Write"/></td><td><ui:text value="Write"/></td>
<% } else { %>
    <td><input type="checkbox" name ="grWrite" id="grWrite" value="Write" checked="true"/></td><td><ui:text value="Write"/></td>
<% } %>       

<% if (groupACL.charAt(9) == '-') { %>
    <td><input type="checkbox" name ="grExecute" id="grExecute" value="Execute"/></td><td><ui:text value="Execute"/></td>
<% } else { %>
    <td><input type="checkbox" name ="grExecute" id="grExecute" value="Execute" checked="true"/></td><td><ui:text value="Execute"/></td>
<% } %>   

</tr>

<tr><td><b>Others:</b>
<% if (otherACL.charAt(7) == '-') { %>
    <td><input type="checkbox" name ="othRead" id="othRead" value="Read" /></td><td><ui:text value="Read"/></td>
<% } else { %>
    <td><input type="checkbox" name ="othRead" id="othRead" value="Read" checked="true"/></td><td><ui:text value="Read"/></td>
<% } %>

<% if (otherACL.charAt(8) == '-') { %>
    <td><input type="checkbox" name ="othWrite" id="othWrite" value="Write"/></td><td><ui:text value="Write"/></td>
<% } else { %>
    <td><input type="checkbox" name ="othWrite" id="othWrite" value="Write" checked="true"/></td><td><ui:text value="Write"/></td>
<% } %>

<% if (otherACL.charAt(9) == '-') { %>
    <td><input type="checkbox" name ="othExecute" id="othExecute" value="Execute"/></td><td><ui:text value="Execute"/></td>
<% } else { %>
    <td><input type="checkbox" name ="othExecute" id="othExecute" value="Execute" checked="true"/></td><td><ui:text value="Execute"/></td>
<% } %>

</tr>
</table></td></tr>

<tr>
<td>
 <% String arguments = "0" + "@"+path+ "@"+ ownerACL+ "@"+ groupACL+ "@"+ otherACL; %>
 <lpds:submit  actionID="action" actionValue="doChmode" paramID="fileName" paramValue='<%=fileName%>' param0ID="file_arguments" param0Value="<%=arguments%>" cssClass="portlet-form-button" txt="Change Mode" tkey="false" />


</td>	
</tr>
</table>

<table>
	<tr><td> 
	<lpds:submit  actionID="action" actionValue="doGoBack" paramID="itemName" paramValue='<%=fileName%>' param0ID="fromPage" param0Value="update" cssClass="portlet-form-button" txt="Back" tkey="false" />
        </td></tr>

</table>

<table>
  <tr bgcolor="#EFEFFF"><td>
    <b><i>Message:</i></b>&nbsp;&nbsp;<font color="990033">${message}</font>
  </td></tr>
</table>

</form>

