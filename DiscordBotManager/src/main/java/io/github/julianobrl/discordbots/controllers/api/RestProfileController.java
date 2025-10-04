package io.github.julianobrl.discordbots.controllers.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Profile Manager")
public class RestProfileController {

    @GetMapping
    protected String getIndex(){
        return "profile";
    }



}
