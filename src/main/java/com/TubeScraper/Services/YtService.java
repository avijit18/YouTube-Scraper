package com.TubeScraper.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface YtService {

    String extractVideoId(String videoId);

    JsonNode getVideoDetails(String videoId) throws JsonProcessingException;

}
