package com.github.argon.moduploader.cli.command;

import com.github.argon.moduploader.core.vendor.modio.model.ModioGame;
import com.github.argon.moduploader.core.vendor.modio.model.ModioMod;
import com.github.argon.moduploader.core.vendor.steam.model.SteamMod;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class CliPrinter {

    public void printSteamMod(@Nullable SteamMod.Remote steamMod) {
        if (steamMod == null) {
            return;
        }

        System.out.printf("%s\t%s\t%s\t%s",
            steamMod.id(),
            steamMod.name(),
            steamMod.ownerId(),
            steamMod.timeUpdated());
    }

    public void printModioGames(List<ModioGame> games) {
        printTable(games, game -> new String[]{
            game.id().toString(),
            game.name(),
            game.timeUpdated().toString()
        }, "id", "name", "timeUpdated");
    }

    public void printModioMods(List<ModioMod.Remote> mods) {
        printTable(mods, mod -> new String[]{
            mod.id().toString(),
            mod.name(),
            mod.status().toString(),
            mod.visible().toString(),
            mod.owner(),
            mod.ownerId().toString(),
            mod.timeUpdated().toString()
        }, "id", "name", "status", "visible", "owner", "ownerId", "timeUpdated");
    }

    public void printSteamMods(List<SteamMod.Remote> mods) {
        printTable(mods, mod -> new String[]{
            (mod.id() != null) ? mod.id().toString() : "null",
            mod.name(),
            mod.ownerId().toString(),
            mod.timeUpdated().toString(),
        }, "id", "name", "ownerId", "timeUpdated");
    }

    public <T> void printTable(List<T> rows, Function<T, String[]> mapper, String... headers) {
        String[][] rowsArray = rows.stream()
            .map(mapper)
            .toArray(String[][]::new);

        printTable(rowsArray, headers);
    }

    public void printTable(String[][] rows, String... headers) {
        Integer[] columnWidths = new Integer[headers.length];

        // find the widest string length for each column
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = headers[i].length();

            for (String[] row : rows) {
                int cellLength = row[i].length();

                if (cellLength > columnWidths[i]) {
                    columnWidths[i] = cellLength;
                }
            }
        }

        // build the format string for each row
        String[] columnFormats = new String[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            String format = "%-" + columnWidths[i] + "s";
            columnFormats[i] = format;
        }

        String rowFormat = String.join("\t", columnFormats) + "%n";

        //noinspection ConfusingArgumentToVarargsMethod
        System.out.printf(rowFormat, headers);

        for (String[] row : rows) {
            //noinspection ConfusingArgumentToVarargsMethod
            System.out.printf(rowFormat, row);
        }

        System.out.println();
    }
}
