package com.github.argon.moduploader.core.vendor.modio.api.dto;

import java.util.List;
import java.util.Map;

public record ModioTagOptionDto(
    String name,
    String type,
    List<String> tags,
    Boolean hidden,
    Boolean locked,
    Map<String, Integer> tagCountMap
) {
}
