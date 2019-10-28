package com.fangle.hikvision.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EhomeAlarmMsgEnum {
    EHOME_ALARM_UNKNOWN(0), //  0 未知报警 未知
    EHOME_ALARM(1), //  1 Ehome基本报警 NET_EHOME_ALARM_INFO
    EHOME_ALARM_HEATMAP_REPORT(2), //  2 热度图报告上传 NET_EHOME_HEATMAP_REPORT
    EHOME_ALARM_FACESNAP_REPORT(3), //  3 人脸抓拍报告上传 NET_EHOME_FACESNAP_REPORT
    EHOME_ALARM_GPS(4), //  4 GPS信息上传 NET_EHOME_GPS_INFO
    EHOME_ALARM_CID_REPORT(5), //  5 报警主机CID告警上传 NET_EHOME_CID_INFO
    EHOME_ALARM_NOTICE_PICURL(6), //  6 图片URL上报 NET_EHOME_NOTICE_PICURL
    EHOME_ALARM_NOTIFY_FAIL(7), //  7 异步失败通知 NET_EHOME_NOTIFY_FAIL_INFO
    ;

    private Integer status;
}
