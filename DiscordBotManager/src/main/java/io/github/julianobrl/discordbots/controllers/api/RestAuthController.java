package io.github.julianobrl.discordbots.controllers.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication")
public class RestAuthController {

    @GetMapping("/login")
    protected String getIndex(){
        return "login";
    }

}
