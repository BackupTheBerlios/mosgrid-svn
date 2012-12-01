
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
<jsp:useBean id="path" class="java.lang.String" scope="request"/>
<jsp:useBean id="showErrorButton" class="java.lang.String" scope="request"/>

<jsp:useBean id="prefList" class="java.util.ArrayList" scope="request"/>

<form method="post" enctype="multipart/form-data" portletMode="VIEW" action="${pURL}">
<table>
<% String path2 = path; %>
	<tr  bgcolor="#ffffff">
		<td><b>Select a file to upload to:</b> <%=path2%> </td>
                <td><input  class="portlet-form-button" type="file" name="uploadfile" /></td>
	</tr>
</table>

<table>


 <tr><td><table>
<tr>	

<td rowspan=4 valign="top">
	<table>
	<tr> 
		<td> <b>Storage Element:</b></td>
		<td><select id="SEListBoxBean_select" name="SEListBoxBean_select">

			 <c:forEach var="ses" items="${SEListBoxBean}">
                                <option Value = ${ses}>${ses}</option>
                         </c:forEach>

                    </select>
		</td>
	</tr>
	<tr>
		<td><b>Upload Name:</b> </td>
                <td><input type="text" id="uploadName" name="uploadName" value="" /> </td>
	</tr>
	
	<tr>          
        <td>
     
        </td>
        <td>

        <lpds:submit  actionID="action" actionValue="doUpload"  cssClass="portlet-form-button" txt="Upload" tkey="false" />

        </td>
		
    </tr>
	</table>
	
</td>

<td rowspan=4 width="10%"> </td>
<td rowspan=4> </td>
<td rowspan=4> </td>

