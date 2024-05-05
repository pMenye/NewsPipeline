package aggregateur;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;


class CategoryTask implements  Runnable {
    private String categoryName;
    public CategoryTask(String categoryName) {
        this.categoryName = categoryName;
        System.out.println("==test passed=====");
    }

    private static String ACCESS_KAFKA_INFO = "src/main/resources/Acces-kafka-info.properties";
    private static String ACCESS_NEW_API_INFO = "src/main/resources/Acces-Api-Info.properties";



    public static String fetchApiKey() {
        Properties properties = loadPropertiesFromInfoAcces(ACCESS_NEW_API_INFO);
        return properties.getProperty("apiKey");
    }

    public static String baseUrl() {
        Properties properties = loadPropertiesFromInfoAcces(ACCESS_NEW_API_INFO);
        return properties.getProperty("baseUrl");
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

    public static HttpURLConnection connectTo(URL url) throws IOException, ProtocolException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }


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

    public static  String fetchAllArticlesByCountryAndTopic(String country, String onTopic) {

        final String NEWS_URL = baseUrl();

        final String API_KEY = fetchApiKey();

        final URL url = createUrl(NEWS_URL, API_KEY, country, onTopic );

        String responseAsString = "";

        if (onTopic.isEmpty() || country.isEmpty()) {
            System.out.println("==test passed=====1");
            throw new IllegalArgumentException("Params should not be empty.");
        } else {
            try {
                HttpURLConnection connection = connectTo(url);
                System.out.println("  connection "+connection);
                int responsecode = connection.getResponseCode();

                if (responsecode != 200) {
                    System.out.println("==test passed=====2");
                    throw new IllegalStateException("Request failed - response code: " + responsecode);
                } else {

                    responseAsString = readFrom(url);
                    System.out.println("==test passed=====3");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseAsString;
    }

    @Override
    public void run() {
        try {
            synchronized (this){

            final Properties props = loadPropertiesFromInfoAcces(ACCESS_KAFKA_INFO);

            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();

            String dataApi = null;

            if (categoryName == "business") {
                dataApi = fetchAllArticlesByCountryAndTopic("us", "business");
            }
            if (categoryName == "sports") {

                dataApi = fetchAllArticlesByCountryAndTopic("us", "sports");
            }
            if (categoryName == "science") {
                dataApi = fetchAllArticlesByCountryAndTopic("us", "science");
            }
            if (categoryName == "health") {
                dataApi = fetchAllArticlesByCountryAndTopic("us", "health");
            }
            if (categoryName == "technology") {
                dataApi = fetchAllArticlesByCountryAndTopic("us", "technology");
            }

            // Créer un objet ObjectMapper pour parser le JSON
            ObjectMapper objectMapper = new ObjectMapper();

            // Parser le contenu JSON
            JsonNode jsonNode = objectMapper.readTree(dataApi);

            //System.out.println(jsonNode);

            JsonNode itemArticles = jsonNode.get("articles");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
            for (int i = 0; i < 10; i++) {
                JsonNode itemArticle = itemArticles.get(i);

                dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
                ZonedDateTime now = ZonedDateTime.now();

                String idItemArticle = "ID_"+categoryName + randomUUIDString;
                String author = itemArticle.get("author").asText();
                String name = itemArticle.get("source").get("name").asText();
                String category = categoryName;
                String title = itemArticle.get("title").asText();
                String description = itemArticle.get("description").asText();
                String url = itemArticle.get("url").asText();
                String urlToImage = itemArticle.get("urlToImage").asText();
                String publishedAt = dtf.format(now).toString();
                String content = itemArticle.get("content").asText();


                ItemArticle art = null;
                if (urlToImage != null && content != null && author != null)
                    art = new ItemArticle(idItemArticle, name, category, author, title, description, url, urlToImage, publishedAt, content);

                if(art != null){
                try (final Producer<String, String> producer = new KafkaProducer<>(props)) {
                    String user = "tpalt";
                    producer.send(
                            new ProducerRecord<>(categoryName, user, art.toString()),
                            (event, ex) -> {
                                if (ex != null)
                                    ex.printStackTrace();
                                else {
                                    System.out.println("=============================================================");
                                    System.out.printf("Produced event to topi " + categoryName, user);
                                }
                            });
                }


                System.out.println(art);
            }}}

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
