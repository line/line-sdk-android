package com.linecorp.linesdk.message;

public interface Stringable {
    default String name() {
        return "Stringable";
    }
}
