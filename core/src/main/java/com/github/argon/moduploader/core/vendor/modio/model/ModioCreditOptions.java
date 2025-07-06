package com.github.argon.moduploader.core.vendor.modio.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModioCreditOptions {

    public final static int NONE = 0;
    public final static int SHOW_CREDITS_SECTION = 1;
    public final static int MARK_ORIGINAL_OR_PERMITTED_ASSETS = 2;
    public final static int ALLOW_REDISTRIBUTION_WITH_CREDIT = 4;
    public final static int ALLOW_PORTING_WITH_CREDIT = 8;
    public final static int ALLOW_PATCHING_WITHOUT_CREDIT = 16;
    public final static int ALLOW_PATCHING_WITH_CREDIT = 32;
    public final static int ALLOW_PATCHING_WITH_PERMISSION = 64;
    public final static int ALLOW_REPACKING_WITHOUT_CREDIT = 128;
    public final static int ALLOW_REPACKING_WITH_CREDIT = 256;
    public final static int ALLOW_REPACKING_WITH_PERMISSION = 512;
    public final static int ALLOW_USERS_TO_RESELL = 1024;
}
