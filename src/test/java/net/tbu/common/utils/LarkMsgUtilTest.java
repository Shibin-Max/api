package net.tbu.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.tbu.GameBetSlipCheckApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest(classes = GameBetSlipCheckApiApplication.class)
class LarkMsgUtilTest {

    @Resource
    LarkMsgUtil larkMsgUtil;

}
