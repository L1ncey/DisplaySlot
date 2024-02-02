package dev.nevermind.baimao.Utils;

import java.io.Serializable;

public interface Callback<T> extends Serializable {
    void callback(T data);
}
