package com.takwolf.android.jsbridge;

import androidx.annotation.Nullable;

public interface PromiseExecutor {
    void resolve(@Nullable String result);

    void reject(@Nullable String error);
}
