package org.test.task.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.task.dto.TimeEntryDto;
import org.test.task.service.TimeEntryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimeEntryController {
    private final TimeEntryService timeEntryService;

    @GetMapping("/times")
    public List<TimeEntryDto> findAllEntries() {
        return timeEntryService.findAllEntries();
    }
}
