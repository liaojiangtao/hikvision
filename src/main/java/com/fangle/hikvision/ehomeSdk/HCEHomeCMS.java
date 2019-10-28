package com.fangle.hikvision.ehomeSdk;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Callback;
import com.sun.jna.Library;

/**
 * @author Gentel
 * @description CMS注册模块
 * @create 2019-10-23 16:19
 */
public interface HCEHomeCMS extends Library {

    HCEHomeCMS INSTANCE = (HCEHomeCMS) Native.load("HCEHomeCMS",
            HCEHomeCMS.class);
    /***宏定义***/
    //常量

    public static final int MAX_NAMELEN = 16;    //DVR本地登陆名
    public static final int MAX_RIGHT = 32;    //设备支持的权限（1-12表示本地权限，13-32表示远程权限）
    public static final int NAME_LEN = 32;    //用户名长度
    public static final int PASSWD_LEN = 16;    //密码长度
    public static final int MAX_DEVICE_ID_LEN = 256;    //设备ID长度
    public static final int NET_EHOME_SERIAL_LEN = 12;  //设备序列号长度


    public static final int MAX_MASTER_KEY_LEN = 16;

    @Structure.FieldOrder({"dwSize", "dwNetUnitType", "byDeviceID", "byFirmwareVersion", "struDevAdd", "dwDevType",
            "dwManufacture", "byPassWord", "sDeviceSerial", "byReliableTransmission", "byWebSocketTransmission",
            "bySupportRedirect", "byDevProtocolVersion", "bySessionKey"})
    public static class NET_EHOME_DEV_REG_INFO extends Structure {
        public int dwSize;
        public int dwNetUnitType;            //根据EHomeSDK协议预留，目前没有意义
        public byte[] byDeviceID = new byte[MAX_DEVICE_ID_LEN]; //设备ID
        public byte[] byFirmwareVersion = new byte[24]; //固件版本
        public NET_EHOME_IPADDRESS struDevAdd;         //设备注册上来是，设备的本地地址

        public int dwDevType;                  //设备类型
        public int dwManufacture;              //设备厂家代码
        public byte[] byPassWord = new byte[32]; //设备登陆CMS的密码，由用户自行根据需求进行验证
        public byte[] sDeviceSerial = new byte[NET_EHOME_SERIAL_LEN]; //设备序列号，数字序列号

        public byte byReliableTransmission;
        public byte byWebSocketTransmission;
        public byte bySupportRedirect;    //设备支持重定向注册 0-不支持 1-支持
        public byte[] byDevProtocolVersion = new byte[6]; //设备协议版本
        public byte[] bySessionKey = new byte[MAX_MASTER_KEY_LEN]; //Ehome5.0设备SessionKey
    }

    @Structure.FieldOrder({"dwSize", "dwKeepAliveSec", "dwTimeOutCount", "struTCPAlarmSever", "struUDPAlarmSever",
            "dwAlarmServerType", "struNTPSever", "dwNTPInterval", "struPictureSever", "dwPicServerType", "struBlackListServer",
            "struRedirectSever", "byClouldAccessKey", "byClouldSecretKey", "byClouldHttps", "byRes"})
    public static class NET_EHOME_SERVER_INFO_V50 extends Structure {
        public int dwSize;
        public int dwKeepAliveSec;            //心跳间隔（单位：秒,0:默认为15S）
        public int dwTimeOutCount;         //心跳超时次数（0：默认为6）
        public NET_EHOME_IPADDRESS struTCPAlarmSever;      //报警服务器地址（TCP协议）
        public NET_EHOME_IPADDRESS struUDPAlarmSever;        //报警服务器地址（UDP协议）
        public int dwAlarmServerType;        //报警服务器类型0-只支持UDP协议上报，1-支持UDP、TCP两种协议上报
        public NET_EHOME_IPADDRESS struNTPSever;            //NTP服务器地址
        public int dwNTPInterval;            //NTP校时间隔（单位：秒）
        public NET_EHOME_IPADDRESS struPictureSever;       //图片服务器地址
        public int dwPicServerType;        //图片服务器类型图片服务器类型，1-VRB图片服务器，0-Tomcat图片服务,2-云存储3,3-KMS
        public NET_EHOME_BLACKLIST_SEVER struBlackListServer;    //黑名单服务器
        public NET_EHOME_IPADDRESS struRedirectSever;       //Redirect Server

