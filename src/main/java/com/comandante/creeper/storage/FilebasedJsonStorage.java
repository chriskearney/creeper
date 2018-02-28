package com.comandante.creeper.storage;

import com.comandante.creeper.Creeper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FilebasedJsonStorage {


    private final Gson gson;
    private static final Logger log = Logger.getLogger(FilebasedJsonStorage.class);

    public FilebasedJsonStorage(Gson gson) {
        this.gson = gson;
    }

    public <E> List<E> readAllMetadatas(String storageDirectory, boolean recursive, E a) {
        return getAllJsonStrings(storageDirectory, recursive).stream()
                .map(s -> {
                    try {
                        return (E) gson.fromJson(s, a.getClass());
                    } catch (JsonSyntaxException e) {
                        log.error("Unable to read NpcMetaData from Json!", e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void saveMetadata(String name, String storageDirectory, Object metadata) throws IOException {
        new File(storageDirectory).mkdirs();
        File file = new File(storageDirectory + name.replaceAll("\\s", "_") + ".json");
        org.apache.commons.io.FileUtils.writeStringToFile(file, gson.toJson(metadata));
    }

    private List<String> getAllJsonStrings(String storageDirectory, boolean recursive) {
        boolean mkdirs = new File(storageDirectory).mkdirs();
        if (mkdirs) {
            log.info("Created directory: " + storageDirectory);
        }
        Iterator iterator = FileUtils.iterateFiles(new File(storageDirectory), new String[]{"json"}, recursive);
        return toListOfJsonStrings(iterator);
    }

    private List<String> toListOfJsonStrings(final Iterator<File> iterator) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .map(f -> {
                    try {
                        Creeper.startUpMessage("Reading: " + f.getAbsolutePath());
                        return new String(Files.readAllBytes(f.toPath()));
                    } catch (IOException e) {
                        log.error("Unable to read: " + f.getAbsolutePath(), e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