<td valign="top">
	
	<table>
	<tr>
		<td><b>File Mode</b> </td>
	</tr>
	<% if (prefList.size() == 9) { %>
	<tr> <td><b>User:</b></td>
		<% if( (prefList.get(0)).equals("1") ) { %>
                <td><input type="checkbox" id="ownRead" name="ownRead" value="Read" checked="true" /></td><td><ui:text value="Read"/></td>
		<% } else { %>
			<td><input type="checkbox" id="ownRead" name="ownRead" value="Read" checked="false" /></td><td><ui:text value="Read"/></td>
		<% } %>
		<% if( (prefList.get(1)).equals("1") ) { %>
			<td><input type="checkbox" id="ownWrite" name="ownWrite" value="Write" checked="true" /></td><td><ui:text value="Write"/></td>
		<% } else { %>
			<td><input type="checkbox" id="ownWrite" name="ownWrite" value="Write" checked="false" /></td><td><ui:text value="Write"/></td>
		<% } %>
		<% if( (prefList.get(2)).equals("1") ) { %>
			<td><input type="checkbox" id="ownExecute" name="ownExecute" value="Execute" checked="true" /></td><td><ui:text value="Execute"/></td>
		<% } else { %>
			<td><input type="checkbox" id="ownExecute" name="ownExecute" value="Execute" checked="false" /></td><td><ui:text value="Execute"/></td>
		<% } %>	
	</tr>
	<tr><td><b>Group:</b></td>
		<% if( (prefList.get(3)).equals("1") ) { %>
			<td><input type="checkbox" id="grRead" name="grRead" value="Read"  checked="true" /></td><td><ui:text value="Read"/></td>
		<% } else { %>
			<td><input type="checkbox" id="grRead" name="grRead" value="Read" checked="false" /></td><td><ui:text value="Read"/></td>
		<% } %>
		<% if( (prefList.get(4)).equals("1") ) { %>
		<td><input type="checkbox" id="grWrite" name="grWrite" value="Write" checked="true" /></td><td><ui:text value="Write"/></td>
		<% } else { %>
		<td><input type="checkbox" id="grWrite" name="grWrite" value="Write" checked="false" /></td><td><ui:text value="Write"/></td>
		<% } %>
			<% if( (prefList.get(5)).equals("1") ) { %>
			<td><input type="checkbox" id="grExecute" name="grExecute" value="Execute" checked="true" /></td><td><ui:text value="Execute"/></td>
		<% } else { %>
			<td><input type="checkbox" id="grExecute" name="grExecute" value="Execute" checked="false" /></td><td><ui:text value="Execute"/></td>
		<% } %>	
	</tr>
	<tr><td><b>Others:</b></td>
		<% if( (prefList.get(6)).equals("1") ) { %>
			<td><input type="checkbox" id="othRead" name="othRead" value="Read" checked="true" /></td><td><ui:text value="Read"/></td>
		<% } else { %>
			<td><input type="checkbox" id="othRead" name="othRead" value="Read" checked="false" /></td><td><ui:text value="Read"/></td>
		<% } %>
		<% if( (prefList.get(7)).equals("1") ) { %>
			<td><input type="checkbox" id="othWrite" name="othWrite" value="Write" checked="true" /></td><td><ui:text value="Write"/></td>
		<% } else { %>
			<td><input type="checkbox" id="othWrite" name="othWrite" value="Write" checked="false" /></td><td><ui:text value="Write"/></td>
		<% } %>
		<% if( (prefList.get(8)).equals("1") ) { %>
			<td><input type="checkbox" id="othExecute" name="othExecute" value="Execute" checked="true" /></td><td><ui:text value="Execute"/></td>
		<% } else { %>
			<td><input type="checkbox" id="othExecute" name="othExecute"  value="Execute" checked="false" /></td><td><ui:text value="Execute"/></td>
		<% } %>
	</tr>
	<% } else { %>
	<tr><td><b>User:</b></td>
		<td><input type="checkbox" id="ownRead" name="ownRead" value="Read" checked="true" /></td><td><ui:text value="Read"/></td>
		<td><input type="checkbox" id="ownWrite" name="ownWrite" value="Write" checked="true" /></td><td><ui:text value="Write"/></td>
		<td><input type="checkbox" id="ownExecute" name="ownExecute" value="Execute" checked="false" /></td><td><ui:text value="Execute"/></td>
	</tr>
	<tr><td><b>Group:</b></td>
		<td><input type="checkbox" id="grRead" name="grRead" value="Read" checked="true" /></td><td><ui:text value="Read"/></td>
		<td><input type="checkbox" id="grWrite" name="grWrite" value="Write" checked="true" /></td><td><ui:text value="Write"/></td>
		<td><input type="checkbox" id="grExecute" name="grExecute" value="Execute" checked="false" /></td><td><ui:text value="Execute"/></td>
	</tr>
	<tr><td><b>Others:</b>
		<td><input type="checkbox" id="othRead" name="othRead" value="Read" checked="true" /></td><td><ui:text value="Read"/></td>
		<td><input type="checkbox" id="othWrite" name="othWrite" value="Write" checked="false" /></td><td><ui:text value="Write"/></td>
		<td><input type="checkbox" id="othExecute" name="othExecute" value="Execute" checked="false" /></td><td><ui:text value="Execute"/></td>
	</tr>
	<% } %>
	</table>	
</td>

</tr>

</table></td></tr>






	<tr><td> 
        

	</td></tr>
<tr bgcolor="#EFEFFF"><td>
    <b><i>Message:</i></b>&nbsp;&nbsp;<font color="990033">${message}</font>
  </td></tr>
  <% if(showErrorButton.equals("1")) { %>
  <tr> <td> <ui:actionsubmit action="doShowError" value="Error"/> </td></tr>
  <% } %>


</table>
  </form>


  <form method="post" portletMode="VIEW" action="${pURL}">
       <input type="hidden" name="action" id="action" value=""/>
        <input type="hidden" name="fromPage" id="fromPage" value=""/>
        <input type="hidden" name="path" id="path" value="${path}"/>
      <lpds:submit  actionID="action" actionValue="doGoBackUpload" cssClass="portlet-form-button" txt="Back" tkey="false" />
  </form>
