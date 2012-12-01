<form method="post" action="init">
<table>
    <tr>
        <th>information service url</th>
        <td><input type="text" value="http://localhost:8080/information" name="service.url" size="50"/> </td>
    </tr>
    <tr>
        <th>resource id</th>
        <td><input type="text" value="/services/urn:infoservice" name="resource.id" size="50" /> </td>
    </tr>
    <tr>
        <th>JDBC Driver</th>
        <td><input id="p0" type="text" value="com.mysql.jdbc.Driver" name="guse.system.database.driver" size="50" /><input type="button" value="clear" onclick="document.getElementById('p0').value='';"> </td>
    </tr>
    <tr>
        <th>Database URL</th>
        <td><input id="p1" type="text" value="jdbc:mysql://localhost:3306/gusedb" name="guse.system.database.url" size="50" /><input type="button" value="clear" onclick="document.getElementById('p1').value='';"> </td>
    </tr>
    <tr>
        <th>Database User</th>
        <td><input id="p2" type="text" value="guseuser" name="guse.system.database.user" size="50" /> <input type="button" value="clear" onclick="document.getElementById('p2').value='';"></td>
    </tr>
    <tr>
        <th>Database User's password</th>
        <td><input id="p3" type="password" value="gUSE:pass-123" name="guse.system.database.password" size="50" /> <input type="button" value="clear" onclick="document.getElementById('p3').value='';"></td>
    </tr>
</table>
    <input type="submit" value="send" />
</form>

