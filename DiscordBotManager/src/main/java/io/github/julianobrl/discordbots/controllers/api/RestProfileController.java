package io.github.julianobrl.discordbots.controllers.api;

import io.github.julianobrl.discordbots.entities.Profile;
import io.github.julianobrl.discordbots.services.ProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile", description = "Profile Manager")
public class RestProfileController {

    private final ProfileService service;

    @GetMapping("/list")
    protected List<Profile> list(){
        return service.list();
    }

    @GetMapping
    protected Profile getById(Authentication authentication){
        Profile profile = (Profile) authentication.getPrincipal();
        return service.getById(String.valueOf(profile.getId()));
    }

    @GetMapping("/{id}")
    protected Profile getOtherById(@PathVariable(name = "id") String id){
        return service.getById(id);
    }

    @PostMapping()
    protected Profile create(@RequestBody Profile profile){
        return service.create(profile);
    }

    @DeleteMapping("/{id}")
    protected void delete(@PathVariable(name = "id") String id){
        service.delete(id);
    }


}
