package net.tbu.feign.client.dynamic;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yueds
 */
@Component
@Slf4j
public class DynamicFeignClient {

    @Resource
    private DynamicFeignClientFactory<DynamicFeignService> dynamicFeignClientFactory;

    /**
     * 通过域名方式调用post请求
     *
     * @param domain 服务域名 eg: 127.0.0.1:8080
     * @param url    接口路径 eg: /create
     * @param params 入参
     */
    public Object executePostDomainApi(String domain, String url, Object params) {
        try {
            DynamicFeignService dynamicFeignService = dynamicFeignClientFactory.getDomainFeignClient(DynamicFeignService.class, domain);
            return dynamicFeignService.executePostApi(url, params);
        } catch (Exception e) {
            log.error("executePostDomainApi invoke out interface：{} return error:{}", domain + url, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 通过域名方式调用post请求,带请求头
     *
     * @param domain  服务域名 eg: 127.0.0.1:8080
     * @param url     接口路径 eg: /create
     * @param params  入参
     * @param headers 请求头
     */
    public Object executePostDomainApi(String domain, String url, Object params, HttpHeaders headers) {
        DynamicFeignService dynamicFeignService = dynamicFeignClientFactory.getDomainFeignClient(DynamicFeignService.class, domain);
        return dynamicFeignService.executePostApi(url, params, headers);
    }

    /**
     * 通过域名方式调用post请求,带请求头
     * 返回数据格式为 [application/octet-stream]
     *
     * @param domain
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public Response executePostDomainApiByStream(String domain, String url, Object params, HttpHeaders headers) {
        DynamicFeignService dynamicFeignService = dynamicFeignClientFactory.getDomainFeignClient(DynamicFeignService.class, domain);
        return dynamicFeignService.executePostApiByStream(url, params, headers);
    }

    /**
     * 通过域名方式调用get请求
     *
     * @param domain 服务域名 eg: 127.0.0.1:8080
     * @param url    接口路径 eg: /health
     * @param params 入参，map接口，没有参数输入空map，而不是null
     */
    public Object executeGetDomainApi(String domain, String url, Object params) {
        try {
            DynamicFeignService dynamicFeignService = dynamicFeignClientFactory.getDomainFeignClient(DynamicFeignService.class, domain);
            return dynamicFeignService.executeGetApi(url, params);
        } catch (Exception e) {
            log.error("executeGetDomainApi invoke out interface：{} return error:{}", domain + url, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 通过服务名调用post
     *
     * @param feignName 服务名称 eg：c66-game-gateway
     * @param url       接口路径 eg: /create
     * @param params    入参
     * @return
     */
    public Object executePostApi(String feignName, String url, Object params) {
        try {
            DynamicFeignService dynamicService = dynamicFeignClientFactory.getFeignClient(DynamicFeignService.class, feignName);
            return dynamicService.executePostApi(url, params);
        } catch (Exception e) {
            log.error("executePostApi invoke out interface：{} return error:{}", feignName + url, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 通过服务名调用get
     *
     * @param feignName 服务名称 eg：c66-game-gateway
     * @param url       接口路径 eg: /health
     * @param params    入参，map接口，没有参数输入空map，而不是null
     */
    public Object executeGetApi(String feignName, String url, Object params) {
        try {
            DynamicFeignService dynamicService = dynamicFeignClientFactory.getFeignClient(DynamicFeignService.class, feignName);
            return dynamicService.executeGetApi(url, params);
        } catch (Exception e) {
            log.error("executeGetApi invoke out interface：{} return error:{}", feignName + url, e.getMessage(), e);
            return null;
        }
    }
}
