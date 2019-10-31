package com.fangle.hikvision.ehome;

import akka.actor.ActorSystem;
import com.fangle.hikvision.ehomeSdk.HCEHomeAlarm;
import com.fangle.hikvision.ehomeSdk.HCEHomeCMS;
import com.fangle.hikvision.ehomeSdk.HCEHomeSS;
import com.fangle.hikvision.enums.EhomeAlarmMsgEnum;
import com.fangle.hikvision.enums.EhomePictureServerTypeEnum;
import com.fangle.hikvision.enums.EhomeRegisterEnum;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Gentel
 * @description ehome server
 * @create 2019-10-23 17:45
 */

@Slf4j
public class EhomeServer {

    private String serverIp;
    private Short serverPort;

    private String udpAlarmIp;
    private Short udpAlarmPort;

    private String tcpAlarmIp;
    private Short tcpAlarmPort;

    private String pictureServerIp;
    private Short pictureServerPort;

    @Autowired
    private EhomeSessionManage ehomeSessionManage;

    @Autowired
    private HCEHomeCMS hceHomeCMS;

    @Autowired
    private HCEHomeAlarm hceHomeAlarm;

    @Autowired
    private HCEHomeSS hceHomeSS;

    @Autowired
    private ActorSystem actorSystem;

    public EhomeServer(String serverIp, Short serverPort, String udpAlarmIp, Short udpAlarmPort, String tcpAlarmIp, Short tcpAlarmPort,
                       String pictureServerIp, short pictureServerPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;

        this.udpAlarmIp = udpAlarmIp;
        this.udpAlarmPort = udpAlarmPort;

        this.tcpAlarmIp = tcpAlarmIp;
        this.tcpAlarmPort = tcpAlarmPort;

        this.pictureServerIp = pictureServerIp;
        this.pictureServerPort = pictureServerPort;
    }

    public void start() {
        new Thread(new HCEHomeServerThread()).start();
    }

    public class HCEHomeServerThread implements Runnable {

        FRegisterCallBack fRegisterCallBack = null;
        EHomeMsgCallBack eHomeMsgCallBack = null;
        PSSMessageCallback pSSMessageCallback = null;
        PSSStorageCallback pSSStorageCallback = null;// 文件保存回调函数(下载)

        @Override
        public void run() {
            if (!ecmsInit()) return;
            if (!hceAlarmInit()) return;

            while (true) ;
        }

        /**
         * 报警模块初始化
         *
         * @return
         */
        private boolean hceAlarmInit() {
            // 报警模块初始化
            boolean bRet = hceHomeAlarm.NET_EALARM_Init();
            if (!bRet) {
                log.error("NET_EALARM_Init failed!");
            }
            log.info("NET_EALARM_Init!");
            if (eHomeMsgCallBack == null) {
                eHomeMsgCallBack = new EHomeMsgCallBack();
            }
            boolean alarmLogToFile = hceHomeAlarm.NET_EALARM_SetLogToFile(3, "/home/gentel/EhomePicServer/", false);

            if (false == alarmLogToFile){
                log.error("NET_EALARM_SetLogToFile init error!");
                return false;
            }

            HCEHomeAlarm.NET_EHOME_ALARM_LISTEN_PARAM netEhomeAlarmListenParam = new HCEHomeAlarm.NET_EHOME_ALARM_LISTEN_PARAM();
            netEhomeAlarmListenParam.struAddress.szIP = tcpAlarmIp.getBytes();
            netEhomeAlarmListenParam.struAddress.wPort = tcpAlarmPort;
            netEhomeAlarmListenParam.fnMsgCb = eHomeMsgCallBack;
            netEhomeAlarmListenParam.pUserData = null;
            netEhomeAlarmListenParam.byProtocolType = 1;//协议类型，0-TCP,1-UDP,2-MQTT
            netEhomeAlarmListenParam.byUseCmsPort = 0;//是否复用CMS端口：0 - 不复用，非0 - 复用
            netEhomeAlarmListenParam.byUseThreadPool = 0;
            NativeLong ARMListen = hceHomeAlarm.NET_EALARM_StartListen(netEhomeAlarmListenParam);
            if (ARMListen.byteValue() < 0) {
                log.error("NET_EALARM_StartListen失败, error code:" + hceHomeCMS.NET_ECMS_GetLastError());
                return false;
            } else {
                log.info("启动报警监听成功");
            }

            log.info("NET_EALARM_StartListen:" + ARMListen);

            boolean sinit = hceHomeSS.NET_ESS_Init();

            if (false == sinit) {
                log.error("HCEHomeSS init error!");
                return false;
            }

            boolean logToFile = hceHomeSS.NET_ESS_SetLogToFile(3, "/home/gentel/EhomePicServer/", false);

            if (false == logToFile) {
                log.error("NET_ESS_SetLogToFile init error!");
                return false;
            }

            if (pSSMessageCallback == null) {
                pSSMessageCallback = new PSSMessageCallback();
            }

            if (pSSStorageCallback == null) {
                pSSStorageCallback = new PSSStorageCallback();
            }

            HCEHomeSS.NET_EHOME_SS_LISTEN_PARAM pSSListenParam = new HCEHomeSS.NET_EHOME_SS_LISTEN_PARAM();
            pSSListenParam.byHttps = 0;
            pSSListenParam.fnSSMsgCb = pSSMessageCallback;

            pSSListenParam.fnSStorageCb = pSSStorageCallback;
            pSSListenParam.struAddress.szIP = pictureServerIp.getBytes();
            pSSListenParam.struAddress.wPort = pictureServerPort;

            NativeLong listen = hceHomeSS.NET_ESS_StartListen(pSSListenParam);
            if (listen.longValue() < -1) {
                log.error("NET_ESS_StartListen failed, error code:" + hceHomeSS.NET_ESS_GetLastError());
                hceHomeCMS.NET_ECMS_Fini();
                return false;
            }
            return true;
        }

