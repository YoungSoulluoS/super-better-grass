/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambdaBetterGrass.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdabettergrass.resource;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.texture.NativeImage;
import dev.lambdaurora.lambdabettergrass.LambdaBetterGrass;
import net.minecraft.resource.ResourceIoSupplier;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.resource.pack.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class LBGResourcePack implements ResourcePack {
	private static final Set<String> NAMESPACES = Sets.newHashSet(LambdaBetterGrass.NAMESPACE);
	private final Map<String, Supplier<byte[]>> resources = new ConcurrentHashMap<>();

	private final LambdaBetterGrass mod;

	public LBGResourcePack(LambdaBetterGrass mod) {
		this.mod = mod;
	}

	public Identifier dynamicallyPutImage(String name, NativeImage image) {
		final var id = new Identifier(LambdaBetterGrass.NAMESPACE, "block/bettergrass/" + name);

		Supplier<byte[]> suppler = () -> {
			try {
				return image.getBytes();
			} catch (IOException e) {
				this.mod.warn("Could not put image {}.", name, e);
				return null;
			}
		};

		this.resources.put(String.format("assets/%s/textures/%s.png", id.getNamespace(), id.getPath()), suppler);

		return id;
	}

	@Override
	public @Nullable ResourceIoSupplier<InputStream> openRoot(String... path) {
		return openResource(String.join("/", path));
	}

	@Override
	public @Nullable ResourceIoSupplier<InputStream> open(ResourceType type, Identifier id) {
		return openResource(String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath()));
	}

	@Override
	public void listResources(ResourceType type, String namespace, String startingPath, ResourceConsumer consumer) {
		String path = String.format("%s/%s/%s", type.getDirectory(), namespace, startingPath);

		this.resources.keySet().stream()
				.filter(string -> string.startsWith(path))
				.forEach(entry -> consumer.accept(fromPath(type, entry), openResource(entry)));
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return NAMESPACES;
	}

	@Override
	public String getName() {
		return "LambdaBetterGrass generated resources";
	}

	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) {
		return null;
	}

	@Override
	public void close() {
		// this.resources.clear();
	}

	protected ResourceIoSupplier<InputStream> openResource(String path) {
		var supplier = this.resources.get(path);
		if (supplier == null) {
			return null;
		}

		byte[] bytes = supplier.get();
		if (bytes == null) {
			return null;
		}

		return () -> new ByteArrayInputStream(bytes);
	}

	private static @Nullable Identifier fromPath(ResourceType type, String path) {
		String[] split = path.substring((type.getDirectory() + "/").length()).split("/", 2);

		return Identifier.tryValidate(split[0], split[1]);
	}
}
