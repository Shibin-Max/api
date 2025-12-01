package net.tbu.http;

import cn.hutool.http.HttpException;
import lombok.extern.slf4j.Slf4j;

import static net.tbu.common.utils.SleepUtils.sleep;

@Slf4j
public final class HttpInvoker {

    private HttpInvoker() {
    }

    /**
     * @param maxRetry int
     * @param interval int
     * @param callable HttpCallable
     * @return T <T>
     * @throws HttpException he
     */
    public static <T> T retryHttpRequest(int maxRetry, int interval, HttpCallable<T> callable)
            throws HttpException {
        return retryHttpRequest("", "", maxRetry, interval, callable);
    }


    /**
     * @param caller   [String] 调用者-用于日志记录
     * @param callName [String] 调用名称-用于日志记录
     * @param maxRetry [int] 最大重试次数
     * @param interval [int] 重试时间间隔
     * @param callable [HttpCallable] 调用函数
     * @return T HttpCallable调用后返回的HttpBody
     * @throws HttpException he
     */
    public static <T> T retryHttpRequest(String caller, String callName,
                                         int maxRetry, int interval,
                                         HttpCallable<T> callable) throws HttpException {
        T rtnData = null;
        // 接口调用时, 最大重试次数10
        int retry = 0;
        do {
            try {
                /// 调用厅方API接口
                rtnData = callable.call();
                /// 查询成功, 中断LOOP
                if (rtnData != null) break;
                sleep(interval);
            } catch (Exception e) {
                /// 出现异常或无法获取到数据时, 输出日志并在可能的情况下重试指定次数后, 结束执行流程
                log.error("{} call {} has exception: {}, retry: {}/{}", caller, callName, e.getMessage(), retry, maxRetry, e);
                if (retry >= maxRetry) {
                    log.error("{} EXECUTION TERMINATED CAUSE {}", caller, e.getMessage(), e);
                    if (e instanceof InterruptedException)
                        Thread.currentThread().interrupt();
                    throw new HttpException(caller + " request " + callName + " has " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
                }
                sleep(interval);
            }
        } while (retry++ < maxRetry);
        if (rtnData == null)
            log.error("{} HttpRequest {} DATA_NOT_FOUND", caller, callName);
        return rtnData;
    }

}
