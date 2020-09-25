package com.comandante.creeper.storage;

import com.comandante.creeper.Creeper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FilebasedJsonStorage {

    private final Gson gson;
    private static final Logger log = Logger.getLogger(FilebasedJsonStorage.class);

    public FilebasedJsonStorage(Gson gson) {
        this.gson = gson;
    }

    public <E> Map<String, E> readAllMetadatasWithFilenames(String storageDirectory, boolean recursive, E a) {

        Map<String, E> fileNameToEntity = Maps.newHashMap();

        Map<String, String> allJsonStrings = getAllJsonStrings(storageDirectory, recursive);

        for (Map.Entry<String, String> fileNameToContents : allJsonStrings.entrySet()) {
            fileNameToEntity.put(fileNameToContents.getKey(), (E) gson.fromJson(fileNameToContents.getValue(), a.getClass()));
        }

        return fileNameToEntity;
    }

    public <E> List<E> readAllMetadatas(String storageDirectory, boolean recursive, E a) {
        return readAllMetadatasWithFilenames(storageDirectory, recursive, a).values()
                .stream().collect(Collectors.toList());
    }

    public void saveMetadata(String name, String storageDirectory, Object metadata) throws IOException {
        new File(storageDirectory).mkdirs();
        File file = new File(storageDirectory + name.replaceAll("\\s", "_") + ".json");
        org.apache.commons.io.FileUtils.writeStringToFile(file, gson.toJson(metadata));
    }

    private Map<String, String> getAllJsonStrings(String storageDirectory, boolean recursive) {
        boolean mkdirs = new File(storageDirectory).mkdirs();
        if (mkdirs) {
            log.info("Created directory: " + storageDirectory);
        }
        return toListOfJsonStrings(getFilesWithExtension(storageDirectory, "json", recursive));
    }

    public Iterator<File> getFilesWithExtension(String storageDirectory, String extension, boolean recursive) {
        return FileUtils.iterateFiles(new File(storageDirectory), new String[]{extension}, recursive);
    }

    private Map<String, String> toListOfJsonStrings(final Iterator<File> iterator) {
        Map<String, String> fileNameToContents = Maps.newHashMap();
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .forEach(file -> {
                    try {
                        fileNameToContents.put(file.getName(), new String(Files.readAllBytes(file.toPath())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        return fileNameToContents;
    }
}
