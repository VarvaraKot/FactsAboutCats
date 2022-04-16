import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    private static final Type factsAboutCatsListType = new TypeToken<List<FactsAboutCats>>() {
    }.getType();

    public static void main(String[] args) {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            HttpGet request = new HttpGet("https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats");
            try (final CloseableHttpResponse response = httpClient.execute(request)) {
                List<FactsAboutCats> factsAboutCatsList = parseResponse(response);
                factsAboutCatsList.stream()
                        .filter(FactsAboutCats::hasUpvotes)
                        .forEach(System.out::println);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static CloseableHttpClient createHttpClient() {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
    }

    private static List<FactsAboutCats> parseResponse(CloseableHttpResponse response) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, factsAboutCatsListType);
    }
}
