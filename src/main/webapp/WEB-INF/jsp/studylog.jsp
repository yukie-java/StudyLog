<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
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

<%
String loginType = (String)request.getAttribute("loginType");
if(loginType == null) loginType = "adult";
%>

<h2>StudyLog</h2>


<p>
  ようこそ ${loginUser.name} さん |
  <a href="${pageContext.request.contextPath}/LogoutServlet">ログアウト</a>
</p>


<h3>今日（<%= request.getAttribute("today") %>）の合計：<%= request.getAttribute("totalToday") %> 分</h3>


<h3>ログ登録</h3>
<form action="<%= request.getContextPath() %>/StudyLogServlet" method="post">

  日付：<input type="date" name="studyDate" required><br>
    科目:

 科目種別：

<% if("child".equals(loginType)) { %>

  <!-- childログインなら固定（adultを選べない） -->
  <input type="hidden" id="regSubjectType" value="child">
  <span>child（固定）</span>

<% } else { %>

  <!-- adultログインなら切替可能 -->
  <select id="regSubjectType" onchange="regChangeSubjectType()">
    <option value="adult" selected>adult</option>
    <option value="child">child</option>
  </select>

<% } %>

  <!-- child用 -->
  <div id="regChildSubjects">

    <select id="regChildSelect" onchange="regToggleOther()">
      <option value="">選択してください</option>
      <option>国語</option>
      <option>数学</option>
      <option>英語</option>
      <option>理科</option>
      <option>社会</option>
      <option value="other">その他</option>
    </select>

    <input type="text" id="regChildOther" placeholder="科目入力" style="display:none">

  </div>

  <!-- adult用 -->
  <div id="regAdultSubjects" style="display:none">
    <input list="subjectHistory" id="regAdultInput" placeholder="科目入力">
  </div>

  <!-- サーバに送るsubject -->
<input type="hidden" name="subject" id="regSubjectHidden">
<input type="hidden" name="subjectType" id="regSubjectTypeHidden" value="child">

  <br>
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

<h3>検索</h3>

<form action="<%= request.getContextPath() %>/StudyLogServlet" method="get">

  期間:
  <input type="date" name="from"
   value="<%= request.getAttribute("from")==null?"":request.getAttribute("from") %>">

  〜

  <input type="date" name="to"
   value="<%= request.getAttribute("to")==null?"":request.getAttribute("to") %>">

  <br>

  科目:

  <% if("child".equals(loginType)) { %>

    <!-- child固定 -->
    <select id="childSelect" onchange="toggleOther()">
      <option value="">選択してください</option>
      <option>国語</option>
      <option>数学</option>
      <option>英語</option>
      <option>理科</option>
      <option>社会</option>
      <option value="other">その他</option>
    </select>

    <input type="text" id="childOther" placeholder="科目入力" style="display:none">

    <input type="hidden" name="subject" id="subjectHidden">
    <input type="hidden" name="subjectType" id="subjectTypeHidden" value="child">

  <% } else { %>

    <!-- adult -->
    <input list="subjectHistory" id="adultInput" placeholder="科目入力">

    <datalist id="subjectHistory">
    <%
    List<String> subjects = (List<String>)request.getAttribute("subjects");
    if(subjects != null){
      for(String s : subjects){
    %>
      <option value="<%= s %>">
    <%
      }
    }
    %>
    </datalist>

    <input type="hidden" name="subject" id="subjectHidden">
    <input type="hidden" name="subjectType" id="subjectTypeHidden" value="adult">

  <% } %>

  <br>
  <input type="submit" value="検索">
  <a href="<%= request.getContextPath() %>/StudyLogServlet">リセット</a>

</form>

<h3>科目別合計</h3>

<%@ page import="java.util.Map" %>

<h3>科目別合計（child）</h3>
<table border="1">
<tr><th>科目</th><th>合計分</th></tr>
<%
Map<String, Map<String,Integer>> totals =
  (Map<String, Map<String,Integer>>)request.getAttribute("subjectTotals");

