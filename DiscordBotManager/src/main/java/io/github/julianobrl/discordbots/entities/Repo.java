package io.github.julianobrl.discordbots.entities;

import lombok.Data;

import java.util.List;

@Data
public class Repo {
    private String id;
    private String name;
    private String description;
    private String owner;
    private String url;
    private String logo;
    private List<Version> versions;
}
