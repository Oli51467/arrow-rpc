package com.sdu.irpc.framework.core.registration.impl;

import com.sdu.irpc.framework.common.entity.ZooKeeperNode;
import com.sdu.irpc.framework.common.exception.DiscoveryException;
import com.sdu.irpc.framework.common.util.NetUtil;
import com.sdu.irpc.framework.common.util.ZookeeperUtil;
import com.sdu.irpc.framework.core.IRpcBootstrap;
import com.sdu.irpc.framework.core.config.ServiceConfig;
import com.sdu.irpc.framework.core.registration.AbstractRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.List;

import static com.sdu.irpc.framework.common.constant.ZooKeeperConstant.getPath;
import static com.sdu.irpc.framework.common.constant.ZooKeeperConstant.getProviderNodePath;

@Slf4j
public class ZooKeeperRegistry extends AbstractRegistry {

    private ZooKeeper zooKeeper;

    public ZooKeeperRegistry() {
        this.zooKeeper = ZookeeperUtil.createZookeeperConnection();
    }

    public ZooKeeperRegistry(String connectionAddr, int timeout) {
        this.zooKeeper = ZookeeperUtil.createZookeeperConnection(connectionAddr, timeout);
    }

    @Override
    public void register(ServiceConfig<?> service) {
        // 服务名称的节点 最后一层是方法的全类名
        String parentNode = getProviderNodePath(service.getInterface().getName());
        // 创建父节点
        if (!ZookeeperUtil.exists(zooKeeper, parentNode, null)) {
            ZooKeeperNode node = new ZooKeeperNode(parentNode, null);
            ZookeeperUtil.createNode(zooKeeper, node, null, CreateMode.PERSISTENT);
        }
        // 创建临时本机节点
        String finalNodePath = getPath(parentNode, NetUtil.getIp(IRpcBootstrap.getInstance().getConfiguration().getPort()));
        if (!ZookeeperUtil.exists(zooKeeper, finalNodePath, null)) {
            ZooKeeperNode node = new ZooKeeperNode(finalNodePath, null);
            ZookeeperUtil.createNode(zooKeeper, node, null, CreateMode.EPHEMERAL);
        }
        log.info("服务{}注册成功", service.getInterface().getName());
    }

    /**
     * 从注册中心拉取合适的服务列表
     * @param serviceName 服务名称
     * @return 服务列表
     */
    @Override
    public List<InetSocketAddress> discover(String serviceName) {
        // 找到服务对应的节点
        String serviceNode = getProviderNodePath(serviceName);
        // 从zk中获取他的子节点
        List<String> children = ZookeeperUtil.getChildren(zooKeeper, serviceNode, null);
        // 获取了所有的可用的服务列表
        List<InetSocketAddress> inetSocketAddresses = children.stream().map(address -> {
            String[] ipAndPort = address.split(":");
            String ip = ipAndPort[0];
            int port = Integer.parseInt(ipAndPort[1]);
            return new InetSocketAddress(ip, port);
        }).toList();

        if (inetSocketAddresses.size() == 0) {
            throw new DiscoveryException("未发现可用的服务主机");
        }
        return inetSocketAddresses;
    }
}