        /**
         * 注册模块初始化
         *
         * @return
         */
        private boolean ecmsInit() {
            // CMS注册模块初始化
            boolean binit = hceHomeCMS.NET_ECMS_Init();

            if (false == binit) {
                log.error("HCEHomeCMS init error!");
                return false;
            }

            // 注册监听参数
            if (fRegisterCallBack == null) {
                fRegisterCallBack = new FRegisterCallBack();
            }

            hceHomeCMS.NET_ECMS_SetLogToFile(3, "/home/gentel/EhomePicServer/", false);
            HCEHomeCMS.NET_EHOME_CMS_LISTEN_PARAM struCMSListenPara = new HCEHomeCMS.NET_EHOME_CMS_LISTEN_PARAM();
            struCMSListenPara.struAddress.szIP = serverIp.getBytes();
            struCMSListenPara.struAddress.wPort = serverPort;
            struCMSListenPara.fnCB = fRegisterCallBack;

            //启动监听，接收设备注册信息
            NativeLong lListen = hceHomeCMS.NET_ECMS_StartListen(struCMSListenPara);
            if (lListen.longValue() < -1) {
                log.error("NET_ECMS_StartListen failed, error code:" + hceHomeCMS.NET_ECMS_GetLastError());
                hceHomeCMS.NET_ECMS_Fini();
                return false;
            }
            log.info("ET_ECMS_StartListen启动注册监听成功!");
            return true;
        }

        /**
         * 监听回调
         */
        class FRegisterCallBack implements HCEHomeCMS.DEVICE_REGISTER_CB {
            public boolean invoke(NativeLong lUserID, int dwDataType, Pointer pOutBuffer, int dwOutLen, HCEHomeCMS.NET_EHOME_SERVER_INFO pInBuffer, int dwInLen, Pointer pUser) {
                // 设备上线
                if (EhomeRegisterEnum.DEV_ON.getStatus().equals(dwDataType)) {
                    HCEHomeCMS.NET_EHOME_DEV_REG_INFO strDevRegInfo = getNetEhomeDevRegInfo(pOutBuffer, pInBuffer, dwInLen);
                    ehomeSessionManage.bindSession(lUserID, new String(strDevRegInfo.byDeviceID).trim());
                    log.info("设备登陆:id-{}", new String(strDevRegInfo.byDeviceID).trim());
                } else if (EhomeRegisterEnum.DEV_OFF.getStatus().equals(dwDataType)) {
                    ehomeSessionManage.unBindSession(lUserID);
                    log.info("设备掉线:id-{}", lUserID);
                } else if (EhomeRegisterEnum.DEV_ADDRESS_CHANGED.getStatus().equals(dwDataType)) {
                    HCEHomeCMS.NET_EHOME_DEV_REG_INFO strDevRegInfo = getNetEhomeDevRegInfo(pOutBuffer, pInBuffer, dwInLen);
                    ehomeSessionManage.bindSession(lUserID, new String(strDevRegInfo.byDeviceID).trim());
                    ehomeSessionManage.reBindSession(lUserID, new String(strDevRegInfo.byDeviceID).trim());
                    log.info("设备地址发生变化:id-{}", lUserID);
                } else if (EhomeRegisterEnum.UNKNOWN.getStatus().equals(dwDataType)) {
                    log.info("未知消息:id-{}", lUserID);
                } else {
                    log.info("接收到未知的消息回调类型:id-{} type{}", lUserID, dwDataType);
                }
                return true;
            }

