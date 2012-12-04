
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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

<jsp:useBean id="showErrorButton" class="java.lang.String" scope="request"/>


<form method="post" portletMode="VIEW" action="${pURL}">
 <input type="hidden" name="action" id="action" value=""/>
 <input type="hidden" name="fromPage" id="fromPage" value=""/>
 <input type="hidden" name="fileName" id="fileName" value=""/>
<table>
	<tr bgcolor="#ffffff"> <td><table> 
	<td><b>File: </b></td>
		<% String fullFileName = path + "/" + fileName;%>
		<td><%=fullFileName%><td>
	</table></td>
	</tr>
</table>

 <table>
	<tr bgcolor="#CCCCFF">
		<td><b>Replicas</b></td>
	</tr>
</table>

<table>


<tr>
<input type="hidden" id="replicaSize" name="replicaSize" value="${fn:length(eplicaList)}" />
	<td><select id="replicaListBoxBean_select" name="replicaListBoxBean_select" size="5" >


                 <c:forEach var="rep" items="${replicaList}">
                                <option Value = ${rep}>${rep}</option>
                         </c:forEach>

		</select>
	</td>
</tr>

<tr>
	<td>
            <lpds:submit  actionID="action" actionValue="doDeleteReplica" paramID="fileName" paramValue='<%=fileName%>' cssClass="portlet-form-button" txt="Delete" tkey="false" />
		
	</td>

</tr>
</table>

 <table>
	<tr bgcolor="#CCCCFF">
		<td><b>New Replica</b></td>
	</tr>
</table>
 
<table>
<tr><td><table>
	
	<tr> 
		<td align="up"> <b>Storage Element:</b></td>
		<td > <select id="SEListBoxBean_select" name="SEListBoxBean_select">
			
			 <c:forEach var="ses" items="${SEListBoxBean}">
                                <option Value = ${ses}>${ses}</option>
                         </c:forEach>
				
                    </select> </td>
	</tr>

	<tr>
		<td>
        <lpds:submit  actionID="action" actionValue="doReplicate" paramID="fileName" paramValue='<%=fileName%>' cssClass="portlet-form-button" txt="Replicate" tkey="false" />
		</td>
	</tr>
</table></td></tr>
</table>

<table>

	<tr><td>
        <lpds:submit  actionID="action" actionValue="doGoBack" paramID="fromPage" paramValue='replicas' cssClass="portlet-form-button" txt="Back" tkey="false" />
        </td></tr>

</table>

<table>
	
  <tr bgcolor="#EFEFFF"><td>
    <b><i>Message:</i></b>&nbsp;&nbsp;<font color="990033">${message}</font>
  </td></tr>
  <% if(showErrorButton.equals("1")) { %>
  <tr> <td> <ui:actionsubmit action="doShowRepError" value="Error"> 
			<ui:actionparam name="fileName" value="<%=fileName%>"/>
			</ui:actionsubmit>
		</td>
  </tr>
  <% } %>
	
</table>

</form>

