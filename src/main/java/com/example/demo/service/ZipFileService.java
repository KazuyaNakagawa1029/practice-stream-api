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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ZipFileService {

    public void createZipFIle() {
        // 文字コード
        Charset charset = Charset.forName("MS932");
        // 入力ファイル
        Path path1 = Paths.get("C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\file\\dummy.txt");
        // 出力ファイル
        String outfile1 = "C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\zip\\zipテスト.zip";

        try (
                FileOutputStream fos = new FileOutputStream(outfile1);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                ZipOutputStream zos = new ZipOutputStream(bos, charset);
        ) {
            // zipの中のファイル1
            byte[] data1 = Files.readAllBytes(path1);
            ZipEntry zip1 = new ZipEntry("zipその1.txt");
            zos.putNextEntry(zip1);
            zos.write(data1);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deforestZipFIle() {
        // 文字コード
        Charset charset = Charset.forName("MS932");
        // 入力ファイル
        String inputfile1 = "C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\zip\\zipテスト.zip";
        // 出力先
        String outputfile1 = "C:\\Users\\NakagawaKazuya\\IdeaProjects\\practice-stream-api\\src\\main\\resources\\file\\";
        try (
                FileInputStream fis = new FileInputStream(inputfile1);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ZipInputStream zis = new ZipInputStream(bis, charset);
        ) {
            ZipEntry zipentry;
            // zipの中のファイルがあるだけ繰り返す
            // 展開後のファイルサイズ、ファイル名に注意
            while ((zipentry = zis.getNextEntry()) != null) {
                try (FileOutputStream fos = new FileOutputStream(outputfile1 + zipentry.getName());
                     BufferedOutputStream bos = new BufferedOutputStream(fos);
                ) {
                    byte[] data = new byte[1024]; // 1KB 調整可
                    int count = 0;
                    while ((count = zis.read(data)) != -1) {
                        bos.write(data, 0, count);
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



