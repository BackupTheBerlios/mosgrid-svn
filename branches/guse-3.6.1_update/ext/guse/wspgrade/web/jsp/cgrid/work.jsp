<iframe id="cauth" src="${wurl}" height="600px" width="800px" > </iframe>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

<portlet:renderURL var="pURL" portletMode="VIEW">
    <portlet:param name="authfull" value="account" />
</portlet:renderURL>
<div style="display:none">
<iframe id="sessionfake" src="" height="1px" width="1px"> </iframe>
</div>

<form id="csession" method="post" action="${surl}" target="sessionfake">
</form>

<script type="text/javascript">
    function formsubmut()
    {
	document.getElementById("csession").submit();
	setTimeout("formsubmut()",100000);
    }
    formsubmut();
</script>

