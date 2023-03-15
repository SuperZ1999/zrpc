package com.zmy.zrpc.core.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.zmy.zrpc.common.enumeration.RpcError;
import com.zmy.zrpc.common.exception.RpcException;
import com.zmy.zrpc.common.util.NacosUtil;
import com.zmy.zrpc.core.loadbalancer.LoadBalancer;
import com.zmy.zrpc.core.loadbalancer.RandomLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private LoadBalancer loadBalancer;

    public NacosServiceDiscovery() {
        this.loadBalancer = new RandomLoadBalancer();
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstances(serviceName);
            if(instances.size() == 0){
                logger.error("找不到对应服务：" + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生" + e);
            throw new RpcException(RpcError.LOOKUP_SERVICE_FAILED);
        }
    }
}
