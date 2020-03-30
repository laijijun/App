package com.app.update;

import java.io.File;

public interface NetdownloadCallBack {
    void success(File apkFile);

    void progress(int progress);

    void failed(Throwable throwable);
}
