package io.github.julianobrl.discordbots.controllers.api;

import io.github.julianobrl.discordbots.entities.App;
import io.github.julianobrl.discordbots.services.AppService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apps")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Apps", description = "Server Apps")
public class RestAppsController {

    private final AppService service;

    @GetMapping
    protected List<App> get(){
        return service.list();
    }

    @PostMapping
    protected App add(@RequestBody App app){
        return service.create(app);
    }

    @GetMapping("/{id}")
    protected App view(@PathVariable(name = "id") String id){
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    protected void delete(@PathVariable(name = "id") String id){
        service.delete(id);
    }

}
