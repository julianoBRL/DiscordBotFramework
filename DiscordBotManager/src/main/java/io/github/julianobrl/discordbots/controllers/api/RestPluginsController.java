package io.github.julianobrl.discordbots.controllers.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/plugins")
@Tag(name = "Plugins", description = "Plugins Manager")
public class RestPluginsController {

    @GetMapping
    protected String get(){
        return "plugins";
    }

    @GetMapping("/add")
    protected String getAdd(@RequestParam(name = "botId") String botId, MultipartFile plugin){
        return "adds/plugins";
    }

    @GetMapping("/{id}/view")
    protected String getView(@PathVariable(name = "id") String id){
        return "views/plugins";
    }

}
