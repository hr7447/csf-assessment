package vttp.batch5.csf.assessment.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = {"", "/", "/confirmation", "/place-order"})
    public String index() {
        return "forward:/index.html";
    }
} 