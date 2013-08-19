<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<msg:help id="helptext" tkey="help.viswkf" img="${pageContext.request.contextPath}/img/help.gif" />

<portlet:defineObjects/>
<portlet:actionURL var="pURL" portletMode="VIEW" >
    <portlet:param name="guse" value="doDetails" />
</portlet:actionURL>
<form method="post" action="${pURL}"> 
<input type="hidden" name="workflow" value="<c:out value="${wkfname}" escapeXml="true" />" />
    <input type="submit" value="back">
</form>

<applet codebase="<msg:surl dest="portal" url="${portalurl}"/>/jsp/pgrade" code="hu.sztaki.lpds.pgportal.visualizer.client.ClientApplet.class"
          archive="visualizer.jar,xmlrpc.jar" width=800 height=600>
 <param name="VISWFSServiceUrl" value="<msg:surl dest="wfs" url="${wfsurl}"/>/xmlrpcvis">
 <param name="portalid" value="${portalurl}">
 <param name="userid" value="${userID}">
     <param name="wfname" value="<c:out value="${wkfname}" escapeXml="true" />">
 <param name="rtid"   value="${rtid}">
          alt="Your browser understands the &lt;APPLET&gt; tag but isn't running the applet, for some reason."
               Your browser is completely ignoring the &lt;APPLET&gt; tag!
</applet>
