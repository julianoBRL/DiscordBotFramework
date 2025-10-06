package io.github.julianobrl.discordbots.entities.enums;

public enum ProfileRoles {
    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    USER("USER"),
    VIEWER("VIEWER"),
    INTERNAL("INTERNAL");

    final String value;

    ProfileRoles(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
