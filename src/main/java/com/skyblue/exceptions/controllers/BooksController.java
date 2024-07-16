package com.skyblue.exceptions.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BooksController {
    @GetMapping("/list")
    public String getBooks() {
        return "All books";
    }

}
