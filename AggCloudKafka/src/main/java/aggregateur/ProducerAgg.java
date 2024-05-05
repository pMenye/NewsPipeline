package aggregateur;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import static utils.HttpConnector.*;
import static utils.StringUtils.*;

public class ProducerAgg{

    public static void main(final String[] args) throws IOException {
        // Ordonnanceur
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
          while(true){
            service.scheduleAtFixedRate(new CategoryTask("sports"), 0, 10, TimeUnit.SECONDS);
            service.scheduleAtFixedRate(new CategoryTask("business"), 0 , 15, TimeUnit.SECONDS);
            service.scheduleAtFixedRate(new CategoryTask("science"), 0, 23, TimeUnit.SECONDS);
            service.scheduleAtFixedRate(new CategoryTask("technology"), 0 , 10, TimeUnit.SECONDS);
            service.scheduleAtFixedRate(new CategoryTask("health"), 0, 18, TimeUnit.SECONDS);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Shutdown the executor service
            service.shutdown();
        }}
}

