
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

<portlet:renderURL var="pURL" portletMode="VIEW">
    <portlet:param name="authfull" value="account" />
</portlet:renderURL>

<form method="post" action="${pURL}" >
    <input type="submit" value="authentication" />
</form>
<div style="display:none">
<iframe id="cauth" src="${surl}?grid=${grid}&user_name=${user}" height="1px" width="1px" ></iframe>
</div>

