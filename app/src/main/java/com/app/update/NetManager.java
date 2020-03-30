package com.app.update;

import java.io.File;

public interface NetManager {

    void download(String url, File file, NetdownloadCallBack netdownloadCallBack,Object tag);

    void cancel(Object tag);
}
