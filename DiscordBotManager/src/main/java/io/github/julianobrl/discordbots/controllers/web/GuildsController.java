package io.github.julianobrl.discordbots.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guilds")
public class GuildsController {

    @GetMapping
    protected String get(){
        return "guilds";
    }

    @GetMapping("/view/{id}")
    protected String getView(){
        return "views/guilds";
    }

}
