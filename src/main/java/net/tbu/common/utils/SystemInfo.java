package net.tbu.common.utils;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Locale;

@Component
@RequestScope
@Data
public class SystemInfo {
    private String username;
    private String platformId;
    private Locale locale;
}
