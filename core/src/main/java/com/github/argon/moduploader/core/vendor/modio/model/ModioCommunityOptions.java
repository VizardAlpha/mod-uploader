package com.github.argon.moduploader.core.vendor.modio.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModioCommunityOptions {
    public final static int ALL = 0;
    public final static int COMMENTS = 1;
    public final static int PREVIEWS = 64;
    public final static int PREVIEW_URLS = 128;
    public final static int DEPENDENCIES = 1024;
}
