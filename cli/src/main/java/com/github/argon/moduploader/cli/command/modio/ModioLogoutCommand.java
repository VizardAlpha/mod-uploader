package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.vendor.modio.Modio;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "logout", description = "Will invalidate the current user token and logs you out.")
public class ModioLogoutCommand implements Callable<Integer> {
    @Inject Modio modio;

    @Override
    public Integer call() {
        if (!modio.authService().logout()) {
            return 1;
        }

        return 0;
    }
}
