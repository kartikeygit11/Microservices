package com.learningmicroservice.section1.controller;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountsController {
@GetMapping("hi")

    public String sayHello(){

    return "hello";
}
}
