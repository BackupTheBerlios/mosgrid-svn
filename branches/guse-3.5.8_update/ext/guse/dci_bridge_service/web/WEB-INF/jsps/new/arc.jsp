<%--
new arc grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="newdata">
    <caption><f:message key="caption.arc.new" /></caption>
    <form method="post" action="conf">
    <tr>
        <th class="ln0"><f:message key="new.arc.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="parcname" size="25" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="general.status" /></th>
        <td class="ln1">
            <input type="radio" name="penabled" checked="true" value="1" /><f:message key="general.yes" />
            <input type="radio" name="penabled" value="0" /><f:message key="general.no" />
        </td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="new.arc.accessdata" /></th>
        <td class="ln0"><input class="ln1" type="text" name="paccessdata" size="60" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="new.arc.type" /></th>
        <td class="ln1"><input class="ln0" type="text" name="ptype" size="60" /></td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="new.arc.config" /></th>
        <td class="ln0"><input class="ln0" type="text" name="pconfig" size="60"  /></td>
    </tr>

    <tr>
        <td colspan="2">
            <input type="image" src="imgs/save.png" value="<f:message key="form.ok" />" /><br />
            (<f:message key="form.ok" />)
        </td>
    </tr>
</form>
</table>
</f:bundle>