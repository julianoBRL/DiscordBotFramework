package io.github.julianobrl.discordbots.repositories;

import io.github.julianobrl.discordbots.entities.Plugin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PluginRepository extends JpaRepository<Plugin, Long>, JpaSpecificationExecutor<Plugin> {

    @Query("SELECT p FROM Plugin p JOIN p.versions v WHERE :botId MEMBER OF v.botsId")
    Optional<List<Plugin>> findInstalledPluginsByBotsId(String botId);

    Optional<Plugin> findByIdAndVersionsVersion(String id, String version);

    Optional<Plugin> findById(String id);
    void deleteById(String id);

}
