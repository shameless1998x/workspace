package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user") // 定义这个控制器处理 /users 路径下的请求
public class UserController {
    @Autowired
    private UserRepository userRepository;

    //1.增：添加一个用户
    @GetMapping("/add")
    public String addUser(@RequestParam String name, @RequestParam String email){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
        return "用户"+ name +" 已存入数据库！";
    }

    //2.查：查看所有用户
    @GetMapping("/all")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
