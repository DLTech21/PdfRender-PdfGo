package com.dl.pdfgo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.blankj.utilcode.util.ConvertUtils.bytes2HexString;
import static com.blankj.utilcode.util.StringUtils.isSpace;

/**
 * Created by donal on 2018/4/9.
 */

public class EncryptUtils {
    /**
     * SHA256加密文件
     *
     * @param filePath 文件路径
     * @return 文件的SHA256校验码
     */
    public static String encryptSHA256File2String(String filePath) {
        File file = isSpace(filePath) ? null : new File(filePath);
        return encryptSHA256File2String(file);
    }

    /**
     * SHA256加密文件
     *
     * @param file 文件
     * @return 文件的SHA256校验码
     */
    public static String encryptSHA256File2String(File file) {
        return bytes2HexString(encryptSHA256File(file));
    }

    /**
     * SHA256加密文件
     *
     * @param file 文件
     * @return 文件的SHA256校验码
     */
    public static byte[] encryptSHA256File(File file) {
        if (file == null) return null;
        FileInputStream fis = null;
        DigestInputStream digestInputStream;
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("SHA256");
            digestInputStream = new DigestInputStream(fis, md);
            byte[] buffer = new byte[256 * 1024];
            while (digestInputStream.read(buffer) > 0) ;
            md = digestInputStream.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