        public byte[] byClouldAccessKey = new byte[64];  //云存储AK
        public byte[] byClouldSecretKey = new byte[64];  //云存储SK
        public byte byClouldHttps;           //云存储HTTPS使能 1-HTTPS 0-HTTP
        public byte[] byRes = new byte[383];
    }

    @Structure.FieldOrder({"dwSize", "dwKeepAliveSec", "dwTimeOutCount", "struTCPAlarmSever", "struUDPAlarmSever",
            "dwAlarmServerType", "struNTPSever", "dwNTPInterval", "struPictureSever", "dwPicServerType",
            "struBlackListServer", "byRes"})
    public static class NET_EHOME_SERVER_INFO extends Structure {
        public int dwSize;
        public int dwKeepAliveSec;            //心跳间隔（单位：秒,0:默认为15S）
        public int dwTimeOutCount;         //心跳超时次数（0：默认为6）
        public NET_EHOME_IPADDRESS struTCPAlarmSever;      //报警服务器地址（TCP协议）
        public NET_EHOME_IPADDRESS struUDPAlarmSever;        //报警服务器地址（UDP协议）
        public int dwAlarmServerType;        //报警服务器类型0-只支持UDP协议上报，1-支持UDP、TCP两种协议上报，2 - 采用MQTT协议方式上报（此时服务器地址为TCP协议服务地址）
        public NET_EHOME_IPADDRESS struNTPSever;            //NTP服务器地址
        public int dwNTPInterval;            //NTP校时间隔（单位：秒）
        public NET_EHOME_IPADDRESS struPictureSever;       //图片服务器地址
        public int dwPicServerType;        //图片服务器类型图片服务器类型，1-VRB图片服务器，0-Tomcat图片服务,2-云存储3,3-KMS,4-EHome5.0存储协议
        public NET_EHOME_BLACKLIST_SEVER struBlackListServer;    //黑名单服务器
        public byte[] byRes = new byte[128];
    }

    @Structure.FieldOrder({"struAdd", "byServerName", "byUserName", "byPassWord", "byRes"})
    public static class NET_EHOME_BLACKLIST_SEVER extends Structure {
        public NET_EHOME_IPADDRESS struAdd = new NET_EHOME_IPADDRESS(); //服务器地址
        public byte[] byServerName = new byte[32];  //服务器名称
        public byte[] byUserName = new byte[32]; //用户名
        public byte[] byPassWord = new byte[32];//密码
        public byte[] byRes = new byte[64];
    }

    @Structure.FieldOrder({"dwSize", "byAccessSecurity", "byRes"})
    public static class NET_EHOME_LOCAL_ACCESS_SECURITY extends Structure {
        public int dwSize;
        public byte byAccessSecurity;
        public byte[] byRes = new byte[127];
    }

    @Structure.FieldOrder({"dwSize", "byEnable", "byRes1", "struAddress", "byRes2"})
    public static class NET_EHOME_AMS_ADDRESS extends Structure {
        public int dwSize;
        public byte byEnable;
        public byte[] byRes1 = new byte[3];
        public NET_EHOME_IPADDRESS struAddress = new NET_EHOME_IPADDRESS();
        public byte[] byRes2 = new byte[32];
    }

    @Structure.FieldOrder({"szIP", "wPort", "byRes"})
    public static class NET_EHOME_IPADDRESS extends Structure {
        public byte[] szIP = new byte[128];
        public short wPort;     //端口
        public byte[] byRes = new byte[2];
    }

