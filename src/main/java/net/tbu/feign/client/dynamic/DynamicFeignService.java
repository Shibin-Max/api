package net.tbu.feign.client.dynamic;

import feign.Response;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


/***
 *  通用feign 调用方式
 */
public interface DynamicFeignService {

    @PostMapping("{url}")
    Object executePostApi(@PathVariable("url") String url, @RequestBody Object params);

    @GetMapping("{url}")
    Object executeGetApi(@PathVariable("url") String url, @SpringQueryMap Object params);

    @PostMapping("{url}")
    Object executePostApi(@PathVariable("url") String url, @RequestBody Object params, @RequestHeader HttpHeaders headers);

    @PostMapping("{url}")
    Response executePostApiByStream(@PathVariable("url") String url, @RequestBody Object params, @RequestHeader HttpHeaders headers);

    @GetMapping("{url}")
    Object executeGetApi(@PathVariable("url") String url, @RequestBody Object params, @RequestHeader HttpHeaders headers);
}
