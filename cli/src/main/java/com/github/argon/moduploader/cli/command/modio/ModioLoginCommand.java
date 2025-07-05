package com.github.argon.moduploader.cli.command.modio;

import com.github.argon.moduploader.core.vendor.modio.Modio;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@ApplicationScoped
@CommandLine.Command(name = "login", description = "Required for sending any kind of data to mod.io")
public class ModioLoginCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    ModioCommand parentCommand;

    String email;

    @CommandLine.Option(names = {"-e", "--email"}, required = true, interactive = true)
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Integer call() {
        if (parentCommand.init(null)) {
            return 1;
        }

        Modio modio = parentCommand.modio;
        modio.getAuthService().requestEmailCode(email);

        // prompt for email code input
        new CommandLine(new EnterEmailCodeCommand(modio)).execute("-c");

        return 0;
    }

    @RequiredArgsConstructor
    private static class EnterEmailCodeCommand implements Callable<Integer> {
        private final Modio modio;

        @CommandLine.Option(names = {"-c", "--code"}, description = "Code received by email", interactive = true)
        String emailCode;

        @Override
        public Integer call() throws Exception {
            modio.getAuthService().exchangeEmailCode(emailCode);
            return 0;
        }
    }
}
