package net.tbu.common.enums;

/**
 * @author FT hao.yu
 * @time 2024/12/24 15:26
 */
public interface IntEnumInterface {

    int getEventId();

    default boolean equalsValue(Object eventId) {
        if (eventId == null) {
            return false;
        }
        return String.valueOf(eventId).equals(String.valueOf(this.getEventId()));
    }

}
