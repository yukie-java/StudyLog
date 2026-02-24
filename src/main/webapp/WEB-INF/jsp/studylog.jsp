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

String viewType = (String)request.getAttribute("viewSubjectType");
if(viewType == null) viewType = "adult";
%>

<h2>StudyLog</h2>

<p>
  ようこそ ${loginUser.name} さん |
  <a href="${pageContext.request.contextPath}/LogoutServlet">ログアウト</a>
</p>

<h3>今日（<%= request.getAttribute("today") %>）の合計：<%= request.getAttribute("totalToday") %> 分</h3>

<h3>集計（<%= viewType %>）</h3>

<%-- adultログインだけ表示切替 --%>
<% if(!"child".equals(loginType)) { %>
  <form method="get" action="<%= request.getContextPath() %>/StudyLogServlet">
    <select name="subjectType">
      <option value="adult" <%= "adult".equals(viewType) ? "selected" : "" %>>adult</option>
      <option value="child" <%= "child".equals(viewType) ? "selected" : "" %>>child</option>
    </select>
    <input type="submit" value="表示切替">
  </form>
<% } %>

<p>週合計：<%= request.getAttribute("weekTotal") %> 分</p>
<p>月合計：<%= request.getAttribute("monthTotal") %> 分</p>
<p>累計：<%= request.getAttribute("grandTotal") %> 分</p>


<h3>ログ登録</h3>
<form id="regForm" action="<%= request.getContextPath() %>/StudyLogServlet" method="post">

  日付：<input type="date" name="studyDate" required><br>

  科目種別：
  <% if("child".equals(loginType)) { %>
    <!-- childログインは固定 -->
    <span>child（固定）</span>
    <input type="hidden" name="subjectType" id="regSubjectTypeHidden" value="child">
  <% } else { %>
    <!-- adultログインは切替可 -->
    <select id="regSubjectType" onchange="regChangeSubjectType()">
      <option value="adult" <%= "adult".equals(viewType) ? "selected" : "" %>>adult</option>
      <option value="child" <%= "child".equals(viewType) ? "selected" : "" %>>child</option>
    </select>
    <input type="hidden" name="subjectType" id="regSubjectTypeHidden" value="<%= viewType %>">
  <% } %>

  <br>

  <%-- ★ここから科目入力（フォーム内に置くのが超重要） --%>
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

  <br>
  分：<input type="number" name="minutes" min="1" required><br>
  メモ：<input type="text" name="memo"><br>
  <input type="submit" value="登録">
</form>

<%
String flash = (String)request.getAttribute("flash");
if(flash != null){
%>
  <div class="flash"><%= flash %></div>
<%
}
%>

<%-- datalist（登録フォームでも検索フォームでも使う） --%>
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


<h3>検索</h3>

<form id="searchForm" action="<%= request.getContextPath() %>/StudyLogServlet" method="get">

  <%-- ★検索も「表示中のtype」で絞る：viewTypeを送る --%>
  <input type="hidden" name="subjectType" id="subjectTypeHidden" value="<%= viewType %>">

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
  <% } else { %>
    <!-- adult -->
    <input list="subjectHistory" id="adultInput" placeholder="科目入力">
  <% } %>

  <input type="hidden" name="subject" id="subjectHidden">

  <br>
  <input type="submit" value="検索">
  <a href="<%= request.getContextPath() %>/StudyLogServlet">リセット</a>
</form>


<h3>科目別合計</h3>

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
// ===== 検索フォーム =====
function toggleOther(){
  const sel = document.getElementById("childSelect");
  const other = document.getElementById("childOther");
  if(!sel || !other) return;
  other.style.display = (sel.value === "other") ? "inline" : "none";
}

const searchForm = document.getElementById("searchForm");
if (searchForm) {
  searchForm.addEventListener("submit", function () {
    let subject = "";

    const childSelect = document.getElementById("childSelect");
    const childOther  = document.getElementById("childOther");
    const adultInput  = document.getElementById("adultInput");

    if (childSelect) {
      const val = childSelect.value;
      subject = (val === "other") ? (childOther ? childOther.value.trim() : "") : val;
    } else if (adultInput) {
      subject = adultInput.value.trim();
    }

    const subjectHidden = document.getElementById("subjectHidden");
    if (subjectHidden) subjectHidden.value = subject;
  });
}


// ===== 登録フォーム =====
function regChangeSubjectType(){
  const typeEl = document.getElementById("regSubjectType");             // adultログイン時だけ
  const hiddenTypeEl = document.getElementById("regSubjectTypeHidden"); // 常にある

  const type = typeEl ? typeEl.value : (hiddenTypeEl ? hiddenTypeEl.value : "adult");

  const childBox = document.getElementById("regChildSubjects");
  const adultBox = document.getElementById("regAdultSubjects");

  if(childBox) childBox.style.display = (type === "child") ? "block" : "none";
  if(adultBox) adultBox.style.display = (type === "adult") ? "block" : "none";

  // ★hiddenも追従させる（select変更したらサーバに送るtypeも変える）
  if(hiddenTypeEl) hiddenTypeEl.value = type;
}

function regToggleOther(){
  const sel = document.getElementById("regChildSelect");
  const other = document.getElementById("regChildOther");
  if(!sel || !other) return;
  other.style.display = (sel.value === "other") ? "inline" : "none";
}

const regForm = document.getElementById("regForm");
if(regForm){
  regForm.addEventListener("submit", function(e){
    const typeEl = document.getElementById("regSubjectType");
    const hiddenTypeEl = document.getElementById("regSubjectTypeHidden");

    let type = hiddenTypeEl ? hiddenTypeEl.value : "adult";
    if(typeEl) type = typeEl.value;

    let subject = "";
    if(type === "child"){
      const selectEl = document.getElementById("regChildSelect");
      const otherEl  = document.getElementById("regChildOther");
      const val = selectEl ? selectEl.value : "";
      subject = (val === "other") ? (otherEl ? otherEl.value.trim() : "") : val;
    }else{
      const adultEl = document.getElementById("regAdultInput");
      subject = adultEl ? adultEl.value.trim() : "";
    }

    if(!subject){
      alert("科目を入力してください");
      e.preventDefault();
      return;
    }

    const subjectHidden = document.getElementById("regSubjectHidden");
    if(subjectHidden) subjectHidden.value = subject;

    if(hiddenTypeEl) hiddenTypeEl.value = type;
  });
}

// 初期表示
regChangeSubjectType();
</script>

</body>
</html>