            private HCEHomeCMS.NET_EHOME_DEV_REG_INFO getNetEhomeDevRegInfo(Pointer pOutBuffer, HCEHomeCMS.NET_EHOME_SERVER_INFO pInBuffer, int dwInLen) {
                HCEHomeCMS.NET_EHOME_DEV_REG_INFO strDevRegInfo = new HCEHomeCMS.NET_EHOME_DEV_REG_INFO();
                strDevRegInfo.write();
                Pointer pDevRegInfo = strDevRegInfo.getPointer();
                pDevRegInfo.write(0, pOutBuffer.getByteArray(0, strDevRegInfo.size()), 0, strDevRegInfo.size());
                strDevRegInfo.read();

                pInBuffer.dwSize = pInBuffer.size();
                byte[] byUdpIP = udpAlarmIp.getBytes();
                System.arraycopy(byUdpIP, 0, pInBuffer.struUDPAlarmSever.szIP, 0, byUdpIP.length);
                pInBuffer.struUDPAlarmSever.wPort = udpAlarmPort;

                byte[] byTcpIP = tcpAlarmIp.getBytes();
                System.arraycopy(byTcpIP, 0, pInBuffer.struTCPAlarmSever.szIP, 0, byTcpIP.length);
                pInBuffer.struTCPAlarmSever.wPort = tcpAlarmPort;

                pInBuffer.dwAlarmServerType = 1; //报警服务器类型：0- 只支持UDP协议上报，1- 支持UDP、TCP两种协议上报

                pInBuffer.dwPicServerType = 0;//图片服务器类型图片服务器类型，1-VRB图片服务器，0-Tomcat图片服务,2-云存储3,3-KMS,4-EHome5.0存储协议

                byte[] byPictureServerIP = pictureServerIp.getBytes();
                System.arraycopy(byPictureServerIP, 0, pInBuffer.struPictureSever.szIP, 0, byPictureServerIP.length);
                pInBuffer.struPictureSever.wPort = pictureServerPort;
                pInBuffer.write();
                dwInLen = pInBuffer.size();
                return strDevRegInfo;
            }
        }

        /**
         * 报警回调
         */
        class EHomeMsgCallBack implements HCEHomeAlarm.EHomeMsgCallBack {

            public boolean invoke(NativeLong iHandle, HCEHomeAlarm.NET_EHOME_ALARM_MSG pAlarmMsg, Pointer pUser) {
                log.info(new String("EHomeMsgCallBack: ") + pAlarmMsg.dwAlarmType + "dwAlarmInfoLen:" + pAlarmMsg.dwAlarmInfoLen + "dwXmlBufLen:" + pAlarmMsg.dwXmlBufLen);
                if (pAlarmMsg.dwXmlBufLen != 0) {
                    HCEHomeAlarm.NET_EHOME_XML_DATA strXMLData = new HCEHomeAlarm.NET_EHOME_XML_DATA();
                    strXMLData.write();
                    Pointer pPlateInfo = strXMLData.getPointer();
                    pPlateInfo.write(0, pAlarmMsg.pXmlBuf.getByteArray(0, strXMLData.size()), 0, strXMLData.size());
                    strXMLData.read();

                    String strXML = new String(strXMLData.byXMLData);
                    log.info(strXML);
                }
                return true;
            }
        }

        /**
         * 信息回调函数(上报)
         */
        public class PSSMessageCallback implements HCEHomeSS.EHomeSSMsgCallBack {

            public boolean invoke(NativeLong iHandle, int enumType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer,
                                  int dwInLen, Pointer pUser) {
                if (EhomeAlarmMsgEnum.EHOME_ALARM.getStatus().equals(enumType)) {
                    HCEHomeSS.NET_EHOME_SS_TOMCAT_MSG strTomcatMsg = new HCEHomeSS.NET_EHOME_SS_TOMCAT_MSG();
                    strTomcatMsg.write();
                    Pointer pTomcatMsg = strTomcatMsg.getPointer();
                    pTomcatMsg.write(0, pOutBuffer.getByteArray(0, strTomcatMsg.size()), 0, strTomcatMsg.size());
                    strTomcatMsg.read();
                    String str = new String(strTomcatMsg.szDevUri).trim();
                    log.info("NET_EHOME_SS_TOMCAT_MSG [szDevUri]：" + str + " strTomcatMsg size:" + strTomcatMsg.size());
                }
                return true;
            }
        }

        /**
         * 文件存储回调
         */
        public class PSSStorageCallback implements HCEHomeSS.EHomeSSStorageCallBack {

            public boolean invoke(NativeLong iHandle, String pFileName, Pointer pFileBuf, int dwFileLen, Pointer pFilePath, Pointer pUser) {
                String strPath = "/home/gentel/EhomePicServer/";
                String strFilePath = strPath + pFileName;

                //判断文件路径是否存在,不存在创建文件夹
                    /*
                   if (Directory.Exists(strPath) == false)
                    {
                        Directory.CreateDirectory(strPath);
                        if (Directory.Exists(strPath) == false)
                        {
                            return false;
                        }
                    }
                    */
                if (dwFileLen > 0 && pFileBuf != null) {
                    FileOutputStream fout;
                    try {
                        fout = new FileOutputStream(strFilePath);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = pFileBuf.getByteBuffer(offset, dwFileLen);
                        byte[] bytes = new byte[dwFileLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        log.error(e.getMessage());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        log.error(e.getMessage());
                    }
                }

                pFilePath.write(0, strFilePath.getBytes(), 0, strFilePath.getBytes().length);

                return true;
            }
        }

    }
}