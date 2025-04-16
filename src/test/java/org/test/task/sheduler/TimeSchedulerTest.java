package org.test.task.sheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test.task.service.TimeEntryService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSchedulerTest {

    @Mock
    private TimeEntryService timeEntryService;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @InjectMocks
    private TimeScheduler timeScheduler;

    @Test
    void logTime_CallsServiceAddTimeEntry() {
        doNothing().when(timeEntryService).addTimeEntry();
        timeScheduler.logTime();

        verify(timeEntryService).addTimeEntry();
    }

    @Test
    void saveBatch_WhenDbAvailable_CallsProcessQueue() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);
        doNothing().when(timeEntryService).processQueue();

        timeScheduler.saveBatch();

        verify(dataSource).getConnection();
        verify(connection).isValid(500);
        verify(connection).close();
        verify(timeEntryService).processQueue();
    }

    @Test
    void saveBatch_WhenDbUnavailable_IsValidFalse_SkipsProcessQueueAndLogsWarning() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(false);

        timeScheduler.saveBatch();

        verify(dataSource).getConnection();
        verify(connection).isValid(500);
        verify(connection).close();
        verify(timeEntryService, never()).processQueue();
    }

    @Test
    void saveBatch_WhenDbUnavailable_SqlException_SkipsProcessQueueAndLogsWarning() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection timed out"));

        timeScheduler.saveBatch();

        verify(dataSource).getConnection();
        verify(connection, never()).isValid(anyInt());
        verify(connection, never()).close();
        verify(timeEntryService, never()).processQueue();
    }

    @Test
    void saveBatch_WhenDbAvailable_AndProcessQueueFailsAndLogsError() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);
        doThrow(new RuntimeException("Error during batch processing")).when(timeEntryService).processQueue();

        timeScheduler.saveBatch();

        verify(dataSource).getConnection();
        verify(connection).isValid(500);
        verify(connection).close();
        verify(timeEntryService).processQueue();
    }
}