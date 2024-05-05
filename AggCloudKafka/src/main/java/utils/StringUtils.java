package utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;
import java.io.FileReader;
import java.util.Properties;


/**
 * 
 *
 *
 */
public class StringUtils {


    public static URL createUrl(String baseURL, String apiKey, String country, String onTopic) {
        URL requiredURL = null;
        try {
            //https://newsapi.org/v2/top-headlines?country=us&category=sports
            requiredURL = new URL(baseURL
                    + "/top-headlines?country="+ country
                    + "&category="+ onTopic
                    + "&sortBy=popularity"
                    + "&apiKey=" + apiKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return requiredURL;
    }

    //UTILE 2
    public static String readFrom(URL url) throws IOException {
        String content = "";
        Scanner sc = new Scanner(url.openStream(), StandardCharsets.UTF_8);
        while (sc.hasNext()) {
            content += sc.nextLine();
            content.replaceAll("\n", "");
        }
        sc.close();
        return content;
    }

    public static Properties loadPropertiesFromInfoAcces(String path) {
        Properties properties = new Properties();
        try {
            FileReader reader = new FileReader(path);
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
