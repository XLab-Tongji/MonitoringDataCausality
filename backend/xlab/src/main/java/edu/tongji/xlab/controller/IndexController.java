package edu.tongji.xlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @CrossOrigin("*")
    @GetMapping(value = "/")
    public String index() {
        return "display";
    }
}
