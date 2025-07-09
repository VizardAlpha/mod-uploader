package com.github.argon.moduploader.core.vendor.modio.api.dto;

public record ModioStatsDto(
   Integer modId,
   Integer popularityRank_position,
   Integer popularityRank_total_mods,
   Integer downloadsToday,
   Integer downloadsTotal,
   Integer subscribersTotal,
   Integer ratingsTotal,
   Integer ratingsPositive,
   Integer ratingsNegative,
   Integer ratingsPercentagePositive,
   Integer ratingsWeightedAggregate,
   String ratingsDisplayText,
   Integer dateExpires
) {}
