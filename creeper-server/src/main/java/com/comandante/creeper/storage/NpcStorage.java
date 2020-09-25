package com.comandante.creeper.storage;


import com.comandante.creeper.common.ColorizedTextTemplate;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcBuilder;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class NpcStorage {

    private final GameManager gameManager;
    private final static String LOCAL_NPC_DIRECTORY = "world/npcs/";
    private final String LOCAL_NPC_ART_DIRECTORY = NpcStorage.LOCAL_NPC_DIRECTORY + "art/";
    private static final Logger log = Logger.getLogger(NpcStorage.class);
    private final FilebasedJsonStorage filebasedJsonStorage;
    private final LoadingCache<String, Optional<BufferedImage>> npcArtCache =
            CacheBuilder.newBuilder().maximumSize(200)
                    .build(new CacheLoader<String, Optional<BufferedImage>>() {
                        @Override
                        public Optional<BufferedImage> load(String originalFileName) {
                            String noExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
                            return readNpcArtImage(noExtension + ".png");
                        }
                    });

    public NpcStorage(GameManager gameManager, FilebasedJsonStorage filebasedJsonStorage) {
        this.gameManager = gameManager;
        this.filebasedJsonStorage = filebasedJsonStorage;
    }

    public List<Npc> getAllNpcs() {
        List<NpcMetadata> npcMetadata = readAllNpcs();
        return npcMetadata.stream()
                .map(metadata -> new NpcBuilder(metadata).setGameManager(gameManager).createNpc())
                .collect(Collectors.toList());
    }

    public void saveNpcMetadata(NpcMetadata npcMetadata) throws IOException {
        npcMetadata.setDieMessage(ColorizedTextTemplate.renderToTemplateLanguage(npcMetadata.getDieMessage()));
        npcMetadata.setColorName(ColorizedTextTemplate.renderToTemplateLanguage(npcMetadata.getColorName()));
        filebasedJsonStorage.saveMetadata(npcMetadata.getName(), LOCAL_NPC_DIRECTORY, npcMetadata);
    }

    private List<NpcMetadata> readAllNpcs() {
        Map<String, NpcMetadata> fileNameToNpcMetaData = filebasedJsonStorage.readAllMetadatasWithFilenames(LOCAL_NPC_DIRECTORY, true, new NpcMetadata());

        for (Map.Entry<String, NpcMetadata> fileNameToData : fileNameToNpcMetaData.entrySet()) {
            fileNameToData.getValue().setColorName(ColorizedTextTemplate.renderFromTemplateLanguage(fileNameToData.getValue().getColorName()));
            fileNameToData.getValue().setDieMessage(ColorizedTextTemplate.renderFromTemplateLanguage(fileNameToData.getValue().getDieMessage()));
            fileNameToData.getValue().setOriginalJsonFilename(fileNameToData.getKey());
        }

        return fileNameToNpcMetaData.values().stream().collect(Collectors.toList());
    }

    public Optional<BufferedImage> getNpcArt(String originalFilename) {
        if (originalFilename == null) {
            return Optional.empty();
        }
        try {
            return npcArtCache.get(originalFilename);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<BufferedImage> readNpcArtImage(String name) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(LOCAL_NPC_ART_DIRECTORY + name));
            InputStream in = new ByteArrayInputStream(bytes);
            return Optional.ofNullable(ImageIO.read(in));
        } catch (Exception e) {
            //
        }
        return Optional.empty();
    }
}
