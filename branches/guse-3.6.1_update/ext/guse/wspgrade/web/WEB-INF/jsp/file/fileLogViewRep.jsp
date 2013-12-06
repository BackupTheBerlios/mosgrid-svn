
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
<jsp:useBean id="fileName" class="java.lang.String" scope="request"/>
<jsp:useBean id="showErrorButton" class="java.lang.String" scope="request"/>


<form method="post" portletMode="VIEW" action="${pURL}">
 <input type="hidden" name="action" id="action" value=""/>
<input type="hidden" name="fileName" id="action" value=""/>
<table>
    <tr bgcolor="#CCCCFF">
		<td colspan=4 align=center><b>Log viewer</b></td>
	</tr>
</table>

<table>
	<tr bgcolor=#ffffff><td>
		<table border=0 align=center width=100%>
		
		<tr><td align="center">
			<table border=1 cellpadding="30" bgcolor="ivory">
			<tr><td>
				<table border=0>
					<% 
					String[] log = fileBean.getLogLines();
					if ( log.length == 0 ){ 
					%>
					<tr><td>( Log file is still empty. )</td></tr>
					<% 
					} else {
						for ( int g=0; g<log.length; g++){
					%>
						<tr><td><%=""+log[g]%></td></tr>
					<%   } //for
					} //else 
					%>   
				</table>
			</td></tr>
			</table>
		</td></tr>	
		<tr bgcolor="#ffffff">
			<td colspan=4 align="center">				

                <lpds:submit  actionID="action" actionValue="doGoBackRep" paramID="fileName" paramValue='<%=fileName%>' cssClass="portlet-form-button" txt="Back" tkey="false" />
				
			</td>
		</tr>
		</table>
	</td></tr>
</table>

<table>
	<tr bgcolor="#EFEFFF"><td>
		<b><i>Message:</i></b>&nbsp;&nbsp;<font color="990033">${message}</font>
	</td></tr>	
</table>
	</form>
