package com.floow.josueherrero.floowapp.persistence;

import android.os.Environment;
import android.util.Log;

import com.floow.josueherrero.floowapp.ui.list.items.Path;
import com.floow.josueherrero.floowapp.utils.FileEncryptionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JosuéManuel on 05/07/2017.
 *
 * This is the repository for saving and loading the user path list in disk.
 */

public final class PathsRepositoryImpl implements PathsRepository {

    public final static String PATHS_FILE_NAME = "floow_path_list";
    public final static String ENCRYPTION_KEY = "94i4jmfnk48fjn85";

    @Override
    public void savePaths(final List<Path> pathList) {
        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + PATHS_FILE_NAME;

        BufferedWriter outputWriter = null;
        final String pathListString = new Gson().toJson(pathList);
        try{
            final String filesBytes = FileEncryptionUtil.crypto(ENCRYPTION_KEY, pathListString, false);
            outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
            outputWriter.write(filesBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputWriter !=null) {
                    outputWriter.close();
                }
            }catch (IOException e) {
                Log.d(PathsRepositoryImpl.class.getName(), e.toString());
            }
        }
    }

    @Override
    public List<Path> loadPaths() {
        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + PATHS_FILE_NAME;

        BufferedReader inputReader = null;
        final StringBuilder stringBuilder = new StringBuilder();
        String dataString= "";
        try {
            inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String line;
            while ((line = inputReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            dataString = FileEncryptionUtil.crypto(ENCRYPTION_KEY, stringBuilder.toString(), true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            try{
                if (inputReader !=null)
                    inputReader.close();
            }catch (IOException e) {
                Log.d(PathsRepositoryImpl.class.getName(), e.toString());
            }
        }

        final Type listType = new TypeToken<ArrayList<Path>>(){}.getType();
        return new Gson().fromJson(dataString, listType);
    }

    @Override
    public boolean exists() {
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + PATHS_FILE_NAME;
        final File file = new File(path);

        if (file.exists()){
            return true;
        }else{
            return false;
        }
    }

}