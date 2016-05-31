package com.adhoc.adhocsdk;

import android.content.Context;
import android.os.Environment;

import com.adhoc.utils.T;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by dongyuangui on 15-4-16.
 */
public class SDcardHandler {
    private static SDcardHandler instance;
    private Context mContext;

    private String getAdhocFileDir() {
//        File fileadhoc = new File(getSDPath()+"/Adhoc");
        String fileDir = null;
        if (hasSdcard()) {
            File fileadhoc = Environment.getExternalStoragePublicDirectory("Adhoc");
            if (!fileadhoc.exists()) {
                fileadhoc.mkdirs();
            }
            fileDir = fileadhoc.getAbsolutePath();
        }

        return fileDir;
    }

    private SDcardHandler(Context context) {

        mContext = context;
    }

    public static SDcardHandler getInstance(Context context) {

        if (instance == null) {
            instance = new SDcardHandler(context);
        }
        return instance;
    }

    // 从ADHOC_CLIENT_ID文件读取ClientID
    public String readFile() {

        String adhocdir = getAdhocFileDir();
        return readFile(adhocdir + "/" + AdhocConstants.FILE_CLIENT_ID);
    }

    /*
     * Read content from file. Returns null if it fails to load.
     */
    private String readFile(String dir) {
        String content = null;

        // 没有挂载可读写sdcard 返回空
        if (!hasSdcard()) {
            T.w("读存储卡失败 存储卡挂载状态 " + SDcardHandler.getInstance(mContext).hasSdcard());
            return null;
        }

        try {
            File file = new File(dir);
            if (!file.exists()) {
                return null;
            }

            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int readLen = inputStream.read(buffer, 0, 1024);
            if (readLen <= 0 || inputStream.available() > 0) {
                T.i("Either file (" + dir + ") is too long or it is corrupted.");
                inputStream.close();
            } else {
                content = new String(buffer, 0, readLen);
                inputStream.close();
            }
        } catch (IOException e) {
            T.w("Fails to read file: " + dir);
        } catch (Throwable e) {
            T.e(e);
        }
        return content;
    }


    // 从AdhocConstants.FILE_CLIENT_ID 中读取字符串
    public boolean writeFile(String data) {
        String adhocdir = getAdhocFileDir();
        return writeFile(adhocdir + "/" + AdhocConstants.FILE_CLIENT_ID, data);
    }

    /*
     * Writes a string into a file. If failed, return false.
     */
    private boolean writeFile(String dir, String data) {
        // 没有挂载sdcard 返回false
        if (!hasSdcard()) {
            T.w("写SDCARD文件失败！SDCARD 挂载状态 " + SDcardHandler.getInstance(mContext).hasSdcard());
            return false;
        }
        File newFile = new File(dir);
        if (newFile.exists()) {
            newFile.delete();
            T.i("删除文件" + dir);
        }

        try {
            newFile.createNewFile();
            FileOutputStream outStream = new FileOutputStream(newFile);
            outStream.write(data.getBytes());
            outStream.close();
            return true;
        } catch (Throwable e) {
            T.e(e);
            try {
                newFile.delete();
            } catch (Throwable ex) {
                T.e(ex);
            }
            return false;
        }
    }

    // 返回是否安装sdcard并且可以读写
    public boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        T.i("SDCARD 挂载状态 " + status);
        if (Environment.MEDIA_MOUNTED.equals(status)) {
            return true;
        } else {
            return false;
        }
    }

    // 返回sdcard 路径 mnt/sdcard
    public String getSDPath() {
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            File sdDirfile = Environment.getExternalStorageDirectory();//获取跟目录
            sdDir = sdDirfile.getAbsolutePath();
        }
        return sdDir;

    }


    /*
     * Read content from file. Returns null if it fails to load.
     */
    public ArrayList<String> readApps(String dir) {
        // 没有挂载可读写sdcard 返回空
        if (!hasSdcard()) {
            T.w("读存储卡失败 存储卡挂载状态 " + hasSdcard());
            return null;
        }
        ArrayList<String> list = null;
        try {
            File file = new File(getAdhocFileDir() + "/" + dir);
            if (!file.exists()) {
                return null;
            }


            list = new ArrayList<String>();

            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "utf8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String str;
            while ((str = reader.readLine()) != null) {
                list.add(str);
            }
            reader.close();
            inputStreamReader.close();
        } catch (Throwable e) {
            T.w("Fails to read file: " + dir);
        }
        return list;
    }

    /*
     * 写app package
     */
    public boolean writeFileAppend(String dir, String data) {
        // 没有挂载sdcard 返回false
        if (!hasSdcard()) {
            T.w("写SDCARD文件失败！SDCARD 挂载状态 " + hasSdcard());
            return false;
        }
        File newFile = new File(getAdhocFileDir() + "/" + dir);
        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (Throwable e) {
                T.e(e);
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile, true), "utf8"));
            writer.write(data);
            writer.close();
            return true;
        } catch (Throwable e) {
            T.e(e);
            return false;
        }
    }

    public String readEditString() {
        String adhocdir = getAdhocFileDir();
        return readFile(adhocdir + "/" + AdhocConstants.FILE_EDITING);
    }

    // 删除已有文件,写入内容.
    public boolean writeEditString(String data) {
        String adhocdir = getAdhocFileDir();
        return writeFile(adhocdir + "/" + AdhocConstants.FILE_EDITING, data);
    }
}