    public static interface DEVICE_REGISTER_CB extends Callback {
        public boolean invoke(NativeLong lUserID, int dwDataType, Pointer pOutBuffer, int dwOutLen, NET_EHOME_SERVER_INFO pInBuffer, int dwInLen, Pointer pUser);
    }

    @Structure.FieldOrder({"struAddress", "fnCB", "pUserData", "byRes"})
    public static class NET_EHOME_CMS_LISTEN_PARAM extends Structure {
        public NET_EHOME_IPADDRESS struAddress;  //本地监听信息，IP为0.0.0.0的情况下，默认为本地地址，多个网卡的情况下，默认为从操作系统获取到的第一个
        public DEVICE_REGISTER_CB fnCB; //报警信息回调函数
        public Pointer pUserData;   //用户数据
        public byte[] byRes = new byte[32];
    }

    public static interface fVoiceDataCallBack extends Callback {
        public void invoke(NativeLong iVoiceHandle, byte[] pRecvDataBuffer, int dwBufSize, int dwEncodeType, byte byAudioFlag, Pointer pUser);
    }

    @Structure.FieldOrder({"bNeedCBNoEncData", "cbVoiceDataCallBack", "dwEncodeType", "pUser", "byVoiceTalk", "byDevAudioEnc", "byRes"})
    public static class NET_EHOME_VOICETALK_PARA extends Structure {
        public boolean bNeedCBNoEncData; //需要回调的语音类型：0-编码后语音，1-编码前语音（语音转发时不支持）
        public fVoiceDataCallBack cbVoiceDataCallBack; //用于回调音频数据的回调函数
        public int dwEncodeType;    //SDK赋值,SDK的语音编码类型,0- OggVorbis，1-G711U，2-G711A，3-G726，4-AAC，5-MP2L2，6-PCM
        public Pointer pUser;    //用户参数
        public byte byVoiceTalk;    //0-语音对讲,1-语音转发
        public byte byDevAudioEnc;  //输出参数，设备的音频编码方式 0- OggVorbis，1-G711U，2-G711A，3-G726，4-AAC，5-MP2L2，6-PCM
        public byte[] byRes = new byte[62];//Reserved, set as 0. 0
    }

    //预览请求
    @Structure.FieldOrder({"iChannel", "dwStreamType", "dwLinkMode", "struStreamSever"})
    public static class NET_EHOME_PREVIEWINFO_IN extends Structure {
        public int iChannel;                        //通道号
        public int dwStreamType;                    // 码流类型，0-主码流，1-子码流, 2-第三码流
        public int dwLinkMode;                        // 0：TCP方式,1：UDP方式,2: HRUDP方式
        public NET_EHOME_IPADDRESS struStreamSever;     //流媒体地址
    }

    @Structure.FieldOrder({"iChannel", "dwStreamType", "dwLinkMode", "struStreamSever", "byDelayPreview", "byRes"})
    public static class NET_EHOME_PREVIEWINFO_IN_V11 extends Structure {
        public int iChannel;
        public int dwStreamType;
        public int dwLinkMode;
        public NET_EHOME_IPADDRESS struStreamSever;
        public byte byDelayPreview;
        public byte[] byRes = new byte[31];
    }

    @Structure.FieldOrder({"lSessionID", "byRes"})
    public static class NET_EHOME_PREVIEWINFO_OUT extends Structure {
        public NativeLong lSessionID;
        public byte[] byRes = new byte[128];
    }

    @Structure.FieldOrder({"dwSize", "lSessionID", "byRes"})
    public static class NET_EHOME_PUSHSTREAM_IN extends Structure {
        public int dwSize;
        public NativeLong lSessionID;
        public byte[] byRes = new byte[128];
    }

