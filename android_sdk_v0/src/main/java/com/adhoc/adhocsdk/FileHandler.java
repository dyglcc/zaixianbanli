package com.adhoc.adhocsdk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.content.Context;

import com.adhoc.utils.T;

/**
 * This class provides a basic function to fetch/write a file in Android.
 */
public class FileHandler {

    public static final String FILENAME = "increment_cache_file";
    // single
    private static FileHandler instance;

    public static FileHandler getInstance() {
        if (instance == null) {
            instance = new FileHandler();
        }
        return instance;
    }

    private FileHandler() {


    }
    public void clearFileContent(Context mContext,String fileName){
        File file = new File(mContext.getFilesDir(), fileName);
        if(file.exists()){
            file.delete();
        }
        T.i("清除文件缓存文件");
    }

    /*
     * Read content from file. Returns null if it fails to load.
     */
    public ArrayList<String> readCacheLines(Context mContext, String fileName) {
        ArrayList<String> list = new ArrayList<String>();
        File file = checkExist(mContext, fileName);
        try {
            FileInputStream inputStream = new FileInputStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {

                list.add(line);
                T.i(line);

            }
            reader.close();

        } catch (FileNotFoundException e) {
            T.e(e);
        } catch (IOException e) {
            T.w("Fails to read file: " + fileName);
            mContext.deleteFile(fileName);
        }catch (Throwable ex){
            T.e(ex);
        }
        return list;
    }

    private File checkExist(Context mContext, String fileName) {
        File file = new File(mContext.getFilesDir(), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                T.e(e);
            }
        }
        return file;
    }

    /*
     * Read content from file. Returns null if it fails to load.
     */
    public String readFile(Context mContext, String fileName, int maxLen) {
        String content = null;
        try {
            FileInputStream inputStream = mContext.openFileInput(fileName);
            byte[] buffer = new byte[maxLen];
            int readLen = inputStream.read(buffer, 0, maxLen);
            if (readLen <= 0 || inputStream.available() > 0) {
                T.i("Either file (" + fileName + ") is too long or it is corrupted.");
                inputStream.close();
                mContext.deleteFile(fileName);
            } else {
                content = new String(buffer, 0, readLen);
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            T.e(e);
        } catch (IOException e) {
            T.w("Fails to read file: " + fileName);
            mContext.deleteFile(fileName);
        }
        return content;
    }


    /*
     * Writes a string into a file. If failed, return false.
     */
    public boolean writeFile(Context mContext, String fileName, String data) {
        checkExist(mContext, fileName);
        try {
            FileOutputStream outStream =
                    mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            outStream.write(data.getBytes());
            outStream.close();
            return true;
        } catch (FileNotFoundException e) {
            T.w("File Not Found Error. Failed to create one.");
            return false;
        } catch (IOException e) {
            T.w("Failed to write to file.");
            return false;
        }
    }

    /*
     * Writes a string into a file. If failed, return false.
     */
    public synchronized boolean writeCacheLines(Context mContext, String fileName, String data) {
        File file = checkExist(mContext, fileName);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath(), true)));
            writer.write(data);
            writer.close();
            T.i("write file data :" + data);
            return true;
        } catch (FileNotFoundException e) {
            T.w("File Not Found Error. Failed to create one.");
            return false;
        } catch (IOException e) {
            T.w("Failed to write to file.");
            return false;
        }catch (Throwable e){
            T.e(e);
            return false;
        }
    }

}
