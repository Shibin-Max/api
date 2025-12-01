package net.tbu.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    FAIL(0, "FAIL"),
    SUCCESS(1, "SUCCESS"),
    SUCCESS2(2, "The QR code is not bound, please scan the code to bind it");

    private final int code;
    private final String msg;

}