package com.fangle.hikvision.ehome;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Gentel
 * @description ehome server
 * @create 2019-10-23 17:45
 */

@Slf4j
public class EhomeServer {

    private int port;

    public EhomeServer(int port){
        this.port = port;
    }

    public void start(){
        log.info("Ehome server bean init!");
    }
}