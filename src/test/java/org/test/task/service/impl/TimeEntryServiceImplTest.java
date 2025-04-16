package org.test.task.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test.task.dto.TimeEntryDto;
import org.test.task.entity.TimeEntry;
import org.test.task.repository.TimeEntryRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceImplTest {

    @Mock
    private TimeEntryRepository repository;

    @InjectMocks
    private TimeEntryServiceImpl service;

    @Test
    void addTimeEntry_ShouldAddEntryWithCurrentTimestampToQueue() {
        service.addTimeEntry();

        assertEquals(1, service.getQueue().size());
    }

    @Test
    void processQueue_WhenQueueIsNotEmpty_ShouldSaveAllEntries() {
        service.addTimeEntry();
        service.addTimeEntry();

        when(repository.saveAll(any())).thenReturn(null);

        service.processQueue();

        verify(repository, times(1)).saveAll(any());
        assertTrue(service.getQueue().isEmpty());
    }

    @Test
    void processQueue_WhenQueueIsEmpty_ShouldDoesNotSaveEntries() {
        service.processQueue();

        verify(repository, never()).saveAll(any());
        assertTrue(service.getQueue().isEmpty());
    }

    @Test
    void processQueue_WhenSavingFails_ShouldRestoreEntriesToQueue() {
        service.addTimeEntry();
        service.addTimeEntry();

        doThrow(new RuntimeException("DB failure")).when(repository).saveAll(any());

        service.processQueue();

        verify(repository, times(1)).saveAll(any());
        assertEquals(2, service.getQueue().size());
    }

    @Test
    void findAllEntries_ShouldReturnListOfTimeEntryDto() {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setTimestamp(LocalDateTime.parse("2023-10-01T12:00:00"));
        List<TimeEntry> entries = List.of(timeEntry);

        when(repository.findAll()).thenReturn(entries);

        List<TimeEntryDto> result = service.findAllEntries();

        assertEquals(1, result.size());
        assertEquals(LocalDateTime.parse("2023-10-01T12:00:00"), result.getFirst().getTimestamp());
    }
}