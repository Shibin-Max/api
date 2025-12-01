package net.tbu.http;

import cn.hutool.http.HttpException;

import java.io.IOException;
import java.util.function.Supplier;

@FunctionalInterface
public interface HttpCallable<T> extends Supplier<T> {

    T call() throws IOException, InterruptedException;

    @Override
    default T get() {
        try {
            return call();
        } catch (IOException e) {
            throw new HttpException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpException(e.getMessage(), e);
        }
    }

}
