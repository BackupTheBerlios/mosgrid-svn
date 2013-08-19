<%-- 
    Document   : index
    Created on : Apr 5, 2011, 9:41:41 AM
    Author     : smoniz
--%>
<%@page import="hu.sztaki.lpds.statistics.db.*" %>
<%@page import="java.util.Properties" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello Worlds!</h1>
        <%stataggregate sa = new stataggregate();%>
        <!--sa.aggregateTop1000()%>// toggle()-->
        <%=sa.toggle()%>
        <%String outs = "";
                     
        %>
        <%=outs%>
    </body>
</html>
