package com.ubo.iptv.hystrix;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * @Author: xuning
 * @Date: 2018/10/15
 */
public final class DelegatingRequestAttributesCallable<V> implements Callable<V> {

    private final Callable<V> delegate;
    private final RequestAttributes delegateRequestAttributes;
    private RequestAttributes originalRequestAttributes;

    public DelegatingRequestAttributesCallable(Callable<V> delegate, RequestAttributes delegateRequestAttributes) {
        this.delegate = delegate;
        this.delegateRequestAttributes = delegateRequestAttributes;
    }

    public DelegatingRequestAttributesCallable(Callable<V> delegate) {
        this(delegate, RequestContextHolder.getRequestAttributes());
    }

    @Override
    public V call() throws Exception {
        this.originalRequestAttributes = RequestContextHolder.getRequestAttributes();
        try {
            RequestContextHolder.setRequestAttributes(delegateRequestAttributes);
            return delegate.call();
        } finally {
            RequestContextHolder.setRequestAttributes(originalRequestAttributes);
            this.originalRequestAttributes = null;
        }
    }
}
