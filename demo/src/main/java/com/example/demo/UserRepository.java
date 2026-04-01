package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 继承 JpaRepository 后，你就自动拥有了 save(), findAll(), deleteById() 等所有功能！
}