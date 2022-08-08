package com.kolay.scriptrunner.service;

import com.kolay.scriptrunner.model.ScriptDetails;
import com.kolay.scriptrunner.model.ScriptStatus;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Runs script and stops it
 */
public class ScriptThread implements Runnable {

    private final ScriptDetails scriptDetails;
    ByteArrayOutputStream baosOut;
    Context cx;

    public ScriptThread(ScriptDetails scriptDetails) {
        this.scriptDetails = scriptDetails;
        baosOut = new ByteArrayOutputStream();
        cx = Context.newBuilder("js")
                .useSystemExit(true)
                .out(baosOut)
                .build();
    }

    /**
     * Runs script.
     * If script ends without error then sets its status as COMPLETED else sets status as FAILED.
     * Also, after scripts ending, sets its execution time and output or error message.
     */
    @Override
    public void run() {
        scriptDetails.setStatus(ScriptStatus.EXECUTING);
        long start = -System.currentTimeMillis();
        try {
            cx.eval("js", scriptDetails.getBody());
            scriptDetails.setExecutionTime(start + System.currentTimeMillis());
            scriptDetails.setOutput(new String(baosOut.toByteArray(), StandardCharsets.UTF_8));
            scriptDetails.setStatus(ScriptStatus.COMPLETED);
        } catch (PolyglotException e) {
            scriptDetails.setExecutionTime(start + System.currentTimeMillis());
            scriptDetails.setErrorMessage(e.getMessage());
            if (scriptDetails.getStatus() != ScriptStatus.STOPPED) {
                scriptDetails.setStatus(ScriptStatus.FAILED);
            }
        }
    }

    /**
     * Retrieves current output of still executing script.
     * @return
     */
    public String getOutput() {
        return new String(baosOut.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Stops executing script.
     * Sets script's status as STOPPED.
     * Sets as script's output what script has written before stopping.
     */
    public void stopScript() {

        scriptDetails.setErrorMessage("Thread was interrupted.");
        scriptDetails.setStatus(ScriptStatus.STOPPED);
        cx.close(true);
        scriptDetails.setOutput(new String(baosOut.toByteArray(), StandardCharsets.UTF_8));

    }
}
