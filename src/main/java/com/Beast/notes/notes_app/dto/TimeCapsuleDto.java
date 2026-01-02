package com.Beast.notes.notes_app.dto;

import java.time.LocalDateTime;

public class TimeCapsuleDto {
    private String timeSpan; // "12_HOURS", "1_DAY", "3_DAYS", "7_DAYS", "CUSTOM"
    private LocalDateTime customUnlockTime; // For custom datetime

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

    public LocalDateTime getCustomUnlockTime() {
        return customUnlockTime;
    }

    public void setCustomUnlockTime(LocalDateTime customUnlockTime) {
        this.customUnlockTime = customUnlockTime;
    }
}
