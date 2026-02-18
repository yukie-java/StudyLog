<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Login</title></head>
<body>
<h1>Login</h1>

<a href="<%= request.getContextPath() %>/LogoutServlet">ログアウト</a>


<% String error = (String)request.getAttribute("error"); %>
<% if (error != null) { %>
  <p style="color:red;"><%= error %></p>
<% } %>

<form action="<%= request.getContextPath() %>/LoginServlet" method="post">
  <div>
    <label>Login ID:</label>
    <input type="text" name="loginId" required>
  </div>
  <div>
    <label>Password:</label>
    <input type="password" name="password" required>
  </div>
  <button type="submit">Login</button>
</form>
</body>
</html>
