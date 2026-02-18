package model;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;      // ログインID相当（y/a など）
    private String role;      // adult/child など
    private String password;  // DB取得用（セッションには基本持たせない運用でもOK）

    public User() {}

    public User(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