    @Structure.FieldOrder({"dwSize", "byRes"})
    public static class NET_EHOME_PUSHSTREAM_OUT extends Structure {
        public int dwSize;
        public byte[] byRes = new byte[128];
    }

    @Structure.FieldOrder({"pRequestUrl", "dwRequestUrlLen", "pCondBuffer", "dwCondSize", "pInBuffer", "dwInSize",
            "pOutBuffer", "dwOutSize", "dwReturnedXMLLen", "byRes"})
    public static class NET_EHOME_PTXML_PARAM extends Structure {
        public Pointer pRequestUrl;        //请求URL
        public int dwRequestUrlLen;    //请求URL长度
        public Pointer pCondBuffer;        //条件缓冲区（XML格式数据）
        public int dwCondSize;         //条件缓冲区大小
        public Pointer pInBuffer;          //输入缓冲区（XML格式数据）
        public int dwInSize;           //输入缓冲区大小
        public Pointer pOutBuffer;         //输出缓冲区（XML格式数据）
        public int dwOutSize;          //输出缓冲区大小
        public int dwReturnedXMLLen;   //实际从设备接收到的XML数据的长度
        public byte[] byRes = new byte[32];          //保留
    }

    @Structure.FieldOrder({"pCmdBuf", "dwCmdLen", "pInBuf", "dwInSize", "pOutBuf", "dwOutSize", "dwSendTimeOut",
            "dwRecvTimeOut", "pStatusBuf", "dwStatusSize", "byRes"})
    public static class NET_EHOME_XML_CFG extends Structure {
        public Pointer pCmdBuf;    //字符串格式命令，参见1.2.3
        public int dwCmdLen;   //pCmdBuf长度
        public Pointer pInBuf;     //输入数据，远程配置报文公用定义
        public int dwInSize;   //输入数据长度
        public Pointer pOutBuf;    //输出缓冲<ConfigXML>
        public int dwOutSize;  //输出缓冲区长度
        public int dwSendTimeOut;  //数据发送超时时间,单位ms，默认5s
        public int dwRecvTimeOut;  //数据接收超时时间,单位ms，默认5s
        public Pointer pStatusBuf;     //返回的状态参数(XML格式),如果不需要,可以置NULL
        public int dwStatusSize;   //状态缓冲区大小(内存大小)
        public byte[] byRes = new byte[24];
    }

    @Structure.FieldOrder({"dwSize", "lpInbuffer", "dwInBufferSize", "dwSendTimeOut", "dwRecvTimeOut", "lpOutBuffer",
            "dwOutBufferSize", "lpStatusBuffer", "dwStatusBufferSize", "byRes"})
    public static class NET_EHOME_XML_REMOTE_CTRL_PARAM extends Structure {
        public int dwSize;
        public Pointer lpInbuffer;
        public int dwInBufferSize;
        public int dwSendTimeOut;
        public int dwRecvTimeOut;
        public Pointer lpOutBuffer;     //输出缓冲区
        public int dwOutBufferSize;  //输出缓冲区大小
        public Pointer lpStatusBuffer;   //状态缓冲区,若不需要可置为NULL
        public int dwStatusBufferSize;  //状态缓冲区大小
        public byte[] byRes = new byte[16];
    }

    ;

    @Structure.FieldOrder({"byString"})
    public static class NET_DVR_STRING_POINTER extends Structure {
        public byte[] byString = new byte[2 * 1024];
    }

    @Structure.FieldOrder({"pCondBuf", "dwCondSize", "pInBuf", "dwInSize", "pOutBuf", "dwOutSize", "byRes"})
    public static class NET_EHOME_CONFIG extends Structure {
        public Pointer pCondBuf;
        public int dwCondSize;
        public Pointer pInBuf;
        public int dwInSize;
        public Pointer pOutBuf;
        public int dwOutSize;
        public byte[] byRes = new byte[40];
    }

