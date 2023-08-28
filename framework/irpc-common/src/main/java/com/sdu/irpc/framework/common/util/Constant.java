package com.sdu.irpc.framework.common.util;

public class Constant {

    // zookeeper的默认连接地址
    public static final String DEFAULT_ZK_CONNECT = "127.0.0.1:2181";

    // zookeeper默认的超时时间
    public static final int TIME_OUT = 10000;

    // 服务提供方和调用方在注册中心的基础路径
    public static final String BASE_PATH = "/irpc-metadata";
    public static final String BASE_PROVIDERS_PATH = "/providers";
    public static final String BASE_CLIENTS_PATH = "/clients";

    public static String BaseProvidersPath() {
        return BASE_PATH + BASE_PROVIDERS_PATH;
    }

    public static String BaseClientsPath() {
        return BASE_PATH + BASE_CLIENTS_PATH;
    }
}
