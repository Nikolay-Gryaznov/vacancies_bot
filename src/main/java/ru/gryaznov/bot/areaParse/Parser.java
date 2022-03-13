package ru.gryaznov.bot.areaParse;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static ru.gryaznov.bot.constants.VarConstants.*;


//class for parse data about regions
public class Parser {

    public Areas parse() {

        Areas areas = new Areas();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.hh.ru/areas"))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            JSONArray obj = new JSONArray(json);

            for (int i = 0; i<obj.length(); i++) {
                org.json.JSONObject jsonObject = obj.getJSONObject(i);
                String name = (String) jsonObject.get(TAG_NAME);
                String id = (String) jsonObject.get(TAG_ID);
                areas.areaData.put(name.toLowerCase(), id.toLowerCase());
                JSONArray jsonArray1 = (JSONArray) jsonObject.get(TAG_AREAS);
                if (!jsonArray1.isEmpty()) {
                    for (int j = 0; j<jsonArray1.length(); j++) {
                        org.json.JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        String name1 = (String) jsonObject1.get(TAG_NAME);
                        String id1 = (String) jsonObject1.get(TAG_ID);
                        areas.areaData.put(name1.toLowerCase(), id1.toLowerCase());
                        JSONArray jsonArray2 = (JSONArray) jsonObject1.get(TAG_AREAS);
                        if (!jsonArray2.isEmpty()) {
                            for (int v = 0; v<jsonArray2.length(); v++) {
                                org.json.JSONObject jsonObject2 = jsonArray2.getJSONObject(v);
                                String name3 = (String) jsonObject2.get(TAG_NAME);
                                String id3 = (String) jsonObject2.get(TAG_ID);
                                areas.areaData.put(name3.toLowerCase(), id3.toLowerCase());
                            }
                        }
                    }

                }

            }
        } catch(IOException | InterruptedException e){
                e.printStackTrace();
            }
        return areas;
    }
}

