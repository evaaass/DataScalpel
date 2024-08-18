package cn.superhuang.data.scalpel.admin.web.resource.impl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloResource {

    @GetMapping("/hello")
    public String getAllTasks() {
        return "Hello World!";
    }
}
