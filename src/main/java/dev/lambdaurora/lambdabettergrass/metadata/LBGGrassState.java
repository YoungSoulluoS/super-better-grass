/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambdaBetterGrass.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdabettergrass.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lambdaurora.lambdabettergrass.LambdaBetterGrass;
import dev.lambdaurora.lambdabettergrass.model.LBGUnbakedModel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents grass model states with its different {@link LBGMetadata}.
 *
 * @author LambdAurora
 * @version 1.4.0
 * @since 1.0.0
 */
public class LBGGrassState extends LBGState {
	private final LBGMetadata metadata;
	private final Map<String, LBGMetadata> metadatas = new Object2ObjectOpenHashMap<>();

	public LBGGrassState(@NotNull Identifier id, @NotNull ResourceManager resourceManager, @NotNull JsonObject json) {
		super(id);

		// Look for variants.
		if (json.has("variants")) {
			var variants = json.getAsJsonObject("variants");
			variants.entrySet().forEach(entry -> {
				var variant = entry.getValue().getAsJsonObject();
				if (variant.has("data")) {
					var metadataId = Identifier.tryParse(variant.get("data").getAsString());

					this.metadatas.put(entry.getKey(), this.loadMetadata(resourceManager, metadataId));
				}
			});

			{
				LBGMetadata normalMetadata = this.metadatas.get("snowy=false");
				LBGMetadata snowyMetadata = this.metadatas.get("snowy=true");

				if (normalMetadata != null && snowyMetadata != null) {
					snowyMetadata.snowyModelVariantProvider = bakedModel -> normalMetadata.snowyModelVariant = bakedModel;
				}
			}

			this.metadata = null;
		} else if (json.has("data")) { // Look for a common metadata if no variants are specified.
			var metadataId = Identifier.tryParse(json.get("data").getAsString());
			this.metadata = metadataId != null ? this.loadMetadata(resourceManager, metadataId) : null;
		} else // The state file is invalid, cannot find any metadata.
			this.metadata = null;
	}

	/**
	 * Loads the metadata from the resource manager.
	 *
	 * @param resourceManager the resource manager
	 * @param metadataId the metadata identifier
	 * @return the metadata if loaded successfully, else {@code null}
	 */
	private @Nullable LBGMetadata loadMetadata(@NotNull ResourceManager resourceManager, @NotNull Identifier metadataId) {
		var metadataResourceId = Identifier.of(metadataId.getNamespace(), metadataId.getPath() + ".json");
		try (var reader = new InputStreamReader(resourceManager.getResourceOrThrow(metadataResourceId).open())) {
			var metadataJson = JsonParser.parseReader(reader).getAsJsonObject();

			return new LBGMetadata(resourceManager, metadataId, metadataJson);
		} catch (IOException e) {
			// Ignore.
		}
		LambdaBetterGrass.get().warn("Could not load metadata `" + metadataId + "`.");
		return null;
	}

	/**
	 * Returns the metadata corresponding to the specified model identifier.
	 *
	 * @param modelId the model identifier
	 * @return a metadata if it exists for the given model id, else {@code null}
	 */
	public @Nullable LBGMetadata getMetadata(@NotNull ModelIdentifier modelId) {
		if (this.metadata != null)
			return this.metadata;
		String[] modelVariant = modelId.getVariant().split(",");
		for (var variant : this.metadatas.entrySet()) {
			if (this.matchVariant(modelVariant, variant.getKey().split(",")))
				return variant.getValue();
		}
		return null;
	}

	@Override
	public @Nullable UnbakedModel getCustomUnbakedModel(ModelIdentifier modelId, UnbakedModel originalModel,
			Function<Identifier, UnbakedModel> modelGetter) {
		var metadata = this.getMetadata(modelId);
		if (metadata != null) {
			return new LBGUnbakedModel(originalModel, metadata);
		}
		return null;
	}
}
