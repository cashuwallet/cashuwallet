package com.cashuwallet.android;

public interface Continuation<T> {
    void cont(T t);
}
