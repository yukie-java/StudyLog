# StudyLog

## 概要
学習時間を記録・管理できるWebアプリです。  
ログイン後、自分の学習ログ（科目・学習時間・メモ）を登録・編集・削除できます。  
Java（Servlet/JSP）とH2 Databaseを使用したポートフォリオ用プロジェクトです。

---

## 使用技術
- Java
- Servlet / JSP
- JDBC
- H2 Database
- Apache Tomcat
- Eclipse
- Git / GitHub

---

## 主な機能
- ログイン / ログアウト
- 学習ログ登録
- 一覧表示（ユーザー別）
- 編集
- 削除（確認ダイアログあり）
- フラッシュメッセージ表示  
  （登録しました / 更新しました / 削除しました）

### 検索機能
- 日付範囲（from〜to）
- 科目（部分一致）

### 科目入力機能
- ユーザー種別（child / adult）に応じた入力UI
- child：五科目＋その他入力
- adult：自由入力＋過去履歴候補表示（datalist）

### 集計機能
- 当日の学習時間合計表示
- 科目別学習時間合計（child / adult別）

---

## DBテーブル（study_logs）

| カラム名 | 型 | 説明 |
|---|---|---|
| id | INTEGER | 主キー |
| user_id | VARCHAR | ユーザーID |
| study_date | VARCHAR / DATE | 学習日 |
| subject_type | VARCHAR | 科目種別（child / adult） |
| subject | VARCHAR | 科目 |
| minutes | INTEGER | 学習時間（分） |
| memo | VARCHAR | メモ |
| created_at | TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | 更新日時 |

---

## 画面

- login.jsp（ログイン画面）
- studylog.jsp（一覧＋登録＋検索＋集計）
- edit.jsp（編集画面）

（スクリーンショットをここに追加予定）

---

## 起動方法

1. EclipseでプロジェクトをTomcatにデプロイ  
2. ブラウザで以下へアクセス  
 http://localhost:8080/StudyLog/LoginServlet
 


---

## 今後の改善予定

- UI改善（CSS整理）
- 入力バリデーション強化
- グラフ表示（学習時間の可視化）
- REST API化（Androidアプリから利用）

---

## 作者

ポートフォリオ用に作成した学習記録アプリです。
