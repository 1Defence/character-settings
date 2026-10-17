package com.charactersettings;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CharacterSettingsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CharacterSettingsPlugin.class);
		RuneLite.main(args);
	}
}