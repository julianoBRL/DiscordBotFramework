package io.github.julianobrl.discordbots.controllers.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.julianobrl.discordbots.entities.Plugin;
import io.github.julianobrl.discordbots.services.PluginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/plugins")
@Tag(name = "Plugins", description = "Plugins Manager")
public class RestPluginsController {

    @Autowired
    private PluginService pluginService;

    @GetMapping("/list")
    protected List<Plugin> get(){
        return pluginService.readPlugins();
    }

    @PostMapping("/add")
    protected Plugin addPluginRepo(@RequestParam(name = "repoUrl") String repoUrl) throws JsonProcessingException {
        return pluginService.add(repoUrl);
    }

    @PostMapping("/install")
    protected Plugin installPlugin(@RequestParam(name = "pluginId") String pluginId,
                                                   @RequestParam(name = "botId") String botId,
                                                   @RequestParam(name = "version") String version) throws JsonProcessingException {
        return pluginService.install(pluginId, botId, version);
    }

    @DeleteMapping("/uninstall")
    protected Plugin uninstallPlugin(@RequestParam(name = "pluginId") String pluginId,
                                                   @RequestParam(name = "botId") String botId) {
        return pluginService.uninstall(pluginId, botId);
    }

    @GetMapping("/{id}/view")
    protected Plugin getView(@PathVariable(name = "id") String id){
        return pluginService.getById(id);
    }

    @DeleteMapping("/{id}/delete")
    protected Plugin delete(@PathVariable(name = "id") String id){
        return pluginService.delete(id);
    }

}
