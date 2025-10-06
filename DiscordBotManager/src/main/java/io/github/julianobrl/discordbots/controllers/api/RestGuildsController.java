package io.github.julianobrl.discordbots.controllers.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guilds")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Guilds", description = "Guilds Manager")
public class RestGuildsController {

    @GetMapping
    protected String get(){
        return "guilds";
    }

    @GetMapping("/{id}/view")
    protected String getView(){
        return "views/guilds";
    }

}
