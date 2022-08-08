package com.kolay.scriptrunner;

import com.kolay.scriptrunner.model.ScriptDetails;
import com.kolay.scriptrunner.model.ScriptStatus;
import com.kolay.scriptrunner.model.ScriptSummaryDTO;
import com.kolay.scriptrunner.service.ScriptService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ScriptServiceTest {

    @InjectMocks
    private ScriptService scriptService;

    @Before
    public void setup() {
        List<ScriptDetails> list = List.of(new ScriptDetails(1L, "Some script", LocalDateTime.now()),
                new ScriptDetails(2L, "Some script", LocalDateTime.now()),
                new ScriptDetails(3L, "Some script", LocalDateTime.now()));
        ReflectionTestUtils.setField(scriptService, "scriptsList", list);
        ReflectionTestUtils.setField(scriptService, "counter", new AtomicLong(3));
        scriptService.findById(1L).setStatus(ScriptStatus.COMPLETED);
        scriptService.findById(2L).setStatus(ScriptStatus.EXECUTING);
        scriptService.findById(3L).setStatus(ScriptStatus.FAILED);
    }

    @Test
    public void returnScriptDetails_whenFindById() {
        ScriptDetails sd = scriptService.findById(3L);
        assertThat(sd).isNotNull();
        assertThat(sd.getId()).isEqualTo(3L);
    }

    @Test
    public void returnNull_whenFindByNotExistingId() {
        ScriptDetails sd = scriptService.findById(5L);
        assertThat(sd).isNull();
    }

    @Test
    public void returnList_whenGetScriptsList() {
        List<ScriptSummaryDTO> list = scriptService.getScriptsList(null, null);
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    public void returnFilteredList_whenGetListWithFilter() {
        List<ScriptSummaryDTO> list = scriptService.getScriptsList("completed", null);
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getId()).isEqualTo(1);
    }

    @Test
    public void returnEmptyList_whenGetListWithNotExistingFilter() {
        List<ScriptSummaryDTO> list = scriptService.getScriptsList("stopped", null);
        assertThat(list).isEmpty();
    }

    @Test
    public void returnSortedList_whenGetListWithSortingById() {
        List<ScriptSummaryDTO> list = scriptService.getScriptsList(null, "id");
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0).getId()).isEqualTo(3L);
    }

}
