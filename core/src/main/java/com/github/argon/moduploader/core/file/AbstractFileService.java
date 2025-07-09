package com.github.argon.moduploader.core.file;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public abstract class AbstractFileService implements IFileService {

    protected String readFromInputStream(InputStream inputStream) throws IOException {
        return String.join("\n", readLinesFromInputStream(inputStream));
    }

    protected List<String> readLinesFromInputStream(InputStream inputStream) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    protected void zipFolder(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFolder(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

//    protected void zipFolder(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
//        String sourceFile = "test1.txt";
//        FileOutputStream fos = new FileOutputStream("compressed.zip");
//        ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//        File fileToZip = new File(sourceFile);
//        FileInputStream fis = new FileInputStream(fileToZip);
//        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
//        zipOut.putNextEntry(zipEntry);
//
//        byte[] bytes = new byte[1024];
//        int length;
//        while((length = fis.read(bytes)) >= 0) {
//            zipOut.write(bytes, 0, length);
//        }
//
//        zipOut.close();
//        fis.close();
//        fos.close();
//    }
}
