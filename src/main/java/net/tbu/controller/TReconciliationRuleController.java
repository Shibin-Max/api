package net.tbu.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * <p>
 * 对账规则表 前端控制器
 * </p>
 *
 * @author hao.yu
 * @since 2024-12-24
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "Reconciliation Rule API")
@Slf4j
public class TReconciliationRuleController {

    @GetMapping("/test")
    public Map<String, Object> test() {
        LocalDateTime nowRaw = LocalDateTime.now();
        String nowFormatted = nowRaw.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return Map.of(
                "nowRaw", nowRaw,
                "nowFormatted", nowFormatted
        );
    }

}
