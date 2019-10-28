package com.fangle.hikvision.ehome;

import com.sun.jna.NativeLong;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class EhomeSessionManage {
    private final Map<NativeLong, String> ehomeDeviceChannelMap = new ConcurrentHashMap<>();

    /**
     * 绑定
     */
    public void bindSession(NativeLong userId, String deviceId) {
        ehomeDeviceChannelMap.put(userId, deviceId);
    }

    /**
     * 解邦
     */
    public void unBindSession(NativeLong userId) {
        if (hasLogin(userId)) {
            ehomeDeviceChannelMap.remove(userId);
        }
    }

    /**
     * 是否login
     */
    public boolean hasLogin(NativeLong userId) {
        return ehomeDeviceChannelMap.containsKey(userId);
    }

    /**
     * 获取设备Ip
     */
    public String getUserId(NativeLong userId) {
        return ehomeDeviceChannelMap.get(userId);
    }

    public void reBindSession(NativeLong userId, String deviceId){
        unBindSession(userId);
        bindSession(userId, deviceId);
    }
}
