package dev.lambdaurora.lambdabettergrass.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resource.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

import net.minecraft.resource.ResourceIoSupplier;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;


/**
 * Represents an in-memory resource pack.
 * <p>
 * The resources of this pack are stored in memory instead of it being on-disk.
 */
public abstract class InMemoryResourcePack implements ResourcePack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<Identifier, Supplier<byte[]>> assets = new ConcurrentHashMap<>();
    private final Map<Identifier, Supplier<byte[]>> data = new ConcurrentHashMap<>();
    private final Map<String, Supplier<byte[]>> root = new ConcurrentHashMap<>();

    @Override
    public @Nullable ResourceIoSupplier<InputStream> openRoot(String... path) {
        String actualPath = String.join("/", path);

        return this.openResource(this.root, actualPath);
    }

    @Override
    public @Nullable ResourceIoSupplier<InputStream> open(ResourceType type, Identifier id) {
        return this.openResource(this.getResourceMap(type), id);
    }

    protected <T> @Nullable ResourceIoSupplier<InputStream> openResource(Map<T, Supplier<byte[]>> map, @NotNull T key) {
        var supplier = map.get(key);

        if (supplier == null) {
            return null;
        }

        byte[] bytes = supplier.get();

        if (bytes == null) {
            return null;
        }

        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(ResourceType type, String namespace, String startingPath, ResourceConsumer consumer) {
        this.getResourceMap(type).entrySet().stream()
                .filter(entry -> entry.getKey().getNamespace().equals(namespace) && entry.getKey().getPath().startsWith(startingPath))
                .forEach(entry -> {
                    byte[] bytes = entry.getValue().get();

                    if (bytes != null) {
                        consumer.accept(entry.getKey(), () -> new ByteArrayInputStream(bytes));
                    }
                });
    }

    @Override
    public @Unmodifiable Set<String> getNamespaces(ResourceType type) {
        return this.getResourceMap(type).keySet().stream()
                .map(Identifier::getNamespace)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        var json = new JsonObject();

        var packJson = new JsonObject();
        packJson.addProperty("description", "A virtual resource pack.");
        packJson.addProperty("pack_format", 15);

        json.add("pack", packJson);

        if (!json.has(metaReader.getKey())) {
            return null;
        } else {
            try {
                return metaReader.fromJson(JsonHelper.getObject(json, metaReader.getKey()));
            } catch (Exception e) {
                LOGGER.error("Couldn't load {} metadata from pack \"{}\":", metaReader.getKey(), this.getName(), e);
                return null;
            }
        }
    }

    @Override
    public void close() {
    }

    public void putResource(@NotNull ResourceType type, @NotNull Identifier id, byte @NotNull [] resource) {
        this.getResourceMap(type).put(id, () -> resource);
    }

    public void clearResources(ResourceType type) {
        this.getResourceMap(type).clear();
    }

    public void clearResources() {
        this.root.clear();
        this.clearResources(ResourceType.CLIENT_RESOURCES);
        this.clearResources(ResourceType.SERVER_DATA);
    }

    public Map<Identifier, Supplier<byte[]>> getResourceMap(ResourceType type) {
        return switch (type) {
            case CLIENT_RESOURCES -> this.assets;
            case SERVER_DATA -> this.data;
        };
    }
}
