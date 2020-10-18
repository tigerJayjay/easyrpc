package com.tiger.easyrpc.core.function;

@FunctionalInterface
public interface ScanConsumer<A,B>{
    void accept(A scanClassName,B sourceName);
}