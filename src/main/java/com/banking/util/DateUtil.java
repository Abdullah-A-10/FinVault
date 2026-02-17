package com.banking.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public class DateUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * Format a LocalDateTime as a string
     * 
     * @param dateTime LocalDateTime to format
     * @return Formatted date and time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Format a LocalDate as a string
     * 
     * @param date LocalDate to format
     * @return Formatted date string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Format a LocalTime as a string
     * 
     * @param time LocalTime to format
     * @return Formatted time string
     */
    public static String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        return time.format(TIME_FORMATTER);
    }
    
    /**
     * Parse a string into a LocalDateTime
     * 
     * @param dateTimeString Date and time string
     * @return LocalDateTime object
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER);
    }
    
    /**
     * Parse a string into a LocalDate
     * 
     * @param dateString Date string
     * @return LocalDate object
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
    
    /**
     * Parse a string into a LocalTime
     * 
     * @param timeString Time string
     * @return LocalTime object
     */
    public static LocalTime parseTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeString, TIME_FORMATTER);
    }
    
    /**
     * Get the start of a day (00:00:00)
     * 
     * @param date Date
     * @return LocalDateTime at the start of the day
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }
    
    /**
     * Get the end of a day (23:59:59.999999999)
     * 
     * @param date Date
     * @return LocalDateTime at the end of the day
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(LocalTime.MAX);
    }
    
    /**
     * Get the start of the current month
     * 
     * @return LocalDate at the start of the current month
     */
    public static LocalDate startOfCurrentMonth() {
        LocalDate today = LocalDate.now();
        return LocalDate.of(today.getYear(), today.getMonth(), 1);
    }
    
    /**
     * Get the end of the current month
     * 
     * @return LocalDate at the end of the current month
     */
    public static LocalDate endOfCurrentMonth() {
        LocalDate today = LocalDate.now();
        return LocalDate.of(today.getYear(), today.getMonth(), today.lengthOfMonth());
    }
}