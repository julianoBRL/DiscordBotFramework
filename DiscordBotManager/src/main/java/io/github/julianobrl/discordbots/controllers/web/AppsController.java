package io.github.julianobrl.discordbots.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/apps")
public class AppsController {

    @GetMapping
    protected String get(){
        return "apps";
    }

    @GetMapping("/add")
    protected String getAdd(){
        return "adds/apps";
    }

    @GetMapping("/view/{id}")
    protected String getView(){
        return "views/apps";
    }

}
