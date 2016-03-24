package com.example.lawrence.recommendationsapp.api;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


// Note: I have to convert everything to static (from non-static) because it is static in Etsy.java
public class MyProperties extends Activity {
    // look for the .properties files in the same directory as the .java file
    private final String[] fileList = {"/config.properties"};
    private Properties prop = new Properties();

    protected String getAPIKey(String name) {
        Properties prop = loadProp();
        String apiKey = prop.getProperty(name);
        return apiKey;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected Properties loadProp() {

        for (int i = fileList.length - 1; i >= 0; --i) {
            String file = fileList[i];

            try (InputStream inputStream = getAssets().open(file)) {
                prop.load(inputStream);
            } catch (FileNotFoundException fnfe) {
                System.out.printf("Ignoring missing property file: %s %n", file);
            } catch (IOException ioe) {
                System.out.printf("More general input/output exception: %s %n", ioe);
            }
        }
        return prop;
    }
} // end TestProperties