    @Structure.FieldOrder({"dwSize", "dwChannelNumber", "dwChannelAmount", "dwDevType", "dwDiskNumber", "sSerialNumber",
            "dwAlarmInPortNum", "dwAlarmInAmount", "dwAlarmOutPortNum", "dwAlarmOutAmount", "dwStartChannel",
            "dwAudioChanNum", "dwMaxDigitChannelNum", "dwAudioEncType", "sSIMCardSN", "sSIMCardPhoneNum",
            "dwSupportZeroChan", "dwStartZeroChan", "dwSmartType", "byRes"})
    public static class NET_EHOME_DEVICE_INFO extends Structure {
        public int dwSize;
        public int dwChannelNumber;
        public int dwChannelAmount;
        public int dwDevType;
        public int dwDiskNumber;
        public byte[] sSerialNumber = new byte[128];
        public int dwAlarmInPortNum;
        public int dwAlarmInAmount;
        public int dwAlarmOutPortNum;
        public int dwAlarmOutAmount;
        public int dwStartChannel;
        public int dwAudioChanNum;
        public int dwMaxDigitChannelNum;
        public int dwAudioEncType;
        public byte[] sSIMCardSN = new byte[128];
        public byte[] sSIMCardPhoneNum = new byte[32];
        public int dwSupportZeroChan;
        public int dwStartZeroChan;
        public int dwSmartType;
        public byte[] byRes = new byte[160];
    }

    ;

    @Structure.FieldOrder({"dwChannel", "dwRecType", "struStartTime", "struStopTime", "dwStartIndex", "dwMaxFileCountPer", "byRes"})
    public static class NET_EHOME_REC_FILE_COND extends Structure {
        public int dwChannel; //通道号
        public int dwRecType;//录像类型：0xff-全部类型，0-定时录像，1-移动报警，2-报警触发，3-报警|动测，4-报警&动测，5-命令触发，6-手动录像，7-震动报警，8-环境报警，9-智能报警（或者取证录像），10（0x0a）-PIR报警，11（0x0b）-无线报警，12（0x0c）-呼救报警，13（0x0d）-全部报警
        public NET_EHOME_TIME struStartTime = new NET_EHOME_TIME();//开始时间
        public NET_EHOME_TIME struStopTime = new NET_EHOME_TIME();//结束时间
        public int dwStartIndex;//查询起始位置，从0开始
        public int dwMaxFileCountPer;//单次搜索最大文件个数，最大文件个数，需要确定实际网络环境，建议最大个数为8
        public byte[] byRes = new byte[64];
    }

    ;

    @Structure.FieldOrder({"dwSize", "sFileName", "struStartTime", "struStopTime", "dwFileSize", "dwFileMainType",
            "dwFileSubType", "dwFileIndex", "byRes"})
    public static class NET_EHOME_REC_FILE extends Structure {
        public int dwSize;
        public byte[] sFileName = new byte[100];
        public NET_EHOME_TIME struStartTime = new NET_EHOME_TIME();
        public NET_EHOME_TIME struStopTime = new NET_EHOME_TIME();
        public int dwFileSize;
        public int dwFileMainType;
        public int dwFileSubType;
        public int dwFileIndex;
        public byte[] byRes = new byte[128];
    }

    ;

    @Structure.FieldOrder({"wYear", "byMonth", "byDay", "byHour", "byMinute", "bySecond", "byRes1", "wMSecond", "byRes2"})
    public static class NET_EHOME_TIME extends Structure {
        public short wYear;//年
        public byte byMonth;//月
        public byte byDay;//日
        public byte byHour;//时
        public byte byMinute;//分
        public byte bySecond;//秒
        public byte byRes1;//保留
        public short wMSecond;//毫秒
        public byte[] byRes2 = new byte[2];
    }

    //初始化，反初始化
    boolean NET_ECMS_Init();

    boolean NET_ECMS_Fini();

    //获取错误码
    int NET_ECMS_GetLastError();

