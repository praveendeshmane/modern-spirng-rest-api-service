package com.example.modernrest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;

@Controller
public class ViewController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("serviceName", "Modern REST Service");
        model.addAttribute("now", Instant.now().toString());
        return "index";
    }
}
