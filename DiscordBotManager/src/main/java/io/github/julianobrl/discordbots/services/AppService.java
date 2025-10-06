package io.github.julianobrl.discordbots.services;

import io.github.julianobrl.discordbots.entities.App;
import io.github.julianobrl.discordbots.exceptions.AppException;
import io.github.julianobrl.discordbots.repositories.AppRepository;
import io.github.julianobrl.discordbots.services.interfaces.IService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppService implements IService<App> {

    private final AppRepository repository;

    @Override
    public List<App> list() {
        return repository.findAll();
    }

    @Override
    public List<App> search() {
        return List.of();
    }

    @Override
    public App create(App object) {
        return repository.save(object);
    }

    @Override
    public App getById(String id) {
        return repository.findById(Long.valueOf(id))
                .orElseThrow(()-> new AppException("App not found!", HttpStatus.NOT_FOUND));
    }

    @Override
    public App update(String id, App updated) {
        return null;
    }

    @Override
    public void delete(String id) {
        repository.deleteById(Long.valueOf(id));
    }

}
