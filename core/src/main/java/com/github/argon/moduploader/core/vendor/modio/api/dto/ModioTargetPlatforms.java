package com.github.argon.moduploader.core.vendor.modio.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ModioTargetPlatforms {
    @JsonProperty("all") ALL,
    @JsonProperty("source") SOURCE,
    @JsonProperty("windows") WINDOWS,
    @JsonProperty("mac") MAX,
    @JsonProperty("linux") LINUX,
    @JsonProperty("android") ANDROID,
    @JsonProperty("ios") IOS,
    @JsonProperty("xboxone") XBOXONE,
    @JsonProperty("xboxseriesx") XOBOXSERIESX,
    @JsonProperty("ps4") PS4,
    @JsonProperty("ps5") PS5,
    @JsonProperty("switch") SWITCH,
    @JsonProperty("oculus") OCCULUS
}