    //获取版本号
    int NET_ECMS_GetBuildVersion();

    boolean NET_ECMS_SetSDKLocalCfg(int enumType, Pointer lpInBuff);

    boolean NET_ECMS_GetSDKLocalCfg(int enumType, Pointer lpOutBuff);

    //开启关闭监听
    NativeLong NET_ECMS_StartListen(NET_EHOME_CMS_LISTEN_PARAM lpCMSListenPara);

    boolean NET_ECMS_StopListen(NativeLong iHandle);

    boolean NET_ECMS_GetDevConfig(NativeLong lUserID, int dwCommand, NET_EHOME_CONFIG lpConfig, int dwConfigSize);

    //注销设备
    boolean NET_ECMS_ForceLogout(NativeLong lUserID)
    ;

    boolean NET_ECMS_SetLogToFile(int iLogLevel, String strLogDir, boolean bAutoDel);

    NativeLong NET_ECMS_StartVoiceTalk(NativeLong lUserID, int dwVoiceChan, NET_EHOME_VOICETALK_PARA pVoiceTalkPara);

    boolean NET_ECMS_StopVoiceTalk(NativeLong iVoiceHandle);

    boolean NET_ECMS_StopVoiceTalkWithStmServer(NativeLong lUserID, NativeLong lSessionID);

    boolean NET_ECMS_SendVoiceTransData(NativeLong iVoiceHandle, String pSendBuf, int dwBufSize);

    boolean NET_ECMS_StartGetRealStream(NativeLong lUserID, NET_EHOME_PREVIEWINFO_IN pPreviewInfoIn, NET_EHOME_PREVIEWINFO_OUT pPreviewInfoOut); //lUserID由SDK分配的用户ID，由设备注册回调时fDeviceRegisterCallBack返回

    boolean NET_ECMS_StartGetRealStreamV11(NativeLong lUserID, NET_EHOME_PREVIEWINFO_IN_V11 pPreviewInfoIn, NET_EHOME_PREVIEWINFO_OUT pPreviewInfoOut);

    boolean NET_ECMS_StopGetRealStream(NativeLong lUserID, NativeLong lSessionID);

    boolean NET_ECMS_StartPushRealStream(NativeLong lUserID, NET_EHOME_PUSHSTREAM_IN pPushInfoIn, NET_EHOME_PUSHSTREAM_OUT pPushInfoOut);

    boolean NET_ESTREAM_StopPreview(NativeLong lPreviewHandle);

    boolean NET_ESTREAM_StopListenPreview(NativeLong lPreivewListenHandle);

    boolean NET_ECMS_GetPTXMLConfig(NativeLong iUserID, NET_EHOME_PTXML_PARAM lpPTXMLParam);

    boolean NET_ECMS_PutPTXMLConfig(NativeLong iUserID, NET_EHOME_PTXML_PARAM lpPTXMLParam);

    boolean NET_ECMS_PostPTXMLConfig(NativeLong iUserID, NET_EHOME_PTXML_PARAM lpPTXMLParam);

    boolean NET_ECMS_DeletePTXMLConfig(NativeLong iUserID, NET_EHOME_PTXML_PARAM lpPTXMLParam);

    boolean NET_ECMS_XMLConfig(NativeLong iUserID, NET_EHOME_XML_CFG pXmlCfg, int dwConfigSize);

    boolean NET_ECMS_XMLRemoteControl(NativeLong lUserID, NET_EHOME_XML_REMOTE_CTRL_PARAM lpCtrlParam, int dwCtrlSize);

    //获取查下句柄
    NativeLong NET_ECMS_StartFindFile_V11(NativeLong lUserID, int lSearchType, Pointer pFindCond, int dwCondSize);

    NativeLong NET_ECMS_FindNextFile_V11(NativeLong lHandle, Pointer pFindData, int dwDataSize);

    boolean NET_ECMS_StopFindFile(NativeLong lHandle);
}