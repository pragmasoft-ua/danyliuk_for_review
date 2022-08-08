package com.kolay.scriptrunner;

import com.kolay.scriptrunner.model.ScriptDetails;
import com.kolay.scriptrunner.model.ScriptStatus;
import com.kolay.scriptrunner.model.ScriptSummaryDTO;
import com.kolay.scriptrunner.service.ScriptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles requests to API
 */
@RestController
public class ScriptController {

    ScriptService scriptService;

    public ScriptController(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    /**
     * Adds new script. Takes script code from plaintext request body.
     * Returns script's id in Location header.
     * @param body script code
     * @param blocking if equals <strong>true</strong>, <strong>yes</strong> or <strong>1</strong>,
     *                 then execution of next scripts will be blocked until this script ends it work.
     *                 Also, in this case script's output returns in response body.
     * @return
     */
    @PostMapping(value = "/script", consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<String> addScript(@RequestBody String body,
                                            @RequestParam(value="blocking", required = false) boolean blocking) {
        ScriptDetails scriptDetails = scriptService.addScript(body, blocking);
        String scriptOutput = scriptDetails.getOutput();
        if (scriptDetails.getStatus() == ScriptStatus.FAILED || scriptDetails.getStatus() == ScriptStatus.STOPPED) {
            scriptOutput = scriptDetails.getErrorMessage();
        }
        return ResponseEntity.ok()
                .header("Location", "/script/" + scriptDetails.getId())
                .body(scriptOutput);
    }

    /**
     * Returns JSON with detailed script's info by its id.
     * @param id
     * @return
     */
    @GetMapping(value = "/script/{id}")
    public ResponseEntity<ScriptDetails> findById(@PathVariable long id) {
        ScriptDetails scriptDetails = scriptService.findById(id);
        if (scriptDetails == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(scriptDetails);
    }

    /**
     * Returns array of previously added scripts info in JSON format.
     * @param status if present, scripts will be filtered by given status.
     *               Possible values: "queued", "executing", "completed", "failed", "stopped"
     * @param sort if present, scripts will be sorted by given field in descending order. Possible values: "id", "time"
     * @return
     */
    @GetMapping("/script")
    public List<ScriptSummaryDTO> getScriptsList(@RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String sort) {
        return scriptService.getScriptsList(status, sort);
    }

    /**
     * Forcibly stops executing script.
     * If script was stopped successfully then returns its detailed info in body.
     * @param id the id of script to stop
     * @return
     */
    @PostMapping(value = "/stop/{id}")
    public ResponseEntity<ScriptDetails> stopById(@PathVariable long id) {
        return ResponseEntity.ok().body(scriptService.stopScript(id));
    }

    /**
     * Removes inactive script by id and returns http status code:
     * 200 - if script was removed,
     * 404 - if script not found by id,
     * 400 - if script is still executing and couldn't be removed.
     * @param id the id of script to remove
     * @return
     */
    @DeleteMapping(value = "/script/{id}")
    public ResponseEntity<String> deleteById(@PathVariable long id) {
        int result = scriptService.deleteById(id);
        if (result == 400) {
            return new ResponseEntity<>("The script is still executing", HttpStatus.BAD_REQUEST);
        }
        if (result == 404) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

}
