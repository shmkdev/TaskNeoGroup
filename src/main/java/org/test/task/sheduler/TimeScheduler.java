package org.test.task.sheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.test.task.service.TimeEntryService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeScheduler {
    private final TimeEntryService service;
    private final DataSource dataSource;

    @Scheduled(fixedRate = 1000)
    public void logTime() {
        service.addTimeEntry();
    }

    @Scheduled(initialDelay = 1000, fixedRate = 5000)
    public void saveBatch() {
        if (!isDatabaseAvailable()) {
            log.warn("DB is unavailable");
            return;
        }

        try {
            service.processQueue();
        } catch (Exception e) {
            log.error("Failed to log time entry, DB is unavailable", e);
        }
    }

    private boolean isDatabaseAvailable() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(500);
        } catch (SQLException e) {
            return false;
        }
    }
}
