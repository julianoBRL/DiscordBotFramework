package io.github.julianobrl.discordbots.entities.enums;

public enum BotStatus {
    STARTING("STARTING"),
    STOPPING("STARTING"),
    ACTIVE("STARTING"),
    INACTIVE("STARTING"),
    ERROR("STARTING");

    final String value;

    BotStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static BotStatus fromDockerState(String dockerState) {
        return switch (dockerState.toLowerCase()) {
            case "created", "restarting" -> STARTING;
            case "running" -> ACTIVE;
            case "paused", "exited" -> INACTIVE;
            case "dead" -> ERROR;
            default -> ERROR; // Estado desconhecido tratado como erro
        };
    }

}
