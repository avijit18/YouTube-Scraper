package com.TubeScraper.Utils;

import org.springframework.context.annotation.Configuration;

@Configuration
public class numberFormatter {

    public static String formatNumber(int number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }
}
