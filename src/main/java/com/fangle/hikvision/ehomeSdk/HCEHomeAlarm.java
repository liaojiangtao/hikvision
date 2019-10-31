package com.fangle.hikvision.ehomeSdk;

import com.sun.jna.*;

/**
 * @author Gentel
 * @description 报警管理服务器
 * @create 2019-10-23 16:19
 */
public interface HCEHomeAlarm extends Library {

    HCEHomeAlarm INSTANCE = (HCEHomeAlarm) Native.load("HCEHomeAlarm",
            HCEHomeAlarm.class);
    /***宏定义***/
    //常量
    public static final int MAX_DEVICE_ID_LEN = 256;    //设备ID长度
    public static final int NET_EHOME_SERIAL_LEN = 12;  //设备序列号长度

    @Structure.FieldOrder({"szIP", "wPort", "byRes"})
    public static class NET_EHOME_IPADDRESS extends Structure {
        public byte[] szIP = new byte[128];
        public short wPort;     //端口
        public byte[] byRes = new byte[2];
    }

    @Structure.FieldOrder({"byXMLData"})
    public static class NET_EHOME_XML_DATA extends Structure {
        public byte[] byXMLData = new byte[2048];
    }

    @Structure.FieldOrder({"dwAlarmType", "pAlarmInfo", "dwAlarmInfoLen", "pXmlBuf", "dwXmlBufLen", "sSerialNumber",
            "pHttpUrl", "dwHttpUrlLen", "byRes"})
    public static class NET_EHOME_ALARM_MSG extends Structure {
        public int dwAlarmType;      //报警类型，见EN_ALARM_TYPE
        public Pointer pAlarmInfo;       //报警内容（结构体）
        public int dwAlarmInfoLen;   //结构体报警内容长度
        public Pointer pXmlBuf;          //报警内容（XML）
        public int dwXmlBufLen;      //xml报警内容长度
        public byte[] sSerialNumber = new byte[NET_EHOME_SERIAL_LEN]; //设备序列号，用于进行Token认证
        public Pointer pHttpUrl;
        public int dwHttpUrlLen;
        public byte[] byRes = new byte[12];
    }

    public static interface EHomeMsgCallBack extends Callback {
        public boolean invoke(NativeLong iHandle, NET_EHOME_ALARM_MSG pAlarmMsg, Pointer pUser);
    }

    @Structure.FieldOrder({"struAddress", "fnMsgCb", "pUserData", "byProtocolType", "byUseCmsPort", "byUseThreadPool", "byRes"})
    public static class NET_EHOME_ALARM_LISTEN_PARAM extends Structure {
        public NET_EHOME_IPADDRESS struAddress;
        public EHomeMsgCallBack fnMsgCb; //报警信息回调函数
        public Pointer pUserData;   //用户数据
        public byte byProtocolType;    //协议类型，0-TCP,1-UDP,2-MQTT
        public byte byUseCmsPort; //是否复用CMS端口,0-不复用，非0-复用，如果复用cms端口，协议类型字段无效（此时本地监听信息struAddress填本地回环地址）
        public byte byUseThreadPool;  //0-回调报警时，使用线程池，1-回调报警时，不使用线程池，默认情况下，报警回调的时候，使用线程池
        public byte byRes[] = new byte[29];
        ;
    }

    //初始化，反初始化
    boolean NET_EALARM_Init();

    boolean NET_EALARM_Fini();

    NativeLong NET_EALARM_StartListen(NET_EHOME_ALARM_LISTEN_PARAM pAlarmListenParam);

    boolean NET_EALARM_SetLogToFile(int iLogLevel, String strLogDir, boolean bAutoDel);
}