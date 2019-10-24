package com.fangle.hikvision;

import com.fangle.hikvision.ehome.EhomeServer;
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

    @Value("${ehome.port}")
    private int port = 7660;

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

    @Bean(initMethod = "start")
    public EhomeServer ehomeServer(){
        return new EhomeServer(port);
    }
}