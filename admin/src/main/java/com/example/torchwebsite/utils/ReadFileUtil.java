package com.example.torchwebsite.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

//  读取文件的工具类
public class ReadFileUtil {
    public String readFileToString(InputStream in, Charset charset) throws IOException {
        StringBuilder resultString = new StringBuilder();
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            resultString.append(new String(buf, 0, len, charset));
        }
        in.close();
        return resultString.toString();
    }
}
