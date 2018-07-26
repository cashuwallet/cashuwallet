package com.cashuwallet.android;

public interface Lambda<A, B> {
    B apply(A a);
}