Map<String,Integer> childMap = (totals==null) ? null : totals.get("child");
if(childMap != null){
  for(String s : childMap.keySet()){
%>
<tr><td><%= s %></td><td><%= childMap.get(s) %></td></tr>
<%
  }
}
%>
</table>

<h3>科目別合計（adult）</h3>
<table border="1">
<tr><th>科目</th><th>合計分</th></tr>
<%
Map<String,Integer> adultMap = (totals==null) ? null : totals.get("adult");
if(adultMap != null){
  for(String s : adultMap.keySet()){
%>
<tr><td><%= s %></td><td><%= adultMap.get(s) %></td></tr>
<%
  }
}
%>
</table>

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

<script>
// ===== 検索フォーム側（既存） =====
function changeSubjectType(){
  let type = document.getElementById("subjectType").value;
  document.getElementById("childSubjects").style.display =
      type==="child" ? "block" : "none";
  document.getElementById("adultSubjects").style.display =
      type==="adult" ? "block" : "none";
}

function toggleOther(){
  let val = document.getElementById("childSelect").value;
  document.getElementById("childOther").style.display =
      val==="other" ? "inline" : "none";
}

// ★検索フォーム（GET）
document.querySelector("form[method='get']").addEventListener("submit", function(){
  let type = document.getElementById("subjectType").value;
  let subject = "";

  if(type === "child"){
    let val = document.getElementById("childSelect").value;
    if(val === "other"){
      subject = document.getElementById("childOther").value.trim();
    }else{
      subject = val;
    }
  }else{
    subject = document.getElementById("adultInput").value.trim();
  }

  document.getElementById("subjectHidden").value = subject;
});


// ===== 登録フォーム側（新規追加） =====
function regChangeSubjectType(){
  let type = document.getElementById("regSubjectType").value;
  document.getElementById("regChildSubjects").style.display =
      type==="child" ? "block" : "none";
  document.getElementById("regAdultSubjects").style.display =
      type==="adult" ? "block" : "none";
}

function regToggleOther(){
  let val = document.getElementById("regChildSelect").value;
  document.getElementById("regChildOther").style.display =
      val==="other" ? "inline" : "none";
}

//★登録フォーム（POST）
document.querySelector("form[method='post']").addEventListener("submit", function(e){

  // type選択が無い画面（child固定など）でも落ちないようにする
  const typeEl = document.getElementById("regSubjectType"); // adult画面だけ存在するかも
  const hiddenTypeEl = document.getElementById("regSubjectTypeHidden"); // これは常にフォーム内に置く

  // デフォルトは hidden の value（JSP側で child/adult を入れておく）
  let type = hiddenTypeEl ? hiddenTypeEl.value : "adult";

  // adult画面で typeセレクトがあるならそれを採用
  if(typeEl){
    type = typeEl.value;
  }

  let subject = "";

  if(type === "child"){
    const selectEl = document.getElementById("regChildSelect");
    const otherEl = document.getElementById("regChildOther");

    let val = selectEl ? selectEl.value : "";
    if(val === "other"){
      subject = otherEl ? otherEl.value.trim() : "";
    }else{
      subject = val;
    }
  }else{
    const adultEl = document.getElementById("regAdultInput");
    subject = adultEl ? adultEl.value.trim() : "";
  }

  if(!subject){
    alert("科目を入力してください");
    e.preventDefault();
    return;
  }

  // subject送信
  const subjectHidden = document.getElementById("regSubjectHidden");
  if(subjectHidden){
    subjectHidden.value = subject;
  }

  // ★type送信（これがNULL防止の要）
  if(hiddenTypeEl){
    hiddenTypeEl.value = type;
  }
});

// 初期状態（念のため）
if (typeof regChangeSubjectType === "function") regChangeSubjectType();
if (typeof changeSubjectType === "function") changeSubjectType();
</script>


</body>

</html>



