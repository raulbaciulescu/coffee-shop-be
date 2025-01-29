package com.raulb.coffee_shop_be.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMultipartFile implements MultipartFile {
    private final Path path;

    public FileMultipartFile(Path path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return path.getFileName().toString();
    }

    @Override
    public String getOriginalFilename() {
        return getName();
    }

    @Override
    public String getContentType() {
        try {
            //not ideal as mentioned in the comments of https://stackoverflow.com/a/8973468/10871900
            return Files.probeContentType(path);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public long getSize() {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return Files.readAllBytes(path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(path);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        transferTo(dest.toPath());
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        Files.copy(path, dest);
    }
}