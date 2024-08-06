package com.TubeScraper.Services.IMPL;

import com.TubeScraper.Configs.YtConfig;
import com.TubeScraper.Services.YtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class YtServiceIMPL implements YtService {

    private final YtConfig ytConfig;

    @Override
    public String extractVideoId(String videoId) {

        Pattern UrlPatternOne = Pattern.compile("https?:\\/\\/(?:www|m)\\.youtube\\.com\\/watch\\?v=([a-zA-Z0-9_-]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcherOne = UrlPatternOne.matcher(videoId);

        Pattern UrlPatternTwo = Pattern.compile("youtu.be\\/(.{11})", Pattern.CASE_INSENSITIVE);
        Matcher matcherTwo = UrlPatternTwo.matcher(videoId);

        if (matcherOne.find()) {
            return matcherOne.group(1);
        } else if (matcherTwo.find()) {
            return matcherTwo.group(1);
        }
        return null;
    }

    @Override
    public JsonNode getVideoDetails(String videoId) throws JsonProcessingException {

        // 3rd party API integration using rest template
        String apiUrl = ytConfig.getApiUrl();
        String apiKey = ytConfig.getApiKey();
        String idParam = "id=" + videoId;
        // String partParam = "part=snippet";

        // Add other statistics to the part parameter
        String partParam = "part=snippet,statistics";


        String keyParam = "key=" + apiKey;

        String URL = apiUrl + "?" + partParam + "&" + idParam + "&" + keyParam;

        //String URL = apiUrl + "?" + apiKey + "&" + partParam + "&" + idParam;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(URL, String.class);

        // System.out.println(response);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);

        // Get the snippet and statistics nodes with other details(likes, views, comments...)
        JsonNode snippetNode = rootNode.path("items").get(0).path("snippet");
        JsonNode statisticsNode = rootNode.path("items").get(0).path("statistics");

        // Create a new JSON object to hold both snippet and statistics(likes, views, comments...)
        ObjectNode videoDetailsNode = mapper.createObjectNode();
        videoDetailsNode.set("snippet", snippetNode);
        videoDetailsNode.set("statistics", statisticsNode);

        return videoDetailsNode;
    }

}

