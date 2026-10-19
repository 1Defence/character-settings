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

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
	name = "Character Settings",
	description = "save character specific settings, to match when on a different account/profile."
)
public class CharacterSettingsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private CharacterSettingsConfig config;

	@Inject
	public ConfigManager configManager;

	@Inject
	private Gson gson;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CharacterSettingsOverlay overlay;

	@Inject
	private GenericWidgetOverlay genericWidgetOverlay;

	@Inject
	private ClientThread clientThread;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private KeyManager keyManager;

	String CONFIG_GROUP = "charactersettings";

	private Widget lockButton = null;
	private boolean freeEditPrayerMode = false;

	SettingsData data;
	int[] currentFkeys = new int[14];
	boolean[] checkedFKeys = new boolean[]{true,false,false,true,true,true,true,false,false,true,true,false,false,false};

	final static int[] RUNEPOUCH_LOADOUT_INDICIES = new int[] {33,37,40,43,45,47,49,51,53,55};
	final static int[] F_KEY_VARBIT_ORDER_INDICIES = new int[] {0,5,10,1,6,11,2,7,12,3,8,13,4,9};
	private static final Map<Integer, Integer> GAME_CLIENT_INDICIES = Map.of(
			548, 0,
			161, 1,
			164, 2
	);

	private static final Map<Integer, Integer> GAME_CLIENT_COMPONENT_IDS = Map.of(
			0, 548,
			1, 161,
			2, 164
	);

	final static String[] PLAYER_ATTACK_OPTIONS = new String[] {
			"Depends on combat levels",
			"Always right-click",
			"Left-click where available",
			"Hidden",
			"Right-click for clanmates"
	};

	final static String[] NPC_ATTACK_OPTIONS = new String[] {
			"Depends on combat levels",
			"Always right-click",
			"Left-click where available",
			"Hidden"
	};

	final static String[] GAME_CLIENT_LAYOUT_OPTIONS = new String[] {
			"Fixed - Classic layout",
			"Resizable - Classic layout",
			"Resizable - Modern layout"
	};

	private static final int SCRIPT_BANK_OPEN = 85;
	private static final int SCRIPT_BANK_OPEN2 = 7785;

	private static final int SCRIPT_QUICK_PRAYERS_REFRESH = 470;

	private static final int SCRIPT_SETTINGS_DROPDOWN_OPEN = 4568;
	private static final int SCRIPT_SETTINGS_DROPDOWN_SELECTED = 4569;

	private static final int SCRIPT_OVERALL_SETTINGS_INIT = 3849;
	private static final int SCRIPT_OVERALL_SETTINGS_SEARCH_CHANGED = 3876;
	private static final int SCRIPT_OVERALL_SETTINGS_TAB_CHANGED = 3835;
	private static final int SCRIPT_OVERALL_SETTINGS_DROPDOWN_OPEN = 3851;
	private static final int GROUP_RUNEPOUCH_LOADOUTS = 15;
	private static final int GROUP_SETTINGS_DROPDOWNS = 116;
	private static final int CHILD_PLAYER_ATTACK_PRIORITY_DROPDOWN = 38;
	private static final int CHILD_PLAYER_ATTACK_PRIORITY_CURRENT = 6;
	private static final int CHILD_NPC_ATTACK_PRIORITY_DROPDOWN = 39;
	private static final int CHILD_NPC_ATTACK_PRIORITY_CURRENT = 7;
	private static final int CHILD_GAME_CLIENT_DROPDOWN = 40;
	private static final int CHILD_GAME_CLIENT_CURRENT = 27;

	private static final int GROUP_BANK_SLOTS = 906;
	private static final int CHILD_BANK_SLOTS_CONTAINER = 8;

	private static final int GROUP_QUICK_PRAYERS = 77;
	private static final int CHILD_QUICK_PRAYERS_CONTAINER = 4;
	private static final int CHILD_QUICK_PRAYERS_PARENT = 0;

	private static final int GROUP_OVERALL_SETTINGS = 134;
	private static final int CHILD_OVERALL_SETTINGS_DROPDOWN_PANEL = 27;
	private static final int CHILD_OVERALL_SETTINGS_DROPDOWN_CONTAINER = 29;
	private static final int CHILD_OVERALL_SETTINGS_PARENT_CONTAINER = 19;

	public static final int TOPLEVEL_LOGIN_CONFIRM = 165;
	private static final int FKEY_DROPDOWN_OPEN_SPRITE_ID = 788;

	private static final int BANK_LOCK_DEFAULT_COLOR = 10461087;//grey
	private static final int BANK_LOCK_LOCKED_COLOR = 16711680;//red
	private static final int BANK_LOCK_DEFAULT_HOVER_COLOR = 16777215;//white
	private static final int BANK_LOCK_LOCKED_HOVER_COLOR = 16740464;//light red

	private static final int BANK_LOCK_DIM_COLOR = 0;//black
	private static final int BANK_LOCK_NEEDS_CHANGE_COLOR = 8454016;//green
	public static final String FKeyHeaderTitleText = "Keybinds";
	public Widget[] FKeyTextWidgets = new Widget[14];
	public Widget[] FKeyButtonWidgets = new Widget[14];
	public Widget modifiedFkeyDDWidget = null;
	public int modifiedFKeyIndex = 0;
	public boolean FKeyWidgetsFound = false;
	int[] swappedRunePouchIndicies = null;
	public boolean helperActive = true;
	public boolean configOnByDefault,
					configAlwaysSortRunePouch;
	public boolean configCheckRunePouch,
					configCheckQuickPrayers,
					configCheckSpellbook,
					configCheckBankLockSlots,
					configCheckGameClient,
					configCheckAutoRetaliate,
					configCheckNPCAttackPriority,
					configCheckPlayerAttackPriority,
					configCheckAcceptAid,
					configCheckMasterVolumeMute,
					configCheckMusicVolumeMute,
					configCheckLocalVolumeMute,
					configCheckAreaVolumeMute;

	boolean fKeysHandled;

	private final HotkeyListener toggleKeyListener = new HotkeyListener(() -> config.toggleKey())
	{
		@Override
		public void hotkeyPressed()
		{
			clientThread.invoke(() -> ToggleOverlay());
		}
	};

	@Override
	protected void startUp() throws Exception
	{
		cacheConfigs();
		helperActive = false;
		overlayManager.add(overlay);
		overlayManager.add(genericWidgetOverlay);
		mouseManager.registerMouseListener(overlay.mouseListener);
		keyManager.registerKeyListener(toggleKeyListener);
		clientThread.invokeLater(() -> {
			data = GetStoredValuesFromConfig();
			if(data != null)
			{
				overlay.SetupImageData();
				overlay.SetupRunePouch();
				overlay.SetupPrayers();
				if (configOnByDefault)
				{
					ToggleOverlay();
				}
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		mouseManager.unregisterMouseListener(overlay.mouseListener);
		keyManager.unregisterKeyListener(toggleKeyListener);
		overlayManager.remove(overlay);
		overlayManager.remove(genericWidgetOverlay);
		if(helperActive){
			ToggleOverlay();
		}
	}

	@Provides
	CharacterSettingsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CharacterSettingsConfig.class);
	}

	/**cache config values on start*/
	public void cacheConfigs(){

		configOnByDefault = config.onByDefault();
		configAlwaysSortRunePouch = config.alwaysSortRunePouch();
		configCheckRunePouch = config.checkRunePouch();
		configCheckQuickPrayers = config.checkQuickPrayers();
		configCheckSpellbook = config.checkSpellbook();
		configCheckBankLockSlots = config.checkBankLockSlots();
		configCheckGameClient = config.checkGameClient();
		configCheckAutoRetaliate = config.checkAutoRetaliate();
		configCheckNPCAttackPriority = config.checkNPCAttackPriority();
		configCheckPlayerAttackPriority = config.checkPlayerAttackPriority();
		configCheckAcceptAid = config.checkAcceptAid();
		configCheckMasterVolumeMute = config.checkMasterVolumeMute();
		configCheckMusicVolumeMute = config.checkMusicVolumeMute();
		configCheckLocalVolumeMute = config.checkLocalVolumeMute();
		configCheckAreaVolumeMute = config.checkAreaVolumeMute();

		checkedFKeys = new boolean[]{
				config.fkeyCombatOptions(),
				config.fkeySkills(),
				config.fkeyQuests(),
				config.fkeyInventory(),
				config.fkeyWornEquipment(),
				config.fkeyPrayer(),
				config.fkeyMagic(),
				config.fkeyFriendsList(),
				config.fkeyAccountManagement(),
				config.fkeyLogout(),
				config.fkeySettings(),
				config.fkeyEmotes(),
				config.fkeyClanChat(),
				config.fkeyMusicPlayer()
		};

		fKeysHandled = false;
		for (boolean fKey : checkedFKeys) {
			if (fKey) {
				fKeysHandled = true;
				break;
			}
		}

		clientThread.invokeLater(() -> {
			if(data == null)
				return;

			HandleFkeys(fKeysHandled);
			HandleQuickPrayers(configCheckQuickPrayers);
			HandleBankLocks(configCheckBankLockSlots);

			boolean shouldSort = ShouldSortRunePouch();
			if(shouldSort && swappedRunePouchIndicies == null){
				UpdateRunePouchWidget();
			}else if(!shouldSort && swappedRunePouchIndicies != null){
				UndoLastRPLoadout();
			}

		});

	}

	public SettingsData GetStoredValuesFromConfig(){
		String json = configManager.getConfiguration(CONFIG_GROUP, "settingsData");
		if (!Strings.isNullOrEmpty(json))
		{
			return gson.fromJson(json, new TypeToken<SettingsData>(){}.getType());
		} else {
			return null;
		}
	}

	public void SaveToConfig(){
		clientThread.invokeLater(() -> {
			data = GetSettingsDataFromClient();
			String json = gson.toJson(data);
			configManager.setConfiguration(CONFIG_GROUP, "settingsData", json);
			overlay.SetupImageData();
			overlay.SetupRunePouch();
			overlay.SetupPrayers();
			log.debug("setting data "+json);
		});
	}

	public SettingsData GetSettingsDataFromClient(){
		SettingsData settingsData = new SettingsData(
				new int[]{
						client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_1),
						client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_2),
						client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_3),
						client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_4)},
				new int[] {
						client.getVarbitValue(VarbitID.STONE_COMBAT_KEY),
						client.getVarbitValue(VarbitID.STONE_STATS_KEY),
						client.getVarbitValue(VarbitID.STONE_JOURNAL_KEY),
						client.getVarbitValue(VarbitID.STONE_INV_KEY),
						client.getVarbitValue(VarbitID.STONE_WORN_KEY),
						client.getVarbitValue(VarbitID.STONE_PRAYER_KEY),
						client.getVarbitValue(VarbitID.STONE_MAGIC_KEY),
						client.getVarbitValue(VarbitID.STONE_FRIENDS_KEY),
						client.getVarbitValue(VarbitID.STONE_ACCOUNT_KEY),
						client.getVarbitValue(VarbitID.STONE_LOGOUT_KEY),
						client.getVarbitValue(VarbitID.STONE_OPTIONS1_KEY),
						client.getVarbitValue(VarbitID.STONE_OPTIONS2_KEY),
						client.getVarbitValue(VarbitID.STONE_CLANCHAT_KEY),
						client.getVarbitValue(VarbitID.STONE_MUSIC_KEY),
				},
				GAME_CLIENT_INDICIES.get(client.getTopLevelInterfaceId()),
				client.getVarpValue(VarPlayerID.OPTION_ATTACKPRIORITY_NPC),
				client.getVarpValue(VarPlayerID.OPTION_ATTACKPRIORITY),
				client.getVarbitValue(VarbitID.LUNAR_SPELLBOOK_CHANGE) == 1 ? 2 : client.getVarbitValue(VarbitID.SPELLBOOK),
				client.getVarpValue(VarPlayerID.OPTION_NODEF),
				client.getVarbitValue(VarbitID.OPTION_ACCEPTAID),
				client.getVarpValue(VarPlayerID.OPTION_MASTER_VOLUME),
				client.getVarpValue(VarPlayerID.OPTION_MUSIC),
				client.getVarpValue(VarPlayerID.OPTION_SOUNDS),
				client.getVarpValue(VarPlayerID.OPTION_AREASOUNDS),
				client.getVarbitValue(VarbitID.QUICKPRAYER_SELECTED),
				client.getVarbitValue(VarbitID.BANK_SIDE_SLOT_OVERVIEW)
		);
		return settingsData;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(CharacterSettingsConfig.GROUP))
		{
			return;
		}
		cacheConfigs();
	}

	public void ToggleOverlay(){
		if(data == null)
			return;
		helperActive = !helperActive;
		clientThread.invokeLater(() -> {
			HandleFkeys(helperActive);
			HandleBankLocks(helperActive);
			HandleQuickPrayers(helperActive);
		});

	}

	public ArrayList<Integer> prayersFromVarbit(int varbit)
	{
		final BitSet bits = BitSet.valueOf(new long[] {varbit});
		final ArrayList<Integer> prayers = new ArrayList<>();

		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1))
		{
			prayers.add(i);
		}
		return prayers;
	}

	public int[] prayerWidgetsFromVarbit(int varbit)
	{
		int[] slots = new int[29];
		final BitSet bits = BitSet.valueOf(new long[] {varbit});

		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1))
		{
			int index = PrayerEnum.GetOriginalWidgetIndex(i);
			slots[index] = 1;
		}
		return slots;
	}


	void CleanBankSlotWidgets(){
		Widget bankLockWidget = client.getWidget(GROUP_BANK_SLOTS,CHILD_BANK_SLOTS_CONTAINER);
		if(bankLockWidget != null)
		{

			int[] currentSlots = lockedBankSlotsFromVarbit(client.getVarbitValue(VarbitID.BANK_SIDE_SLOT_OVERVIEW));

			Widget[] slots = bankLockWidget.getChildren();
			if(slots == null || slots.length < 56)
				return;

			for(int i=28; i<56; i++)
			{
				if (slots[i] == null)
					continue;

				//offset index by 28 so we reference the stored bit via its outline widget
				int idx = i-28;

				slots[i].setTextColor(currentSlots[idx] == 1 ? BANK_LOCK_LOCKED_COLOR : BANK_LOCK_DEFAULT_COLOR);

			}

		}
	}

	void HandleBankLocks(boolean showHelper){
		if(!showHelper){
			CleanBankSlotWidgets();
			return;
		}

		Widget bankLockWidget = client.getWidget(GROUP_BANK_SLOTS,CHILD_BANK_SLOTS_CONTAINER);
		if(bankLockWidget != null){

			int[] currentSlots = lockedBankSlotsFromVarbit(client.getVarbitValue(VarbitID.BANK_SIDE_SLOT_OVERVIEW));
			int[] intendedSlots = lockedBankSlotsFromVarbit(data.lockedBankSlots);

			if(Arrays.equals(currentSlots,intendedSlots)){
				return;
			}

			Widget[] slots = bankLockWidget.getChildren();
			if(slots == null || slots.length < 56)
				return;

			for(int i=28; i<56; i++){
				if(slots[i] == null)
					continue;

				//script 85 and 7785 effect color of the outline
				//85 is for click, 7785 is for hover, both need to be handled.
				//slot 28-55 occupy the outline element, 0-27 are the actual item which we don't care about

				int slotColor = slots[i].getTextColor();
				boolean slotHovered = slotColor == BANK_LOCK_DEFAULT_HOVER_COLOR || slotColor == BANK_LOCK_LOCKED_HOVER_COLOR;

				//offset index by 28 so we reference the stored bit via its outline widget
				int idx = i-28;
				int currentOccupied = currentSlots[idx];
				int intendedOccupied = intendedSlots[idx];

				boolean slotNeedsUnset = currentOccupied == 1 && (currentOccupied != intendedOccupied);
				boolean slotNeedsSet = currentOccupied == 0 && (currentOccupied != intendedOccupied);
				boolean currentSlotCorrect = currentOccupied == 1 && (currentOccupied == intendedOccupied);

				if(slotNeedsUnset || slotNeedsSet){
					//if the slot needs to be modified to match stored slots, color it green
					slots[i].setTextColor(slotHovered ? BANK_LOCK_DEFAULT_HOVER_COLOR : BANK_LOCK_NEEDS_CHANGE_COLOR);
				}else if(currentSlotCorrect && slotHovered){
					//the slot is correct and hovered, make the hover white same as others for visual consistency
					slots[i].setTextColor(BANK_LOCK_DEFAULT_HOVER_COLOR);
				}else if(!slotHovered && currentOccupied == 0){
					//the slot is not set/hovered and matches no other checks, therefore it's the least visibly useful slot as such we set it to black to make the others stand out
					slots[i].setTextColor(BANK_LOCK_DIM_COLOR);
				}

			}
		}
	}


	void HandleQuickPrayers(boolean showHelper){
		UpdateQuickPrayerLockButton(showHelper);
		Widget quickPrayersWidget = client.getWidget(GROUP_QUICK_PRAYERS, CHILD_QUICK_PRAYERS_CONTAINER);
		if (quickPrayersWidget != null)
		{
			int[] currentSlots = prayerWidgetsFromVarbit(client.getVarbitValue(VarbitID.QUICKPRAYER_SELECTED));
			int[] intendedSlots = prayerWidgetsFromVarbit(data.quickPrayers);

			Widget[] slots = quickPrayersWidget.getChildren();
			if (slots == null || slots.length < 87)
				return;

			if (freeEditPrayerMode || !helperActive || !showHelper || Arrays.equals(currentSlots, intendedSlots))
			{
				for(int i=0; i<87; i++){
					slots[i].setHidden(false);
				}
				return;
			}

			boolean[] missing  = new boolean[29];
			boolean[] unwanted = new boolean[29];
			for (int i = 0; i < 29; i++)
			{
				missing[i] = intendedSlots[i] != 0 && currentSlots[i] == 0;
				unwanted[i] = currentSlots[i] != 0 && intendedSlots[i] == 0;
			}

			for (int i = 0; i < 29; i++)
			{
				boolean show = missing[i];

				if (unwanted[i])
				{
					//showing a prayer is redundant if clicking a missing prayer will deselect this one
					boolean redundant = false;
					for (int m = 0; m < 29 && !redundant; m++)
					{
						if (missing[m] && PrayerEnum.conflicts(i, m))
							redundant = true;
					}
					show = !redundant;
				}

				slots[29 + i * 2].setHidden(!show);
				slots[30 + i * 2].setHidden(!show);
				slots[PrayerEnum.WidgetIndexToBit(i)].setHidden(!show);
			}
		}
	}

	private void UpdateQuickPrayerLockButton(boolean shown) {
		Widget parent = client.getWidget(GROUP_QUICK_PRAYERS,CHILD_QUICK_PRAYERS_PARENT);
		if (parent == null) return;

		boolean found = false;
		if (lockButton != null) {
			for (Widget dynamicChild : parent.getDynamicChildren())
			{
				if (dynamicChild == lockButton) {
					found = true;
					lockButton.setHidden(!shown);
					UpdateQuickPrayerLockAction();
					break;
				}
			}
		}
		if (!found || lockButton == null) {
			lockButton = parent.createChild(-1, WidgetType.GRAPHIC);

			lockButton.setOriginalHeight(22);
			lockButton.setOriginalWidth(29);
			lockButton.setYPositionMode(0);
			lockButton.setXPositionMode(0);
			lockButton.setOriginalX(20);
			lockButton.setOriginalY(232);
			lockButton.setSpriteId(1342);

			lockButton.setOnOpListener((JavaScriptCallback) (e) -> ToggleQuickPrayerLock());
			lockButton.setHasListener(true);
			lockButton.revalidate();
			lockButton.setHidden(!shown);
			UpdateQuickPrayerLockAction();
		}
	}

	void ToggleQuickPrayerLock(){
		freeEditPrayerMode = !freeEditPrayerMode;
		UpdateQuickPrayerLockAction();
		HandleQuickPrayers(true);
	}

	void UpdateQuickPrayerLockAction(){
		lockButton.setAction(0, freeEditPrayerMode ? "Lock to saved character settings" : "Ignore saved character settings");
		lockButton.setOpacity(freeEditPrayerMode ? 200 : 0);
	}

	Widget GetNestedWidget(int group,int child, int nestedChild){
		Widget parentWidget = client.getWidget(group,child);
		if(parentWidget != null){
			Widget[] children = parentWidget.getChildren();
			if(children != null && children.length > nestedChild){
				return children[nestedChild];
			}
		}
		return null;
	}

	private String getDesiredOption(int currentChild) {
		switch (currentChild) {
			case CHILD_PLAYER_ATTACK_PRIORITY_DROPDOWN:
			case CHILD_PLAYER_ATTACK_PRIORITY_CURRENT:
				return PLAYER_ATTACK_OPTIONS[data.playerAttackOps];
			case CHILD_NPC_ATTACK_PRIORITY_DROPDOWN:
			case CHILD_NPC_ATTACK_PRIORITY_CURRENT:
				return NPC_ATTACK_OPTIONS[data.npcAttackOps];
			case CHILD_GAME_CLIENT_DROPDOWN:
			case CHILD_GAME_CLIENT_CURRENT:
				return GAME_CLIENT_LAYOUT_OPTIONS[data.clientOps];
			default: return "";
		}
	}


	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if(!helperActive)
			return;

		if(data == null)
			return;

		int id = event.getScriptId();
		switch (id){
			case SCRIPT_BANK_OPEN:
			case SCRIPT_BANK_OPEN2:
				HandleBankLocks(configCheckBankLockSlots);
				break;
			case SCRIPT_QUICK_PRAYERS_REFRESH:
				HandleQuickPrayers(configCheckQuickPrayers);
				break;
			case SCRIPT_SETTINGS_DROPDOWN_SELECTED:
			case SCRIPT_SETTINGS_DROPDOWN_OPEN:
				RevalidateDropDowns();
			break;
			case SCRIPT_OVERALL_SETTINGS_INIT:
			case SCRIPT_OVERALL_SETTINGS_SEARCH_CHANGED:
			case SCRIPT_OVERALL_SETTINGS_TAB_CHANGED:
				HandleFkeys(fKeysHandled);
				break;
			case SCRIPT_OVERALL_SETTINGS_DROPDOWN_OPEN:
				RevalidateFkeyDropdown();
				break;
		}
	}

	public void RevalidateFkeyDropdown(){
		Widget dropDownPanelWidget = client.getWidget(GROUP_OVERALL_SETTINGS,CHILD_OVERALL_SETTINGS_DROPDOWN_PANEL);
		if(dropDownPanelWidget != null){
			modifiedFKeyIndex = -1;
			for(int i = 0; i< FKeyButtonWidgets.length; i++){
				Widget w = FKeyButtonWidgets[i];
				if(w.getSpriteId() != FKEY_DROPDOWN_OPEN_SPRITE_ID){
					modifiedFKeyIndex = i;
					break;
				}
			}

			//match found
			if(modifiedFKeyIndex != -1){

				Widget fkeyDropDownWidget = client.getWidget(GROUP_OVERALL_SETTINGS, CHILD_OVERALL_SETTINGS_DROPDOWN_CONTAINER);

				if(fkeyDropDownWidget == null)
					return;

				Widget[] children = fkeyDropDownWidget.getChildren();

				//starts 4 indicies in, increasing by 3 for every fkey
				if(children != null){
					if(!FkeyMatchesStoredSingle(modifiedFKeyIndex)){
						//storedfkeys technically start at 1 (because 0 is none) thus the -1
						int index = 4 + ((data.fKeys[modifiedFKeyIndex] - 1) *3);
						modifiedFkeyDDWidget = children[index];
					}
				}

			}
		}
	}

	public void HandleFkeys(boolean showHelper){

		if(!showHelper)
			return;

		InitFkeyWidgets();
	}

	public void InitFkeyWidgets(){
		FKeyTextWidgets = new Widget[14];
		FKeyButtonWidgets = new Widget[14];
		FKeyWidgetsFound = false;
		Widget settingsWidget = client.getWidget(GROUP_OVERALL_SETTINGS,CHILD_OVERALL_SETTINGS_PARENT_CONTAINER);

		if(settingsWidget == null)
			return;

		Widget[] children = settingsWidget.getChildren();

		if(children != null){

			for(int i=0; i<children.length; i++){

				Widget child = children[i];
				if(children[i] == null)
					continue;
				//first text widget is 8 indexs after header, each following is 9 after
				//however the varbit indicies are in an entirely seperate order to the widget which would make parsing a pain so we store it in the varbits order
				if(child.getText().equals(FKeyHeaderTitleText)){
					FKeyWidgetsFound = true;
					int lastIndex = i + 8;
					for(int j=0; j<FKeyTextWidgets.length; j++){
						int adjustedChildIndex = lastIndex + (j*9);
						int varbitIndex = F_KEY_VARBIT_ORDER_INDICIES[j];
						FKeyTextWidgets[varbitIndex] = children[adjustedChildIndex];
						FKeyButtonWidgets[varbitIndex] = children[adjustedChildIndex-1];
					}
				}

			}

		}
	}

	public int[] lockedBankSlotsFromVarbit(int varbit)
	{
		int[] slots = new int[28];
		final BitSet bits = BitSet.valueOf(new long[] {varbit});

		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1))
		{
			slots[i] = 1;
		}
		return slots;
	}

	Widget wPlayerAttackOptionCurrent,
			wPlayerAttackOptionDesired,
			wNPCAttackOptionCurrent,
			wNPCAttackOptionDesired,
			wGameClientOptionCurrent,
			wGameClientOptionDesired;

	void RevalidateDropDowns(){
		wPlayerAttackOptionCurrent = GetNestedWidget(GROUP_SETTINGS_DROPDOWNS, CHILD_PLAYER_ATTACK_PRIORITY_CURRENT,4);
		wPlayerAttackOptionDesired = GetNestedWidget(GROUP_SETTINGS_DROPDOWNS, CHILD_PLAYER_ATTACK_PRIORITY_DROPDOWN,data.playerAttackOps+1);

		wNPCAttackOptionCurrent = GetNestedWidget(GROUP_SETTINGS_DROPDOWNS, CHILD_NPC_ATTACK_PRIORITY_CURRENT,4);
		wNPCAttackOptionDesired = GetNestedWidget(GROUP_SETTINGS_DROPDOWNS, CHILD_NPC_ATTACK_PRIORITY_DROPDOWN,data.npcAttackOps+1);

		wGameClientOptionCurrent = GetNestedWidget(GROUP_SETTINGS_DROPDOWNS, CHILD_GAME_CLIENT_CURRENT,4);
		wGameClientOptionDesired = GetNestedWidget(GROUP_SETTINGS_DROPDOWNS, CHILD_GAME_CLIENT_DROPDOWN,data.clientOps+1);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e){
		int group = e.getGroupId();

		if(data == null)
			return;

		if(group == GROUP_SETTINGS_DROPDOWNS){
			RevalidateDropDowns();
		}

		//normally you'd only want to do this if the pouch does not match the setting (helperActive), however runepouch is unique in that you likely want to top off your runes even if the contents are correct
		if(e.getGroupId() == GROUP_RUNEPOUCH_LOADOUTS){
			swappedRunePouchIndicies = null;
			if(ShouldSortRunePouch())
				UpdateRunePouchWidget();
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick){

		if(data == null && client.getGameState() == GameState.LOGGED_IN && client.getTopLevelInterfaceId() != TOPLEVEL_LOGIN_CONFIRM){
			SaveToConfig();
			if(data != null)
			{
				overlay.SetupImageData();
				overlay.SetupRunePouch();
				overlay.SetupPrayers();
				if (!helperActive)
				{
					ToggleOverlay();
				}
			}
			return;
		}
	}

	public boolean SpellbookMatch(){
		int lunarSwapped = client.getVarbitValue(VarbitID.LUNAR_SPELLBOOK_CHANGE);
		//if lunar swap active, our active spellbook is lunar no matter what
		int spellbook = lunarSwapped == 1 ? 2 : client.getVarbitValue(VarbitID.SPELLBOOK);
		return (spellbook == data.spellbook);
	}

	public boolean GameClientMatch(){
		return (client.getTopLevelInterfaceId() == GAME_CLIENT_COMPONENT_IDS.get(data.clientOps));
	}

	public boolean NPCAttackOptionsMatch(){
		return (client.getVarpValue(VarPlayerID.OPTION_ATTACKPRIORITY_NPC) == data.npcAttackOps);
	}

	public boolean PlayerAttackOptionsMatch(){
		return (client.getVarpValue(VarPlayerID.OPTION_ATTACKPRIORITY) == data.playerAttackOps);
	}

	public boolean PlayerAttackOptionsMatchWidget(){
		if(wPlayerAttackOptionCurrent == null){
			return PlayerAttackOptionsMatch();
		}else{
			String selected = wPlayerAttackOptionCurrent.getText();
			String desired = getDesiredOption(CHILD_PLAYER_ATTACK_PRIORITY_DROPDOWN);
			return !desired.isEmpty() && selected.equals(desired);
		}
	}

	public boolean NPCAttackOptionsMatchWidget(){
		if(wNPCAttackOptionCurrent == null){
			return NPCAttackOptionsMatch();
		}else{
			String selected = wNPCAttackOptionCurrent.getText();
			String desired = getDesiredOption(CHILD_NPC_ATTACK_PRIORITY_DROPDOWN);
			return !desired.isEmpty() && selected.equals(desired);
		}
	}

	public boolean GameClientOptionsMatchWidget(){
		if(wGameClientOptionCurrent == null){
			return GameClientMatch();
		}else{
			String selected = wGameClientOptionCurrent.getText();
			String desired = getDesiredOption(CHILD_GAME_CLIENT_DROPDOWN);
			return !desired.isEmpty() && selected.equals(desired);
		}
	}

	public boolean ShouldSortRunePouch(){
		return ((helperActive && configCheckRunePouch && !RunePouchMatchesStored()) || configAlwaysSortRunePouch);
	}

	public int GetKeyIndex(String key){
		if (key == null || key.equals("None"))
		{
			return 0;
		}

		if (key.equals("ESC"))
		{
			return 13;
		}

		if (key.matches("F([1-9]|1[0-2])"))
		{
			return Integer.parseInt(key.substring(1));
		}

		return 0;
	}

	public void RefreshCurrentFKeys(){
		if(FKeyTextWidgets[0] != null){
			//earlier check, as varbits are not set instantly, gives better visual feedback
			currentFkeys = new int[]{
					GetKeyIndex(FKeyTextWidgets[0].getText()),
					GetKeyIndex(FKeyTextWidgets[1].getText()),
					GetKeyIndex(FKeyTextWidgets[2].getText()),
					GetKeyIndex(FKeyTextWidgets[3].getText()),
					GetKeyIndex(FKeyTextWidgets[4].getText()),
					GetKeyIndex(FKeyTextWidgets[5].getText()),
					GetKeyIndex(FKeyTextWidgets[6].getText()),
					GetKeyIndex(FKeyTextWidgets[7].getText()),
					GetKeyIndex(FKeyTextWidgets[8].getText()),
					GetKeyIndex(FKeyTextWidgets[9].getText()),
					GetKeyIndex(FKeyTextWidgets[10].getText()),
					GetKeyIndex(FKeyTextWidgets[11].getText()),
					GetKeyIndex(FKeyTextWidgets[12].getText()),
					GetKeyIndex(FKeyTextWidgets[13].getText()),
			};
			return;
		}
		//regular check, page not open instant feedback not needed
		currentFkeys = new int[] {
				client.getVarbitValue(VarbitID.STONE_COMBAT_KEY),
				client.getVarbitValue(VarbitID.STONE_STATS_KEY),
				client.getVarbitValue(VarbitID.STONE_JOURNAL_KEY),
				client.getVarbitValue(VarbitID.STONE_INV_KEY),
				client.getVarbitValue(VarbitID.STONE_WORN_KEY),
				client.getVarbitValue(VarbitID.STONE_PRAYER_KEY),
				client.getVarbitValue(VarbitID.STONE_MAGIC_KEY),
				client.getVarbitValue(VarbitID.STONE_FRIENDS_KEY),
				client.getVarbitValue(VarbitID.STONE_ACCOUNT_KEY),
				client.getVarbitValue(VarbitID.STONE_LOGOUT_KEY),
				client.getVarbitValue(VarbitID.STONE_OPTIONS1_KEY),
				client.getVarbitValue(VarbitID.STONE_OPTIONS2_KEY),
				client.getVarbitValue(VarbitID.STONE_CLANCHAT_KEY),
				client.getVarbitValue(VarbitID.STONE_MUSIC_KEY),
		};
	}

	//check specific fkey
	public boolean FkeyMatchesStoredSingle(int index){
		//not checking, assume valid
		if(!checkedFKeys[index])
			return true;
		RefreshCurrentFKeys();
		return (data.fKeys[index] == currentFkeys[index]);
	}

	//check all fkeys
	public boolean FkeyMatchesStoredAll(){
		RefreshCurrentFKeys();

		for(int i=0; i<currentFkeys.length; i++){
			//not checking, skip the key
			if(!checkedFKeys[i])
				continue;
			//any keymismatch, invalid
			if(currentFkeys[i] != data.fKeys[i])
				return false;
		}

		//no mismatches found, valid
		return true;
	}

	public boolean RunePouchMatchesStored(){
		Set<Integer> storedSet = Arrays.stream(data.runePouch)
				.boxed()
				.collect(Collectors.toCollection(TreeSet::new));

		Set<Integer> currentSet = new TreeSet<>(List.of(
				client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_1),
				client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_2),
				client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_3),
				client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_4)));
		return storedSet.equals(currentSet);
	}

	//Swap the first loadout that matches desired runes with the top most loadout
	public void UpdateRunePouchWidget(){
		//temp data representing config of desired runepouch
		Set<Integer> storedRunePouchSet = Arrays.stream(data.runePouch)
				.boxed()
				.collect(Collectors.toCollection(TreeSet::new));

		for(int i=0; i<RUNEPOUCH_LOADOUT_INDICIES.length; i++){
			Widget loadOutWidget = client.getWidget(GROUP_RUNEPOUCH_LOADOUTS,RUNEPOUCH_LOADOUT_INDICIES[i]);
			if(loadOutWidget != null)
			{
				Set<Integer> loadOutRunePouch = new TreeSet<>();

				for (int j = 1; j < 5; j++)
				{
					Widget runeWidget = loadOutWidget.getChild(j);
					if(runeWidget != null)
					{
						//convert widgets itemId to its varbit index to compare against stored
						loadOutRunePouch.add(RunePouchData.GetVarbit(runeWidget.getItemId()));
					}
				}

				//swap the first loadout that matches desired runes and swap its position with the top most loadout
				if(i != 0 && loadOutRunePouch.equals(storedRunePouchSet)){
					SwapRPLoadout(i,0);
					break;
				}

			}
		}
	}

	public void SwapRPLoadout(int indexFrom, int indexTo){
		Widget loadOutWidget = client.getWidget(GROUP_RUNEPOUCH_LOADOUTS,RUNEPOUCH_LOADOUT_INDICIES[indexFrom]);
		if(loadOutWidget == null)
			return;

		//temp store the location of matching loadout
		int x = loadOutWidget.getOriginalX();
		int y = loadOutWidget.getOriginalY();

		Widget topLoadoutWidget = client.getWidget(GROUP_RUNEPOUCH_LOADOUTS,RUNEPOUCH_LOADOUT_INDICIES[indexTo]);
		if(topLoadoutWidget == null)
			return;

		//set position of loadout to the topmost position
		loadOutWidget.setPos(topLoadoutWidget.getOriginalX(),topLoadoutWidget.getOriginalY());
		loadOutWidget.revalidate();

		//set position of topmost loadout to the stored position of the matching loadout
		topLoadoutWidget.setPos(x, y);
		topLoadoutWidget.revalidate();

		//store index to swap back on config change
		swappedRunePouchIndicies = new int[]{indexFrom,indexTo};
	}

	public void UndoLastRPLoadout(){
		if(swappedRunePouchIndicies != null){
			SwapRPLoadout(swappedRunePouchIndicies[0], swappedRunePouchIndicies[1]);
			swappedRunePouchIndicies = null;
		}
	}

}
