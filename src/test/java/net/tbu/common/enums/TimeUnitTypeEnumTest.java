package net.tbu.common.enums;

import org.junit.jupiter.api.Test;

import static java.lang.System.out;

class TimeUnitTypeEnumTest {

    @Test
    void test0() {
        var enumsIn = TimeUnitTypeEnum.getEnumsInSelected(
                TimeUnitTypeEnum.SECOND.name(),
                TimeUnitTypeEnum.FIVE_MINUTES.name(),
                TimeUnitTypeEnum.HALF_DAY.name(),
                TimeUnitTypeEnum.TWO_HOURS.name(),
                TimeUnitTypeEnum.DAY.name(),
                TimeUnitTypeEnum.INVALID.name());

        TimeUnitTypeEnum next = TimeUnitTypeEnum.getNextPeriodBySelected(TimeUnitTypeEnum.SECOND, enumsIn);

        out.println(next);

        enumsIn.stream().sorted()
                .map(TimeUnitTypeEnum::name)
                .forEach(out::println);

        enumsIn.stream().map(TimeUnitTypeEnum::getEventId)
                .sorted(Integer::compareTo)
                .forEach(out::println);

    }

}