package com.fangle.hikvision.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EhomePictureServerTypeEnum {
    //图片服务器类型图片服务器类型，1-VRB图片服务器，0-Tomcat图片服务,2-云存储3,3-KMS,4-EHome5.0存储协议
    TOMCAT(0),
    VRB(1),
    CLOUD(2),
    KMS(3),
    EHOME5_0(4),
    ;

    private Integer status;

}
