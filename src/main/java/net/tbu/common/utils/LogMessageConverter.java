package net.tbu.common.utils;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogMessageConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        if (event.getLoggerName().equalsIgnoreCase("c.i.t.TraceLogger")) {
            return event.getMessage();
        }
        String message = event.getFormattedMessage();
        message = message.replaceAll("(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)", "***");
        message = message.replaceAll("(\\d{3})(\\d{10,13})(\\d{3}\\\\*)", "***");
        message = message.replaceAll("\\d{11}", "***");
        message = message.replaceAll("(\\d{3})(\\d{11,14})(\\w{1})", "***");
        return message;
    }
}
