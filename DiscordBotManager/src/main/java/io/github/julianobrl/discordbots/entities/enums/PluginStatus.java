package io.github.julianobrl.discordbots.entities.enums;

public enum PluginStatus {
    INSTALLED("INSTALLED"),
    DISABLED("DISABLED"),
    UPDATE_AVAILABLE("UPDATE_AVAILABLE"),
    DELETED("DELETED"),
    AVAILABLE("AVAILABLE");

    final String value;

    PluginStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }


}
