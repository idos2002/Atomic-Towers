package com.example.atomictowers.util;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Util {

    private Util() {
    }

    @NonNull
    public static String readResourceFile(Context applicationContext, int resourceId) throws IOException {
        InputStream inputStream = applicationContext.getResources().openRawResource(resourceId);
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

    @NonNull
    public static String readInternalStorageFile(@NonNull Context context,
                                                 @NonNull String filename) throws IOException {
        FileInputStream stringBuilder = context.openFileInput(filename);
        StringBuilder buffer = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stringBuilder))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }

        return buffer.toString();
    }

    public static boolean internalStorageFileExists(@NonNull Context context, @NonNull String filename) {
        File file = context.getFileStreamPath(filename);
        return file.exists();
    }

    public static void writeInternalStorageFile(@NonNull Context context, @NonNull String filename,
                                                @NonNull String data) throws IOException {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(data.getBytes());
        }
    }
}
