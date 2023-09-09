/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambdaBetterGrass.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdabettergrass;

import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the better grass mode.
 *
 * @author LambdAurora
 * @version 1.2.1
 * @since 1.0.0
 */
public enum LBGMode {
	OFF,
	FASTEST,
	FAST,
	FANCY;

	/**
	 * Returns whether this mode enables better grass.
	 *
	 * @return {@code true} if the mode enables better grass, otherwise {@code false}
	 */
	public boolean isEnabled() {
		return this != OFF;
	}

	/**
	 * {@return the next available better grass mode}
	 */
	public LBGMode next() {
		var v = values();
		if (v.length == this.ordinal() + 1)
			return v[0];
		return v[this.ordinal() + 1];
	}

	/**
	 * {@return the translated text of the better grass mode}
	 */
	@Deprecated
	public Text getTranslatedText() {
		return Text.empty();
	}

	@Deprecated
	public String getName() {
		return this.name().toLowerCase();
	}

	/**
	 * Gets the better grass mode from its identifier.
	 *
	 * @param id the identifier of the better grass mode
	 * @return the better grass mode if found, otherwise empty
	 */
	public static Optional<LBGMode> byId(String id) {
		return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
	}
}
