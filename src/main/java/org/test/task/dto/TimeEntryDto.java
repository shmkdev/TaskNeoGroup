package org.test.task.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimeEntryDto {
    private LocalDateTime timestamp;
}
