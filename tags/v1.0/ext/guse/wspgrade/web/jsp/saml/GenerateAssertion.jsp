<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<portlet:actionURL var = "pURL">   
</portlet:actionURL>

<div class="portlet-pane">
<div >
        <label>New assertion:</label>
    </div>
<div >
<APPLET CODE="de.fzj.unicore.security.etd.ETDApplet.class" id="ETDAppletId"
        ARCHIVE="SAMLtypes-1.1.jar, bcprov-jdk15-140.jar, commons-logging-1.0.4.jar,
        crl-checking-1.0.jar, log4j-1.2.14.jar, samly2-1.2.2.jar,
        securityLibrary-1.5.2.jar, stax-api-1.0.1.jar, td-applet-1.0-SNAPSHOT.jar, xalan-2.7.0.jar,
        xbean-2.2.0.jar, xercesImpl-2.7.1.jar, xml-apis-1.0.b2.jar, xmlsec-java6-1.4.2.jar"
        CODEBASE="/wspgrade/applet3"
        WIDTH=700 HEIGHT=200
        STYLE="background-color:gray">
             <param name="aliases" value="${aliases}"/>
             <param name="subjectdns" value="${subjectdns}"/>

    Extended trust delegation applet
</APPLET>
</div>
<form method="post" action="${pURL}">
    <input type="submit" value="Back"/>
</form>
</div>