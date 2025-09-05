package io.github.julianobrl.discordbots.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/plugins")
public class PluginsController {

    @GetMapping
    protected String get(){
        return "plugins";
    }

    @GetMapping("/add")
    protected String getAdd(){
        return "adds/plugins";
    }

    @GetMapping("/view/{id}")
    protected String getView(){
        return "views/plugins";
    }

}
