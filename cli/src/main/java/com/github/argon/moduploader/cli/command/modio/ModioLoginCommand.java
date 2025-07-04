package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.auth.BearerTokenFileConsumer;
import com.github.argon.moduploader.core.file.FileService;
import com.github.argon.moduploader.core.vendor.modio.ModioAuthService;
import com.github.argon.moduploader.core.vendor.modio.ModioProperties;
import com.github.argon.moduploader.core.vendor.modio.client.ModioOAuthClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@ApplicationScoped
@CommandLine.Command(name = "login", description = "Required for sending any kind of data to mod.io")
public class ModioLoginCommand implements Runnable {
    @RestClient
    ModioOAuthClient modioAuthClient;

    @Inject
    FileService fileService;

    @Inject
    ModioProperties modioProperties;

    String apiKey;
    String email;

    // TODO make this global for all modio commands
    @CommandLine.Option(names = {"-key", "--api-key"}, required = true,
        description = "The mod.io api access key. You can create a key in your mod.io profile")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @CommandLine.Option(names = {"-e", "--email"}, required = true, interactive = true)
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void run() {
        ModioAuthService modioAuth = new ModioAuthService(apiKey, modioAuthClient);
        modioAuth.requestEmailCode(email);

        BearerTokenFileConsumer bearerTokenFileConsumer = new BearerTokenFileConsumer(modioProperties.tokenFilePath(), fileService);

        // prompt for email code input
        new CommandLine(new EnterEmailCodeCommand(modioAuth, bearerTokenFileConsumer)).execute("-c");
    }

    @RequiredArgsConstructor
    private static class EnterEmailCodeCommand implements Callable<Integer> {
        private final ModioAuthService modioAuth;
        private final BearerTokenFileConsumer bearerTokenFileConsumer;

        @CommandLine.Option(names = {"-c", "--code"}, description = "Code received by email", interactive = true)
        String emailCode;

        @Override
        public Integer call() throws Exception {
            modioAuth.exchangeEmailCode(emailCode, bearerToken -> {
                bearerTokenFileConsumer.accept(bearerToken);
                System.out.println("Login successful");
            });

            return 0;
        }
    }
}
