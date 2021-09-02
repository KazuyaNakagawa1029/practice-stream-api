package com.example.demo.service;


import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ZipFileService {

    public void createZipFIle()  throws IOException{
        byte[] readData = new byte[1];
        String fileName ="C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\file\\dummy.txt";
        BufferedInputStream  bis = new BufferedInputStream (new FileInputStream(fileName));
        String zipName =  "C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\zip\\zipテスト.zip";
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream( new File(zipName)));
        zos.putNextEntry(new ZipEntry(("dummy2.txt")));
        BufferedOutputStream bos = new BufferedOutputStream(zos);

        int data;
        while ((data = bis.read(readData)) != -1) {
            bos.write((byte) data);
        }
        bis.close();
        bos.close();
    }


    public void deforestZipFIle() {
        File zipFile = new File("C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\zip\\zipテスト.zip");
        String outputfile = "C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\file\\";
        try (
                FileInputStream fis = new FileInputStream(zipFile);
                ZipInputStream zis = new ZipInputStream(fis);
        ) {
            ZipEntry zipentry;
            while ((zipentry = zis.getNextEntry()) != null) {
                try (FileOutputStream fos = new FileOutputStream(outputfile + zipentry.getName());
                     BufferedOutputStream bos = new BufferedOutputStream(fos);
                     BufferedInputStream bis =new BufferedInputStream(zis);
                ) {
                    byte[] readData = new byte[1];
                    int data;
                    while ((data = bis.read(readData)) != -1) {
                        bos.write((byte) data);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTarGzFile() throws Exception {
        Path dir = Paths.get("C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\file\\dummy.txt");
        Path dest = Paths.get(dir.getFileName() + ".tar.gz");

        try (OutputStream fo = Files.newOutputStream(dest);
             OutputStream gzo = new GzipCompressorOutputStream(fo);
             ArchiveOutputStream out = new TarArchiveOutputStream(gzo);
             Stream<Path> stream = Files.walk(dir)) {

            stream.forEach(p -> {
                try {
                    ArchiveEntry entry = out.createArchiveEntry(
                            p.toFile(),
                            p.subpath(dir.getNameCount() - 1,
                                    p.getNameCount()
                            ).toString());
                    out.putArchiveEntry(entry);
                    if (p.toFile().isFile()) {
                        try (InputStream i = Files.newInputStream(p)) {
                            IOUtils.copy(i, out);
                        }
                    }
                    out.closeArchiveEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            out.finish();
        }
    }

    public void deforestTarGzFile() throws Exception {
        final Path dir = Paths.get("./var");
        final Path source = Paths.get("C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\dummy.txt.tar.gz");

        try (InputStream fi = Files.newInputStream(source);
             InputStream gzi = new GzipCompressorInputStream(fi);
             ArchiveInputStream in = new TarArchiveInputStream(gzi)) {

            ArchiveEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                if (!in.canReadEntryData(entry)) {
                    continue;
                }

                File file = dir.resolve(entry.getName()).toFile();
                if (entry.isDirectory()) {
                    if (!file.isDirectory() && !file.mkdirs()) {
                        throw new IOException("failed to create directory " + file);
                    }
                } else {
                    File parent = file.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }
                    try (OutputStream o = Files.newOutputStream(file.toPath())) {
                        IOUtils.copy(in, o);
                    }
                }
            }
        }
    }
}



