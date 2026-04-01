package com.example.demo;

import jakarta.persistence.*;

@Entity // 告诉 JPA 这是一个数据库实体类
@Table(name = "users") // 映射到数据库中名为 users 的表
public class User {

    @Id // 声明为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键自增
    private Long id;

    private String name;

    private String email;

    // --- 下面需要生成 Getter 和 Setter 方法 ---
    // 小技巧：在 IDEA 中按 Alt + Insert 选择 Getter and Setter 即可自动生成


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}