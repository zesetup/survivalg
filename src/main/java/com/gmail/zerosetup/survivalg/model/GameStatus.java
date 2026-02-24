package com.gmail.zerosetup.survivalg.model;

public enum GameStatus {
    NEW("Новая"),
    OPEN_FOR_REGISTRATION("Открыта регистрация"),
    ACTIVE("Активна"),
    COMPLETED("Завершена");

    private final String displayName;

    GameStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
