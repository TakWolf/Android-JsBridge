package com.takwolf.android.jsbridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ErrorHandler {
    void onNoService(@NonNull String name, @Nullable String params, @NonNull PromiseExecutor executor);

    void onException(@NonNull String name, @Nullable String params, @NonNull Throwable e, @NonNull PromiseExecutor executor);
}
