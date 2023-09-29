package com.takwolf.android.jsbridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ServiceHandler {
    void onCall(@Nullable String params, @NonNull PromiseExecutor executor);
}
