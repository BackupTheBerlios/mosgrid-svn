<%--
new unicore grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="newdata" >
    <caption><f:message key="caption.unicore.new" /></caption>
    <form method="post" action="conf">
    <tr>
        <th class="ln0"><f:message key="new.unicore.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="punicorename" size="25" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="general.status" /></th>
        <td class="ln1">
            <input type="radio" name="penabled" checked="true" value="1" /><f:message key="general.yes" />
            <input type="radio" name="penabled" value="0" /><f:message key="general.no" />
        </td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="unicore.keystore" /></th>
        <td class="ln0">
            <input type="text" name="pkeystore" />
        </td>
    </tr>

    <tr>
        <th class="ln1"><f:message key="unicore.keypass" /></th>
        <td class="ln1">
            <input type="text" name="pkeypass" />
        </td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="unicore.keyalias" /></th>
        <td class="ln0">
            <input type="text" name="pkeyalias" />
        </td>
    </tr>

    <tr>
        <th class="ln1"><f:message key="unicore.subjectdn" /></th>
        <td class="ln1">
            <input type="text" name="psubjectdn" />
        </td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="unicore.truststore" /></th>
        <td class="ln0">
            <input type="text" name="ptruststore" />
        </td>
    </tr>

    <tr>
        <th class="ln1"><f:message key="unicore.trustpass" /></th>
        <td class="ln1">
            <input type="text" name="ptrustpass" />
        </td>
    </tr>

    <tr>
        <td colspan="2"><input type="image" src="imgs/save.png" value="<f:message key="form.ok" />" /><br />
            (<f:message key="form.ok" />)
        </td>
    </tr>
</form>
</table>
</f:bundle>
