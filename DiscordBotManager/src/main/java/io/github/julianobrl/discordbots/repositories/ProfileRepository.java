package io.github.julianobrl.discordbots.repositories;

import io.github.julianobrl.discordbots.entities.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Version, Long>, JpaSpecificationExecutor<Version> {
}
