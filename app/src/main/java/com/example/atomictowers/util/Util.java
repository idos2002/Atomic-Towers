package com.example.atomictowers.util;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Util {

    private Util() {
    }

    @NonNull
    public static String readResourceFile(Context applicationContext, int resourceId) throws IOException {
        InputStream inputStream = applicationContext.getResources().openRawResource(resourceId);

        StringBuilder buffer = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        }

        return buffer.toString();
    }
}
