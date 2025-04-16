package org.test.task.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.task.dto.TimeEntryDto;
import org.test.task.entity.TimeEntry;
import org.test.task.repository.TimeEntryRepository;
import org.test.task.service.TimeEntryService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class TimeEntryServiceImpl implements TimeEntryService {
    private final TimeEntryRepository repository;
    private final BlockingDeque<TimeEntry> queue = new LinkedBlockingDeque<>();

    @Override
    @Transactional(readOnly = true)
    public List<TimeEntryDto> findAllEntries() {
        return repository.findAll()
                .stream()
                .map(entry -> TimeEntryDto.builder()
                        .timestamp(entry.getTimestamp())
                        .build())
                .collect(toList());
    }

    @Override
    public void addTimeEntry() {
        TimeEntry timeEntry = new TimeEntry();
        queue.add(timeEntry);
        log.info("Saved timeEntry to Queue: {}", timeEntry);
    }

    @Override
    @Transactional
    public void processQueue() {
        List<TimeEntry> entriesToSave = new ArrayList<>();
        while (!queue.isEmpty()) {
            entriesToSave.add(queue.pollFirst());
        }

        try {
            if (!entriesToSave.isEmpty()) {
                repository.saveAll(entriesToSave);
                log.info("Saved timeEntries batch to DB: {}", entriesToSave);
                entriesToSave.clear();
            }
        } catch (Exception e) {
            while (!entriesToSave.isEmpty()) {
                queue.addFirst(entriesToSave.removeLast());
            }
            log.error("Failed to save timeEntries batch to DB: {}", e.getMessage());
        }
    }
}

