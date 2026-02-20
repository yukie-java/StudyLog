<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.StudyLog" %>



<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>StudyLog</title>
 <style>
.flash{
  color: green;
  font-size: 18px;
  font-weight: bold;
  background: #f0fff0;
  border: 2px solid green;
  padding: 10px;
  margin: 15px 0;
  border-radius: 6px;
}
</style>

</head>
<body>

<h2>StudyLog</h2>


<p>
  ようこそ ${loginUser.name} さん |
  <a href="${pageContext.request.contextPath}/LogoutServlet">ログアウト</a>
</p>


<h3>今日（<%= request.getAttribute("today") %>）の合計：<%= request.getAttribute("totalToday") %> 分</h3>


<h3>ログ登録</h3>
<form action="<%= request.getContextPath() %>/StudyLogServlet" method="post">

  日付：<input type="date" name="studyDate" required><br>
  科目：<input type="text" name="subject" required><br>
  分：<input type="number" name="minutes" min="1" required><br>
  メモ：<input type="text" name="memo"><br>
  <input type="submit" value="登録">
</form>

<% 
String flash = (String)
request.getAttribute("flash");
if(flash != null){
%>
 <div class="flash"><%= flash %></div>
 
 
<%
}
%>

<h3>一覧</h3>

<table border="1">
<tr>
  <th>日付</th><th>科目</th><th>分</th><th>メモ</th><th>削除</th><th>編集</th>
</tr>

<%
List<StudyLog> list = (List<StudyLog>) request.getAttribute("logList");
if (list != null) {
  for (StudyLog log : list) {
%>
<tr>
<td><%= log.getStudyDate() %></td>
<td><%= log.getSubject() %></td>
<td><%= log.getMinutes() %></td>
<td><%= log.getMemo() %></td>

<td>
<a href="<%= request.getContextPath() %>/StudyLogServlet?action=delete&id=<%= log.getId() %>"
onclick="return confirm('削除しますか？');">削除</a>
</td>
 <td>
    <a href="<%= request.getContextPath() %>/EditStudyLogServlet?id=<%= log.getId() %>">編集</a>
  </td>

</tr>

<%
  }
}
%>

</table>

</body>
</html>
