package io.github.julianobrl.discordbots.controllers.web;

import io.github.julianobrl.discordbots.services.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bots")
public class BotsController {

    @Autowired
    private BotService service;

    @GetMapping
    protected String get(){
        return "bots";
    }

    @GetMapping("/add")
    protected String getAdd(){
        return "adds/bots";
    }

    @GetMapping("/view/{id}")
    protected String getView(){
        return "views/bots";
    }

}
