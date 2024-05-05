package utils;

import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Properties;


/**
 * 
 * 
 *
 */
public class HttpConnector {

    //private static String ACCESS_KAFKA_API_INFO = "resources/main/Acces-Api-Info.properties";
    //private static String ACCESS_KAFKA_API_INFO = "src/main/resources/Acces-Api-Info.properties";

    private static String ACCESS_NEW_API_INFO = "src/main/resources/Acces-Api-Info.properties";
    public static HttpURLConnection connectTo(URL url) throws IOException, ProtocolException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }

    public static String fetchApiKey() {
        Properties properties = utils.StringUtils.loadPropertiesFromInfoAcces(ACCESS_NEW_API_INFO);
        return properties.getProperty("apiKey");
    }

    public static String baseUrl() {
        Properties properties = utils.StringUtils.loadPropertiesFromInfoAcces(ACCESS_NEW_API_INFO);
        return properties.getProperty("baseUrl");
    }

    private static Properties loadPropertiesFromInfoAcces(String  path) {
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
