
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
<jsp:useBean id="fileName" class="java.lang.String" scope="request"/>
<jsp:useBean id="path" class="java.lang.String" scope="request"/>
<jsp:useBean id="type" class="java.lang.String" scope="request"/>
<jsp:useBean id="completed" class="java.lang.String" scope="request"/>
<jsp:useBean id="showErrorButton" class="java.lang.String" scope="request"/>


<form method="post"  portletMode="VIEW" action="${pURL}">
 <input type="hidden" name="action" id="action" value="00"/>
 <input type="hidden" name="fileName" id="fileName" value=""/>
 <input type="hidden" name="path" id="path" value=""/>
 <input type="hidden" name="fromPage" id="fromPage" value=""/>
 <input type="hidden" name="itemName" id="itemName" value=""/>
<% String typefile = type + "@" + fileName; %>
            
<% if(completed.equals("0")) { %>
<table>

	<tr bgcolor="#ffffff">
            <td>
                <table align=center>

                    <% if(type.equals("+")) { %>
	
                        <tr>
                            <% String fullFileName =   path + "/" + fileName;%>
                                <td align=center><b>Are you sure to remove all replicas of the files under the directory:     </b><%=fullFileName%><td>
                        </tr>
                        <tr>
                            <td>(Non-empty directories under this directory will not be effected. The directory will be removed if it is finally empty.)</td>
                        </tr>
	

                <% } else { %>
                <tr>
		<td align=center><b>Are you sure to remove all replicas of the file:</b><td>
		<% String fullFileName =  "lfn:" + path + "/" + fileName;%>
		<td><%=fullFileName%><td>
                </tr>
                <% } %>
	
                </table>
            </td>
        </tr>

	<tr bgcolor="#ffffff">
	<td align=center>

            <lpds:submit actionID="action" actionValue="doDelete" paramID="fileName" paramValue="<%=typefile%>" param0ID="path" param0Value='<%=path%>'  cssClass="portlet-form-button" txt="Remove" tkey="false" />
            <lpds:submit  actionID="action" actionValue="doGoBack" paramID="itemName" paramValue='<%=typefile%>' param0ID="fromPage" param0Value="delete" cssClass="portlet-form-button" txt="Cancel" tkey="false" />
	</td></tr>

</table>


<% } %>
<table>
	<tr><td>
         <lpds:submit  actionID="action" actionValue="doGoBack" paramID="itemName" paramValue='<%=typefile%>' param0ID="fromPage" param0Value="delete" cssClass="portlet-form-button" txt="Back" tkey="false" />
         </td></tr>

   <tr bgcolor="#EFEFFF"><td>
       <b><i>Message:</i></b>&nbsp;&nbsp;<font color="990033">${message}</font> </td>
   </tr>
   <% if(showErrorButton.equals("1")) { %>
   <tr> <td>

   <lpds:submit  actionID="action" actionValue="doShowRemoveErrork" paramID="itemName" paramValue='<%=typefile%>' cssClass="portlet-form-button" txt="Back" tkey="false" />

   		</td>
   </tr>
   <% } %>
</table>
</form>


	

