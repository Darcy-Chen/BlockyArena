/*
 * Copyright 2017-2020 The BlockyArena Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mrdarcychen;

import com.google.common.reflect.TypeToken;
import io.github.mrdarcychen.utils.Kit;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all custom kits defined by server admin.
 */
public class KitManager {
    private final Map<String, Kit> kits = new HashMap<>();
    private final Path configDirectory;

    KitManager(Path configDirectory) {
        this.configDirectory = configDirectory;
        loadKits();
    }

    public void loadKits() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(configDirectory, "*.conf");
            for (Path path : stream) {
                ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                        .builder().setPath(path).build();
                ConfigurationNode rootNode = loader.load();
                String id = rootNode.getNode("id").getString();
                try {
                    Kit kit = rootNode.getValue(TypeToken.of(Kit.class));
                    kits.put(kit.getId(), kit);
                } catch (InvalidDataException e) {
                    Utility.warn("Kit " + id + " cannot be loaded because it contains " +
                            "unknown items.");
                }
                loader.save(rootNode);
            }
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the given Kit with the given id associated with it.
     *
     * @param kit the Kit to be added
     * @param id the id associated with the Kit
     */
    public void add(Kit kit, String id) {
        kits.put(id, kit);
        Utility.warn(id + " has been added to kit manager.");
        Path path = Paths.get(configDirectory + File.separator + id + ".conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder().setPath(path).build();
        try {
            ConfigurationNode rootNode = loader.load();
            rootNode.setValue(TypeToken.of(Kit.class), kit);
            loader.save(rootNode);
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Kit with the given id.
     *
     * @param id the id of the Kit
     * @return the Kit with the given id
     */
    public Kit get(String id) {
        return kits.get(id);
    }

    /**
     * Removes the {@link Kit} and its config file if exists.
     *
     * @param id the id of the Kit
     */
    public void remove(String id) {
        kits.remove(id);
        Path path = Paths.get(configDirectory + File.separator + id + ".conf");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(id + " does not exist.");
        }
    }
}
