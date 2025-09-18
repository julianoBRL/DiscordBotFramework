package io.github.julianobrl.discordbots.entities;

import lombok.Data;

import java.util.Date;

@Data
public class Version {
    private String version;
    private String changelog;
    private String sourceUrl;
    private String checksum;
    private Date timestamp;
}
