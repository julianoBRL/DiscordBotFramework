package io.github.julianobrl.discordbots.controllers.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apps")
@Tag(name = "Apps", description = "Server Apps")
public class RestAppsController {

    @GetMapping
    protected String get(){
        return "apps";
    }

    @GetMapping("/add")
    protected String getAdd(){
        return "adds/apps";
    }

    @GetMapping("/{id}/view")
    protected String getView(){
        return "views/apps";
    }

}
