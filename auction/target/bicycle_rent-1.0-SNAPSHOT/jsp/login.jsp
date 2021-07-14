<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="en_En" scope="session" />

<fmt:setBundle basename="localization/locale"/>

<fmt:message key="login.login" var="login"/>
<fmt:message key="login.password" var="password"/>
<fmt:message key="login.sign_in" var="sign_in"/>
<fmt:message key="login.language" var="language"/>

<!DOCTYPE html>
<html>
<head>
  <title>${sign_in}</title>
</head>
<body>
<form name="loginForm" method="POST" action="controller">
  <input type="hidden" name="command" value="login" />
  ${login}:<br/>
  <input type="text" name="login" value=""/>
  <br/>
  ${password}:<br/>
  <input type="password" name="password" value=""/>
  <br/>
  <input type="submit" value="${sign_in}"/>
  <br/>
  <input type="submit"  value="${language}"/>
</form>
<hr/>
</body>
</html>
