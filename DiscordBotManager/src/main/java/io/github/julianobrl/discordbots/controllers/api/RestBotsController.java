package io.github.julianobrl.discordbots.controllers.api;

import io.github.julianobrl.discordbots.entities.Bot;
import io.github.julianobrl.discordbots.services.BotService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bots")
@Tag(name = "Bots", description = "Bots Manager")
public class RestBotsController {

    @Autowired
    private BotService service;

    @GetMapping("/list")
    public List<Bot> list(){
        return service.list();
    }

    @GetMapping("/{id}/view")
    public Bot view(@PathVariable(name = "id") String id){
        return service.getById(id);
    }

    @PostMapping("/add")
    public Bot create(@Valid @RequestBody Bot bot){
        return service.create(bot);
    }

    @PutMapping("/{id}/edit")
    public Bot edit(Bot object, String id) {
        return null;
    }

    @DeleteMapping("/{id}/delete")
    public void delete(@PathVariable(name = "id") String id) {
        service.delete(id);
    }

}
