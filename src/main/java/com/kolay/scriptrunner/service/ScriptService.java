package com.kolay.scriptrunner.service;

import com.kolay.scriptrunner.model.ScriptDetails;
import com.kolay.scriptrunner.model.ScriptStatus;
import com.kolay.scriptrunner.model.ScriptSummaryDTO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class of application
 */
@Service
@Scope("application")
public class ScriptService {

    /**
     * Thread to maintain queue of scripts.
     */
    LoopThread loopThread;
    /**
     * Stores scripts that were added to application.
     */
    private List<ScriptDetails> scriptsList = new CopyOnWriteArrayList<>();

    /**
     * ID of last added script.
     */
    private final AtomicLong counter = new AtomicLong();

    public ScriptService(LoopThread loopThread) {
        this.loopThread = loopThread;
        Thread thread = new Thread(this.loopThread);
        thread.start();
    }

    /**
     * Creates new script.
     * Sets script's ID, scheduled time, status as QUEUED.
     * Adds script to queue for execution and list for storing.
     * If script is blocking, wait for end of it execution and return script detailed info.
     * @param body script code
     * @param blocking if <strong>true</strong>, method waits for ends of script's execution before returning scripts info.
     * @return script's detailed info
     */
    public ScriptDetails addScript(String body, boolean blocking) {
        ScriptDetails scriptDetails = new ScriptDetails(counter.incrementAndGet(), body, LocalDateTime.now());
        scriptsList.add(scriptDetails);
        scriptDetails.setStatus(ScriptStatus.QUEUED);
        scriptDetails.setOutput("");
        scriptDetails.setBlocking(blocking);

        loopThread.addScriptToQueue(scriptDetails);

        if (blocking) {
            while (true) {
                if (scriptDetails.getStatus() == ScriptStatus.STOPPED || scriptDetails.getStatus() == ScriptStatus.COMPLETED ||
                        scriptDetails.getStatus() == ScriptStatus.FAILED) break;
            }
        }

        return scriptDetails;
    }

    /**
     * Returns script details by its ID.
     * If script is still executing, previously retrieve its current output.
     * @param id
     * @return script's detailed info
     */
    public ScriptDetails findById(long id) {
        ScriptDetails scriptDetails = getScriptDetailsById(id);
        if (scriptDetails != null && scriptDetails.getStatus() == ScriptStatus.EXECUTING) {
            scriptDetails.setOutput(scriptDetails.getScriptThread().getOutput());
        }
        return scriptDetails;
    }

    public int getScriptsListSize() {
        return scriptsList.size();
    }

    /**
     * Returns list of scripts that were added in application.
     * If <strong>status</strong> parameter is null then returns all scripts, else returns only scripts with given status.
     * If <strong>sort</strong> parameter is equal <strong>id</strong> or <strong>time</strong> then orders the list by given field in descending order.
     * Returns not all detailed script info, but only most important {@link ScriptSummaryDTO}
     * @param status for filtering scripts. Possible values: "executing", "completed", "failed", "stopped"
     * @param sort for sorting scripts in descending order. Possible values: "id", "time"
     * @return
     */
    public List<ScriptSummaryDTO> getScriptsList(String status, String sort) {
        Stream<ScriptDetails> stream = scriptsList.stream()
                .filter(s -> status == null || s.getStatus().toString().equalsIgnoreCase(status));
        if (sort != null && sort.equalsIgnoreCase("id")) {
            stream = stream
                    .sorted(Comparator.comparingLong(ScriptDetails::getId)
                            .reversed());
        } else if (sort != null && sort.equalsIgnoreCase("time")) {
            stream = stream
                    .sorted(Comparator.comparing(ScriptDetails::getScheduledTime)
                            .reversed());
        }
        return stream
                .map(details -> mapToSummaryDTO(details, new ScriptSummaryDTO()))
                .collect(Collectors.toList());
    }

    /**
     * Forcibly stops executing script and returns its detailed info.
     * If script not found or not executing then returns null.
     * @param id the id of script to stop
     * @return
     */
    public ScriptDetails stopScript(long id) {
        ScriptDetails scriptDetails = getScriptDetailsById(id);
        if (scriptDetails == null) {
            return null;
        }
        if (scriptDetails.getStatus() != ScriptStatus.EXECUTING) {
            return null;
        }
        scriptDetails.getScriptThread().stopScript();
        return scriptDetails;
    }

    /**
     * Removes inactive script by id and returns int of http status code:
     * 200 - if script was removed,
     * 404 - if script not found by id,
     * 400 - if script is still executing and couldn't be removed.
     * @param id the id of script to remove
     * @return
     */
    public int deleteById(long id) {
        ScriptDetails scriptDetails = getScriptDetailsById(id);
        if (scriptDetails == null) {
            return 404;
        }
        if (scriptDetails.getStatus() == ScriptStatus.EXECUTING || scriptDetails.getStatus() == ScriptStatus.QUEUED) {
            return 400;
        }
        scriptsList.remove(scriptDetails);
        return 200;
    }

    private ScriptDetails getScriptDetailsById(long id) {
        return scriptsList.stream()
                .filter(sd -> sd.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private ScriptSummaryDTO mapToSummaryDTO(ScriptDetails scriptDetails, ScriptSummaryDTO scriptSummaryDTO) {
        scriptSummaryDTO.setId(scriptDetails.getId());
        scriptSummaryDTO.setStatus(scriptDetails.getStatus());
        scriptSummaryDTO.setScheduledTime(scriptDetails.getScheduledTime());
        scriptSummaryDTO.setExecutionTime(scriptDetails.getExecutionTime());
        return scriptSummaryDTO;
    }

}
