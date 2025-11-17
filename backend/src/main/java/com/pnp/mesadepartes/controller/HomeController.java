package com.pnp.mesadepartes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/pages/auth/login.html";
    }
    
    @GetMapping("/login")
    public String login() {
        return "redirect:/pages/auth/login.html";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/pages/common/dashboard.html";
    }
}
