/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambdaBetterGrass.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdabettergrass;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the mod configuration.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
@Config(name = LambdaBetterGrass.NAMESPACE)
public class LBGConfig implements ConfigData {
	@ConfigEntry.Gui.Excluded
	public static final ConfigHolder<LBGConfig> INSTANCE = AutoConfig.register(LBGConfig.class, Toml4jConfigSerializer::new);

	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
	public LBGMode mode = LBGMode.FANCY;

	@ConfigEntry.Gui.Tooltip
	public boolean betterLayer = true;

	/**
	 * Loads the configuration.
	 */
	@Deprecated
	public void load() {
		// no-op
	}

	/**
	 * Resets the configuration.
	 */
	@Deprecated
	public void reset() {
		INSTANCE.resetToDefault();
	}

	/**
	 * {@return the better grass mode}
	 */
	public LBGMode getMode() {
		return this.mode;
	}

	/**
	 * Sets the better grass mode.
	 *
	 * @param mode the better grass mode
	 */
	@Deprecated
	public void setMode(@NotNull LBGMode mode) {
		this.mode = mode;
		INSTANCE.save();
	}

	/**
	 * Returns whether better snow is enabled or not.
	 *
	 * @return {@code true} if better snow is enabled, otherwise {@code false}
	 */
	public boolean hasBetterLayer() {
		return this.betterLayer;
	}

	/**
	 * Sets whether better snow is enabled or not.
	 *
	 * @param betterSnow {@code true} if better snow is enabled, otherwise {@code false}
	 */
	@Deprecated
	public void setBetterLayer(boolean betterSnow) {
		this.betterLayer = betterSnow;
		INSTANCE.save();
	}

	/**
	 * Returns whether this mod is in debug mode.
	 *
	 * @return {@code true} if this mod is in debug mode, otherwise {@code false}
	 */
	@Deprecated
	public boolean isDebug() {
		return false;
	}

	/**
	 * Sets whether this mod is in debug mode.
	 *
	 * @param debug {@code true} if this mod is in debug mode, otherwise {@code false}
	 */
	@Deprecated
	public void setDebug(boolean debug) {
		// no-op
	}
}
