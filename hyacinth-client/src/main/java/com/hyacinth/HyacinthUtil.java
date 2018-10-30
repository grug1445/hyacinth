package com.hyacinth;

import com.hyacinth.exception.HyacinthException;

import java.net.InetAddress;
import java.util.stream.Stream;

/**
 * Created by feichen on 2018/10/29.
 */
public class HyacinthUtil {

    public static String pathParse(String appServiceMethodName, String localHost) {
        if (appServiceMethodName == null) {
            throw new HyacinthException("appServiceMethodName should not be null");
        }
        try {
            String[] strings = appServiceMethodName.split("#");
            if (strings.length == 0) {
                throw new HyacinthException("pathParse error, appServiceMethodName error");
            }
            // /canna/FullBackActivityBalanceService/createActivityBalance/192.168.1.1
            if (localHost == null) {
                return "/" + Stream.of(strings).reduce((s, s2) -> s + "/" + s2).orElse(null);
            } else {
                return "/" + Stream.of(strings).reduce((s, s2) -> s + "/" + s2).orElse(null) + "/" + localHost;
            }
        } catch (Exception e) {
            throw new HyacinthException("pathParse error", e);
        }
    }

    public static String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new HyacinthException("getLocalHost error", e);
        }
    }

}
