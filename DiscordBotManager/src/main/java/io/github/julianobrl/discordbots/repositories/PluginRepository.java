package io.github.julianobrl.discordbots.repositories;

import io.github.julianobrl.discordbots.entities.Plugin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PluginRepository extends JpaRepository<Plugin, Long>, JpaSpecificationExecutor<Plugin> {
    Optional<Plugin> findByIdAndVersionsVersion(String id, String version);
    Optional<Plugin> findById(String id);
    void deleteById(String id);
}
