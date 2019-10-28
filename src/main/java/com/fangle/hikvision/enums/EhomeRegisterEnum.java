package com.fangle.hikvision.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@Getter
public enum EhomeRegisterEnum {
    UNKNOWN(-1), // 未知
    DEV_ON(0),  // 设备上线回调
    DEV_OFF(1),  // 设备下线回调
    DEV_ADDRESS_CHANGED(2), // 设备地址发生变化
    ;

    private Integer status;
}
