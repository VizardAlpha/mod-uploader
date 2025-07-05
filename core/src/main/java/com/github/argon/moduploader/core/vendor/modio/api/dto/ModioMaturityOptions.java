package com.github.argon.moduploader.core.vendor.modio.api.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModioMaturityOptions {
    public final static int NONE = 0;
    public final static int ALCOHOL = 1;
    public final static int DRUGS = 2;
    public final static int VIOLENCE = 4;
    public final static int EXPLICIT = 8;
}
