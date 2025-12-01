package net.tbu.http;

import org.junit.jupiter.api.Test;

class HttpInvokerTest {

    @Test
    void retryHttpRequest() {

        HttpCallable<String> callable = () -> {
            throw new RuntimeException("TEST ERROR");
        };

        String data = HttpInvoker
                .retryHttpRequest("HttpInvokerTest", "test", 10, 1000, callable);

        System.out.println(data);

    }
}