package org.test.task.service;

import org.test.task.dto.TimeEntryDto;

import java.util.List;

public interface TimeEntryService {
    void addTimeEntry();
    List<TimeEntryDto> findAllEntries();
    void processQueue();
}
