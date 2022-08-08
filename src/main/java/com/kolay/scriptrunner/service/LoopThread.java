package com.kolay.scriptrunner.service;

import com.kolay.scriptrunner.model.ScriptDetails;
import com.kolay.scriptrunner.model.ScriptStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread to maintain queue of scripts.
 */
@Component
@Scope("application")
public class LoopThread implements Runnable {

    /**
     * Queue of scripts that came for execution.
     */
    ConcurrentLinkedQueue<ScriptDetails> scripts = new ConcurrentLinkedQueue<>();


    /**
     * Checks if any scripts placed in queue.
     * If yes, then pick up one script and starts new {@link ScriptThread} to execute script.
     * If script is blocking, then waits while it ends before checking next script.
     */
    @Override
    public void run() {

        while (true) {

            ScriptDetails script = scripts.poll();
            if (script != null) {
                ScriptThread scriptThread = new ScriptThread(script);
                script.setScriptThread(scriptThread);
                Thread thread = new Thread(scriptThread);
                thread.start();
                if (script.isBlocking()) {
                    while (true) {
                        if (script.getStatus() == ScriptStatus.STOPPED || script.getStatus() == ScriptStatus.COMPLETED ||
                            script.getStatus() == ScriptStatus.FAILED) break;
                    }
                }
            }

        }

    }

    /**
     * Adds script to queue.
     * @param scriptDetails
     */
    public void addScriptToQueue(ScriptDetails scriptDetails) {
        scripts.add(scriptDetails);
    }
}
