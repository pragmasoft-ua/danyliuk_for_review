package com.kolay.scriptrunner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kolay.scriptrunner.service.ScriptThread;

import java.time.LocalDateTime;

/**
 * Represents Detailed script info
 */
public class ScriptDetails {

    private final long id;
    private volatile ScriptStatus status;
    private final LocalDateTime scheduledTime;
    private long executionTime;
    private final String body;
    private String output;
    private String errorMessage;
    @JsonIgnore
    private ScriptThread scriptThread;
    @JsonIgnore
    private boolean blocking;

    public ScriptDetails(long id, String body, LocalDateTime scheduledTime) {
        this.id = id;
        this.body = body;
        this.scheduledTime = scheduledTime;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public ScriptStatus getStatus() {
        return status;
    }

    public void setStatus(ScriptStatus status) {
        this.status = status;
    }

    public ScriptThread getScriptThread() {
        return scriptThread;
    }

    public void setScriptThread(ScriptThread scriptThread) {
        this.scriptThread = scriptThread;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }
}
