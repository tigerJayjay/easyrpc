package com.tiger.easyrpc.core.function;

@FunctionalInterface
public interface ScanConsumer<A,B,C>{
    void accept(A scanClassName,B sourceName,C resolveType);
}