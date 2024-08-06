package com.TubeScraper.Controllers;

import com.TubeScraper.Services.IMPL.YtServiceIMPL;
import com.TubeScraper.Utils.numberFormatter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class YtController {

    private final YtServiceIMPL ytService;

    private final numberFormatter NumberFormatter;

    @GetMapping("/")
    public String getHome() {
        return "index";
    }

    @PostMapping("/processing")
    public String getData(@RequestParam String videoLink, Model model) {

        String videoId = ytService.extractVideoId(videoLink);

        if (videoId != null) {
            try {
                JsonNode videoDetails = ytService.getVideoDetails(videoId);
                JsonNode snippet = videoDetails.path("snippet");
                JsonNode statistics = videoDetails.path("statistics");

                String title = snippet.path("title").asText();
                String description = snippet.path("description").asText();
                String thumbURL = snippet.path("thumbnails").path("standard").path("url").asText();

                JsonNode tagsNode = snippet.path("tags");
                String[] tags = null;
                if (tagsNode.isArray()) {
                    tags = new ObjectMapper().treeToValue(tagsNode, String[].class);
                }

                // System.out.println(tags);


                // Data for other statistics-(likes, views, comments)
                int viewCount = statistics.path("viewCount").asInt();
                int likeCount = statistics.path("likeCount").asInt();
                int commentCount = statistics.path("commentCount").asInt();

                // convert the integers into some String data-(Likes-> 3M, Views- 12M....)
                String formattedViewCount = NumberFormatter.formatNumber(viewCount);
                String formattedLikeCount = NumberFormatter.formatNumber(likeCount);
                String formattedCommentCount = NumberFormatter.formatNumber(commentCount);

                model.addAttribute("title", title);
                model.addAttribute("description", description);
                model.addAttribute("thumbURL", thumbURL);
                model.addAttribute("tags", tags);
                model.addAttribute("viewCount", formattedViewCount);
                model.addAttribute("likeCount", formattedLikeCount);
                model.addAttribute("commentCount", formattedCommentCount);

                return "details";

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "error";
            }
        }
        return "error";
    }
}
