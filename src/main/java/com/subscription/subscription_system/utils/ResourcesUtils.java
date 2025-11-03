package com.subscription.subscription_system.utils;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class ResourcesUtils {

    private ResourcesUtils() {
        super();
    }

    public static String getResourceContent(Resource resource) throws IOException
    {
        InputStream inputStream = resource.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder fileContent= new StringBuilder();
        String line;
        while ((line = bufferedReader
                .readLine()) != null) {

            fileContent.append(line);
        }
        bufferedReader.close();
        return fileContent.toString();
    }
}
