package com.security.controller;

import com.security.model.Employee;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class XSSFilterController {

    @GetMapping("/hello/{message}")
    public String show(@PathVariable String message){
        return message;

    }

    @GetMapping("/welcome")
    public String welcome(@RequestParam String message,@RequestParam String message1){
        return message;

    }

    @PostMapping("/test")
    public String test(@Valid @RequestBody Employee employee){
        return "Post call sucessfuly";

    }
}
