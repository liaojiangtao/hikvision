package com.fangle.hikvision;

import akka.actor.ActorSystem;
import com.fangle.hikvision.ehome.EhomeServer;
import com.fangle.hikvision.ehome.EhomeSessionManage;
import com.fangle.hikvision.ehomeSdk.HCEHomeAlarm;
import com.fangle.hikvision.ehomeSdk.HCEHomeCMS;
import com.fangle.hikvision.ehomeSdk.HCEHomeSS;
import com.fangle.hikvision.ehomeSdk.HCEHomeStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @author Gentel
 * @description 配置文件
 * @create 2019-10-23 16:14
 */
public class EhomeConfig {

    @Value("${ehome.serverIp}")
    private String serverIp = "127.0.0.1";

    @Value("${ehome.serverPort}")
    private Short serverPort = 7660;

    @Value("${ehome.udpAlarmServerIp}")
    private String udpAlarmServerIp = "127.0.0.1";

    @Value("${ehome.udpAlarmServerPort}")
    private Short udpAlarmServerPort = 7200;

    @Value("${ehome.tcpAlarmServerIp}")
    private String tcpAlarmServerIp = "127.0.0.1";

    @Value("${ehome.tcpAlarmServerPort}")
    private Short tcpAlarmServerPort = 7280;

    @Value("${ehome.pictureServerIp}")
    private String pictureServerIp = "127.0.0.1";

    @Value("${ehome.pictureServerPort}")
    private Short pictureServerPort = 8080;

    @Bean
    public HCEHomeCMS hceHomeCMS(){
        return HCEHomeCMS.INSTANCE;
    }

    @Bean
    public HCEHomeStream hceHomeStream(){
        return HCEHomeStream.INSTANCE;
    }

    @Bean
    public HCEHomeSS hceHomeSS(){
        return HCEHomeSS.INSTANCE;
    }

    @Bean
    public HCEHomeAlarm hceHomeAlarm(){
        return HCEHomeAlarm.INSTANCE;
    }


    @Bean
    public EhomeSessionManage sessionManage() {
        return new EhomeSessionManage();
    }

    @Bean(initMethod = "start")
    public EhomeServer ehomeServer(){
        return new EhomeServer(serverIp, serverPort, udpAlarmServerIp, udpAlarmServerPort, tcpAlarmServerIp, tcpAlarmServerPort,
                pictureServerIp, pictureServerPort);
    }
}