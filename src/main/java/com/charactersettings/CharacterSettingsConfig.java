/*
 * Copyright (c) 2026, Jamal <http://github.com/1Defence>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.charactersettings;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup(CharacterSettingsConfig.GROUP)
public interface CharacterSettingsConfig extends Config
{
	String GROUP = "charactersettings";
	@ConfigSection(name="General Checks", description="What incorrect settings should trigger the overlay", position=10, closedByDefault=true)
	String generalChecksPanel = "generalChecksPanel";
	@ConfigSection(name="FKey Checks", description="What incorrect fkeys should trigger the overlay", position=11, closedByDefault=true)
	String fkeyChecksPanel = "fkeyChecksPanel";

	@ConfigItem(
			keyName = "toggleKey",
			name = "Toggle Key",
			description = "When you press this key you'll toggle the overlay/helpers on or off",
			position = 1
	)
	default Keybind toggleKey()
	{
		return new Keybind(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
	}

	@ConfigItem(
			keyName = "onByDefault",
			name = "On By Default",
			description = "Overlay and checking will be enabled on startup",
			position = 2
	)
	default boolean onByDefault()
	{
		return true;
	}

	@ConfigItem(
			keyName = "alwaysSortRunePouch",
			name = "Always Sort Rune Pouch",
			description = "Always sort the rune pouch loadout in the bank even if the overlay isn't present for rune top-offs",
			position = 3
	)
	default boolean alwaysSortRunePouch()
	{
		return true;
	}

	/**General Checks**/
	@ConfigItem(
			keyName = "runePouch",
			name = "Rune Pouch",
			description = "Checks if rune pouch has the same collection of runes, order does not matter.",
			position = 0,
			section = generalChecksPanel
	)
	default boolean checkRunePouch()
	{
		return true;
	}

	@ConfigItem(
			keyName = "quickPrayers",
			name = "Quick Prayers",
			description = "Checks if quick prayers match what you have saved.",
			position = 1,
			section = generalChecksPanel
	)
	default boolean checkQuickPrayers()
	{
		return true;
	}

	@ConfigItem(
			keyName = "spellbook",
			name = "Spellbook",
			description = "Checks if current spellbook matches what you have saved.",
			position = 2,
			section = generalChecksPanel
	)
	default boolean checkSpellbook()
	{
		return true;
	}

	@ConfigItem(
			keyName = "bankLockSlots",
			name = "Bank Lock Slots",
			description = "Checks if bank lock slots match what you have saved.",
			position = 3,
			section = generalChecksPanel
	)
	default boolean checkBankLockSlots()
	{
		return true;
	}

	@ConfigItem(
			keyName = "gameClient",
			name = "Game Client",
			description = "Checks if game client (fixed,modern,classic) matches what you have saved.",
			position = 4,
			section = generalChecksPanel
	)
	default boolean checkGameClient()
	{
		return true;
	}

	@ConfigItem(
			keyName = "autoRetaliate",
			name = "Auto Retaliate",
			description = "Checks if auto retaliate status matches what you have saved.",
			position = 5,
			section = generalChecksPanel
	)
	default boolean checkAutoRetaliate()
	{
		return true;
	}

	@ConfigItem(
			keyName = "npcAttackPriority",
			name = "NPC Attack Priority",
			description = "Checks if npc attack priority option matches what you have saved.",
			position = 6,
			section = generalChecksPanel
	)
	default boolean checkNPCAttackPriority()
	{
		return true;
	}

	@ConfigItem(
			keyName = "playerAttackPriority",
			name = "Player Attack Priority",
			description = "Checks if player attack priority option matches what you have saved.",
			position = 7,
			section = generalChecksPanel
	)
	default boolean checkPlayerAttackPriority()
	{
		return true;
	}

	@ConfigItem(
			keyName = "acceptAid",
			name = "Accept Aid",
			description = "Checks if accept aid status matches what you have saved.",
			position = 8,
			section = generalChecksPanel
	)
	default boolean checkAcceptAid()
	{
		return true;
	}

	@ConfigItem(
			keyName = "masterVolumeMute",
			name = "Master Volume Mute",
			description = "Checks if master volume muted status matches what you have saved, while storing exact volume this only cares for mute/unmute",
			position = 9,
			section = generalChecksPanel
	)
	default boolean checkMasterVolumeMute()
	{
		return true;
	}

	@ConfigItem(
			keyName = "musicVolumeMute",
			name = "Music Volume Mute",
			description = "Checks if music volume muted status matches what you have saved, while storing exact volume this only cares for mute/unmute",
			position = 10,
			section = generalChecksPanel
	)
	default boolean checkMusicVolumeMute()
	{
		return true;
	}

	@ConfigItem(
			keyName = "localVolumeMute",
			name = "Local Volume Mute",
			description = "Checks if local(non area sounds) volume muted status matches what you have saved, while storing exact volume this only cares for mute/unmute",
			position = 11,
			section = generalChecksPanel
	)
	default boolean checkLocalVolumeMute()
	{
		return true;
	}

	@ConfigItem(
			keyName = "areaVolumeMute",
			name = "Area Volume Mute",
			description = "Checks if area sounds volume muted status matches what you have saved, while storing exact volume this only cares for mute/unmute",
			position = 12,
			section = generalChecksPanel
	)
	default boolean checkAreaVolumeMute()
	{
		return true;
	}

	/**Fkey Checks**/
	@ConfigItem(
			keyName = "fkeyCombatOptions",
			name = "Combat Options",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 0,
			section = fkeyChecksPanel
	)
	default boolean fkeyCombatOptions()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fkeySkills",
			name = "Skills",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 1,
			section = fkeyChecksPanel
	)
	default boolean fkeySkills()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fkeyQuests",
			name = "Quests",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 2,
			section = fkeyChecksPanel
	)
	default boolean fkeyQuests()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fkeyInventory",
			name = "Inventory",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 3,
			section = fkeyChecksPanel
	)
	default boolean fkeyInventory()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fkeyWornEquipment",
			name = "Worn Equipment",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 4,
			section = fkeyChecksPanel
	)
	default boolean fkeyWornEquipment()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fkeyPrayer",
			name = "Prayer",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 5,
			section = fkeyChecksPanel
	)
	default boolean fkeyPrayer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fkeyMagic",
			name = "Magic",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 6,
			section = fkeyChecksPanel
	)
	default boolean fkeyMagic()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fkeyFriendsList",
			name = "Friends List",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 7,
			section = fkeyChecksPanel
	)
	default boolean fkeyFriendsList()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fkeyAccountManagement",
			name = "Account Management",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 8,
			section = fkeyChecksPanel
	)
	default boolean fkeyAccountManagement()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fkeyLogout",
			name = "Logout",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 9,
			section = fkeyChecksPanel
	)
	default boolean fkeyLogout()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fkeySettings",
			name = "Settings",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 10,
			section = fkeyChecksPanel
	)
	default boolean fkeySettings()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fkeyEmotes",
			name = "Emotes",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 11,
			section = fkeyChecksPanel
	)
	default boolean fkeyEmotes()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fkeyClanChat",
			name = "Clan Chat",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 12,
			section = fkeyChecksPanel
	)
	default boolean fkeyClanChat()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fkeyMusicPlayer",
			name = "Music Player",
			description = "Fkey being incorrect will display overlay, and modify colors in the settings-selection area",
			position = 13,
			section = fkeyChecksPanel
	)
	default boolean fkeyMusicPlayer()
	{
		return false;
	}
}
