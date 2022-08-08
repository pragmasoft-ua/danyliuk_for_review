package com.kolay.scriptrunner.model;

import java.time.LocalDateTime;

/**
 * Represents DTO for most important script info
 */
public class ScriptSummaryDTO {

    private long id;
    private ScriptStatus status;
    private LocalDateTime scheduledTime;
    private long executionTime;

    public ScriptSummaryDTO() {
    }

    public ScriptSummaryDTO(long id, ScriptStatus status, LocalDateTime scheduledTime, long executionTime) {
        this.id = id;
        this.status = status;
        this.scheduledTime = scheduledTime;
        this.executionTime = executionTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ScriptStatus getStatus() {
        return status;
    }

    public void setStatus(ScriptStatus status) {
        this.status = status;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
