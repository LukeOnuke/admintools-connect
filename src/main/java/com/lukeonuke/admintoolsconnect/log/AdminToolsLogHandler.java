package com.lukeonuke.admintoolsconnect.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lukeonuke.admintoolsconnect.services.DataService;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Custom log handler that intercepts all server logs and sends them to AdminTools platform
 * Replaces the old Log4j filter implementation
 */
public class AdminToolsLogHandler extends Handler {
    private final DataService dataService;

    public AdminToolsLogHandler(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public void publish(LogRecord record) {
        if (record == null || record.getMessage() == null) {
            return;
        }

        try {
            // Format the log message
            String formattedMessage = formatMessage(record);
            
            // Send to AdminTools platform
            dataService.publishLog(formattedMessage);
        } catch (JsonProcessingException e) {
            // Avoid logging errors to prevent recursive logging
            System.err.println("AdminTools: Failed to publish log: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other exceptions to prevent breaking the logging system
            System.err.println("AdminTools: Unexpected error in log handler: " + e.getMessage());
        }
    }

    @Override
    public void flush() {
        // No buffering, so nothing to flush
    }

    @Override
    public void close() throws SecurityException {
        // No resources to close
    }

    /**
     * Format log record to a readable string
     */
    private String formatMessage(LogRecord record) {
        String level = record.getLevel().getName();
        String message = record.getMessage();
        String loggerName = record.getLoggerName();
        
        // Include logger name if available and not root
        if (loggerName != null && !loggerName.isEmpty()) {
            return String.format("[%s] [%s] %s", level, loggerName, message);
        }
        
        return String.format("[%s] %s", level, message);
    }
}
