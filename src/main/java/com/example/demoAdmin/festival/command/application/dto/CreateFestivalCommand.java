package com.example.demoadmin.festival.command.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateFestivalCommand(
        String name,
        String description,
        String address,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime operationStartTime,
        LocalTime operationEndTime
) {
}
