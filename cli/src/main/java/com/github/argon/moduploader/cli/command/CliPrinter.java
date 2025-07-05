package com.github.argon.moduploader.cli.command;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class CliPrinter {
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

        String rowFormat = String.join("\t", columnFormats);

        //noinspection ConfusingArgumentToVarargsMethod
        System.out.printf(rowFormat + "%n", headers);

        for (String[] row : rows) {
            //noinspection ConfusingArgumentToVarargsMethod
            System.out.printf(rowFormat, row);
        }

        System.out.println();
    }
}
