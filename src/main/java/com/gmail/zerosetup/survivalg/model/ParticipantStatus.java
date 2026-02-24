package com.gmail.zerosetup.survivalg.model;

public enum ParticipantStatus {
    IN_SHELTER("В убежище"),
    OUTSIDE_SHELTER("Вне убежища"),
    ELIMINATED("Погиб");

    private final String displayName;

    ParticipantStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

