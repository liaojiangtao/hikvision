package com.fangle.hikvision.ehomeSdk;

import com.sun.jna.*;

/**
 * @author Gentel
 * @description 预览回放取流模块
 * @create 2019-10-23 16:19
 */
public interface HCEHomeStream extends Library {

    HCEHomeStream INSTANCE = (HCEHomeStream) Native.loadLibrary("HCEHomeStream",
            HCEHomeStream.class);

    public class NET_EHOME_LISTEN_PREVIEW_CFG extends Structure {
        public HCEHomeCMS.NET_EHOME_IPADDRESS struIPAdress; //本地监听信息，IP为0.0.0.0的情况下，默认为本地地址，多个网卡的情况下，默认为从操作系统获取到的第一个
        public PREVIEW_NEWLINK_CB fnNewLinkCB; //预览请求回调函数，当收到预览连接请求后，SDK会回调该回调函数。
        public Pointer pUser;        // 用户参数，在fnNewLinkCB中返回出来
        public byte byLinkMode;   //0：TCP，1：UDP 2: HRUDP方式
        public byte[] byRes = new byte[127];
    }

    public class NET_EHOME_NEWLINK_CB_MSG extends Structure {
        public byte[] szDeviceID = new byte[HCEHomeCMS.MAX_DEVICE_ID_LEN];   //设备标示符
        public NativeLong iSessionID;     //设备分配给该取流会话的ID
        public int dwChannelNo;    //设备通道号
        public byte byStreamType;   //0-主码流，1-子码流
        public byte[] byRes1 = new byte[3];
        public byte[] sDeviceSerial = new byte[HCEHomeCMS.NET_EHOME_SERIAL_LEN];    //设备序列号，数字序列号
        public byte[] byRes = new byte[112];
    }

    public class NET_EHOME_PREVIEW_CB_MSG extends Structure {
        public byte byDataType;       //NET_DVR_SYSHEAD(1)-码流头，NET_DVR_STREAMDATA(2)-码流数据
        public byte[] byRes1 = new byte[3];
        public Pointer pRecvdata;      //码流头或者数据
        public int dwDataLen;      //数据长度
        public byte[] byRes2 = new byte[128];
    }

    public class NET_EHOME_PREVIEW_DATA_CB_PARAM extends Structure {
        public PREVIEW_DATA_CB fnPreviewDataCB;    //数据回调函数
        public Pointer pUserData;         //用户参数, 在fnPreviewDataCB回调出来
        public byte[] byRes = new byte[128];          //保留
    }

    public interface PREVIEW_NEWLINK_CB extends Callback {
        public boolean invoke(NativeLong lLinkHandle, NET_EHOME_NEWLINK_CB_MSG pNewLinkCBMsg, Pointer pUserData);
    }

    public interface PREVIEW_DATA_CB extends Callback {
        public void invoke(NativeLong iPreviewHandle, NET_EHOME_PREVIEW_CB_MSG pPreviewCBMsg, Pointer pUserData);
    }

    public interface fExceptionCallBack extends Callback {
        public void invoke(int dwType, NativeLong iUserID, NativeLong iHandle, Pointer pUser);
    }

    public boolean NET_ESTREAM_Init();

    public boolean NET_ESTREAM_Fini();

    public boolean NET_ESTREAM_GetLastError();

    public boolean NET_ESTREAM_SetExceptionCallBack(int dwMessage, int hWnd, fExceptionCallBack cbExceptionCallBack, Pointer pUser);

    public boolean NET_ESTREAM_SetLogToFile(int iLogLevel, String strLogDir, boolean bAutoDel);

    //获取版本号
    public int NET_ESTREAM_GetBuildVersion();

    public NativeLong NET_ESTREAM_StartListenPreview(NET_EHOME_LISTEN_PREVIEW_CFG pListenParam);

    public boolean NET_ESTREAM_StopListenPreview(NativeLong iListenHandle);

    public boolean NET_ESTREAM_StopPreview(NativeLong iPreviewHandle);

    public boolean NET_ESTREAM_SetPreviewDataCB(NativeLong iHandle, NET_EHOME_PREVIEW_DATA_CB_PARAM pStruCBParam);
}
