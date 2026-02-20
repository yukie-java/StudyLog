<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.StudyLog" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit</title>
</head>
<body>
<h2>編集</h2>

<%
StudyLog log = (StudyLog) request.getAttribute("log");
String error = (String) request.getAttribute("error");
%>


<% if (error != null) { %>
  <p style="color:red;"><%= error %></p>
<% } %>

<form action="<%= request.getContextPath() %>/EditStudyLogServlet" method="post">
  <input type="hidden" name="id" value="<%= log.getId() %>">

  日付：<input type="date" name="studyDate" value="<%= log.getStudyDate() %>" required><br>
  科目：<input type="text" name="subject" value="<%= log.getSubject() %>" required><br>
  分：<input type="number" name="minutes" min="1" value="<%= log.getMinutes() %>" required><br>
  メモ：<input type="text" name="memo" value="<%= log.getMemo() == null ? "" : log.getMemo() %>"><br>

  <input type="submit" value="更新">
</form>

<p><a href="<%= request.getContextPath() %>/StudyLogServlet">戻る</a></p>
</body>
</html>