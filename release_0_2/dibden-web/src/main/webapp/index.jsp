<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<body>
<h2>Dibden Web Application</h2>

<p>Things that don't need registration:</p>
<ul>
    <li><a href="registration.do">Registration</a></li>
    <li><a href="forgottenPassword.do">Forgotten password</a></li>
</ul>
<p>Things that need authentication:</p>
<ul>
    <li><a href="./secured/displayProfile.do">View profile</a></li>
    <li><a href="./secured/changePassword.do">Change password</a></li>
</ul>
<security:authorize ifAllGranted="ROLE_ADMIN">
    <p>You are an administrator:</p>
    <ul>
        <li><a href="./secured/admin/listUsers.do">List Users</a></li>
        <li><a href="./secured/admin/listRoles.do">List Roles</a></li>
    </ul>
</security:authorize>
</body>
</html>
