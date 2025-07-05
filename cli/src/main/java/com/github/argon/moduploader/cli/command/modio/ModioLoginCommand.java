package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.vendor.modio.Modio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@ApplicationScoped
@CommandLine.Command(name = "login", description = "Required for sending any kind of data to mod.io")
public class ModioLoginCommand implements Callable<Integer> {
    @Inject Modio modio;

    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    String email;

    @CommandLine.Option(names = {"-e", "--email"}, required = true, interactive = true)
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Integer call() {
        String apiKey = parentCommand.apiKey;
        modio.authService().requestEmailCode(apiKey, email);

        // prompt for email code input
        new CommandLine(new EnterEmailCodeCommand(apiKey, modio)).execute("-c");

        return 0;
    }

    @RequiredArgsConstructor
    private static class EnterEmailCodeCommand implements Callable<Integer> {
        private final String apiKey;
        private final Modio modio;

        @CommandLine.Option(names = {"-c", "--code"}, description = "Code received by email", interactive = true)
        String emailCode;

        @Override
        public Integer call() {
            modio.authService().exchangeEmailCode(apiKey, emailCode);
            return 0;
        }
    }
}
