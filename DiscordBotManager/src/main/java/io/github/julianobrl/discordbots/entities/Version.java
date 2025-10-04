package io.github.julianobrl.discordbots.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Version extends BaseEntity{

    @Column(unique = true)
    private String version;
    private String changelog;

    @Column(unique = true)
    private String sourceUrl;

    @Column(unique = true)
    private String checksum;
    private Date timestamp;
    private List<String> botsId = new ArrayList<>();

}
