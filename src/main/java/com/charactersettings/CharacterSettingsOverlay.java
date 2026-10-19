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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import java.awt.event.MouseEvent;
import net.runelite.client.input.MouseAdapter;
public class CharacterSettingsOverlay extends Overlay
{
    private static final String MESSAGE_ANY = "Character settings don't match!";
    private static final String MESSAGE_RESIZE_MODE = "-Game client layout";
    private static final String MESSAGE_SPELLBOOK = "-Spellbook";
    private static final String MESSAGE_AUTO_RETALIATE = "-Auto retaliate";

    private static final String MESSAGE_NPC_ATTACK_OPS = "-NPC attack options";

    private static final String MESSAGE_PLAYER_ATTACK_OPS = "-Player attack options";

    private static final String MESSAGE_ACCEPT_AID = "-Accept aid";

    private static final String MESSAGE_MASTER_VOLUME = "-Master volume mute";
    private static final String MESSAGE_MUSIC_VOLUME = "-Music volume mute";

    private static final String MESSAGE_LOCAL_SOUND_VOLUME = "-Local sounds volume mute";

    private static final String MESSAGE_AREA_SOUND_VOLUME = "-Area sounds volume mute";

    private static final String MESSAGE_RUNE_POUCH = "-Rune pouch";

    private static final String MESSAGE_QUICK_PRAYERS = "-Quick prayers";
    private static final String MESSAGE_LOCKED_BANK_SLOTS = "-Locked bank slots";
    private static final String MESSAGE_FKEYS = "-F Keys";

    private final PanelComponent panelComponent = new PanelComponent();

    private CharacterSettingsPlugin plugin;
    private final Client client;
    private final ItemManager itemManager;
    private final SpriteManager spriteManager;
    public RunePouchData runePouchData;
    public PrayerData prayerData;

    IconLineComponent cachedQuickPrayerRow = null;
    IconLineComponent cachedRunePouchRow = null;
    private final OverlayButton overwriteButton = new OverlayButton("Overwrite saved settings");
    public boolean panelActive = false;

    private final OverlayButton dismissButton = new OverlayButton("Dismiss",Color.RED);
    private static final int CORNER_BTN_HEIGHT = 14;
    private static final int CORNER_BTN_WIDTH = 55;
    private static final int CORNER_BTN_MARGIN = 3;

    private static final String[] SPELLBOOK_NAMES = new String[] {"Normals","Ancients","Lunars","Arceuus"};

    @Inject
    public CharacterSettingsOverlay(CharacterSettingsPlugin plugin, Client client, ItemManager itemManager, SpriteManager spriteManager)
    {
        this.plugin = plugin;
        this.client = client;
        this.itemManager = itemManager;
        this.spriteManager = spriteManager;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public final MouseAdapter mouseListener = new MouseAdapter()
    {
        @Override
        public MouseEvent mousePressed(MouseEvent e)
        {
            if (plugin.helperActive && panelActive && e.getButton() == MouseEvent.BUTTON1)
            {
                if(overwriteButton.getScreenBounds().contains(e.getPoint()))
                {
                    plugin.SaveToConfig();
                    e.consume();
                }
                if(dismissButton.getScreenBounds().contains(e.getPoint()))
                {
                    plugin.ToggleOverlay();
                    e.consume();
                }
            }
            return e;
        }
    };

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if(!plugin.helperActive)
            return null;

        if(plugin.data == null)
            return null;

        panelComponent.getChildren().clear();

        int textWidth = graphics.getFontMetrics().stringWidth(MESSAGE_ANY);
        panelComponent.setPreferredSize(new Dimension(textWidth + CORNER_BTN_WIDTH + 10, 0));

        panelComponent.getChildren().add(LineComponent.builder()
                .left(MESSAGE_ANY).leftColor(Color.RED).build());

        boolean bankOpen = false;
        Widget bankWidget = client.getWidget(12,0);
        if(bankWidget != null && !bankWidget.isHidden()){
            bankOpen = true;
        }


        boolean settingsOpen = false;
        Widget settingsWidget = client.getWidget(134,0);
        if(settingsWidget != null && !settingsWidget.isHidden()){
            settingsOpen = true;
        }

        boolean condensed = bankOpen || settingsOpen;


        if((bankOpen || !condensed) && plugin.configCheckRunePouch && !plugin.RunePouchMatchesStored()){
            panelComponent.getChildren().add(cachedRunePouchRow);
        }

        int quickPrayers = client.getVarbitValue(VarbitID.QUICKPRAYER_SELECTED);
        if(!condensed && plugin.configCheckQuickPrayers && quickPrayers != plugin.data.quickPrayers){
            panelComponent.getChildren().add(cachedQuickPrayerRow);
        }


        if(!condensed && !plugin.SpellbookMatch()){
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_SPELLBOOK+" ("+SPELLBOOK_NAMES[plugin.data.spellbook]+")").leftColor(Color.white).build());
        }

        int lockedBankSlots = client.getVarbitValue(VarbitID.BANK_SIDE_SLOT_OVERVIEW);
        if((bankOpen || !condensed) && plugin.configCheckBankLockSlots && lockedBankSlots != plugin.data.lockedBankSlots){
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_LOCKED_BANK_SLOTS).leftColor(Color.white).build());
        }

        if(!condensed && plugin.configCheckGameClient && !plugin.GameClientMatch())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_RESIZE_MODE).leftColor(Color.white).build());
        }

        if((settingsOpen || !condensed) && !plugin.FkeyMatchesStoredAll())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_FKEYS).leftColor(Color.white).build());
        }

        int autoRetaliate = client.getVarpValue(VarPlayerID.OPTION_NODEF);
        if(!condensed && plugin.configCheckAutoRetaliate && autoRetaliate != plugin.data.autoRetaliate)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_AUTO_RETALIATE).leftColor(Color.white).build());
        }


        if(!condensed && !plugin.NPCAttackOptionsMatch())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_NPC_ATTACK_OPS).leftColor(Color.white).build());
        }

        if(!condensed && !plugin.PlayerAttackOptionsMatch())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_PLAYER_ATTACK_OPS).leftColor(Color.white).build());
        }

        int acceptAid = client.getVarbitValue(VarbitID.OPTION_ACCEPTAID);
        if(!condensed && plugin.configCheckAcceptAid && acceptAid != plugin.data.acceptAid)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_ACCEPT_AID).leftColor(Color.white).build());
        }

        int masterVolume = client.getVarpValue(VarPlayerID.OPTION_MASTER_VOLUME);
        if(!condensed && plugin.configCheckMasterVolumeMute && ((plugin.data.masterVolume > 0 && masterVolume == 0) || (plugin.data.masterVolume == 0 && masterVolume > 0)))
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_MASTER_VOLUME).leftColor(Color.white).build());
        }

        int musicVolume = client.getVarpValue(VarPlayerID.OPTION_MUSIC);
        if(!condensed && plugin.configCheckMusicVolumeMute && ((plugin.data.musicVolume > 0 && musicVolume == 0) || (plugin.data.musicVolume == 0 && musicVolume > 0)))
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_MUSIC_VOLUME).leftColor(Color.white).build());
        }

        int localVolume = client.getVarpValue(VarPlayerID.OPTION_SOUNDS);
        if(!condensed && plugin.configCheckLocalVolumeMute && ((plugin.data.localSoundVolume > 0 && localVolume == 0) || (plugin.data.localSoundVolume == 0 && localVolume > 0)))
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_LOCAL_SOUND_VOLUME).leftColor(Color.white).build());
        }

        int areaVolume = client.getVarpValue(VarPlayerID.OPTION_AREASOUNDS);
        if(!condensed && plugin.configCheckAreaVolumeMute && ((plugin.data.areaSoundVolume > 0 && areaVolume == 0) || (plugin.data.areaSoundVolume == 0 && areaVolume > 0)))
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MESSAGE_AREA_SOUND_VOLUME).leftColor(Color.white).build());
        }



        panelActive = panelComponent.getChildren().size() > 1;

        if(!panelActive)
        {
            return null;
        } else {
            net.runelite.api.Point m = client.getMouseCanvasPosition();
            overwriteButton.hovered = overwriteButton.getScreenBounds().contains(m.getX(), m.getY());
            panelComponent.getChildren().add(overwriteButton);
        }


        Dimension dim = panelComponent.render(graphics);

        if (panelActive)
        {
            net.runelite.api.Point m = client.getMouseCanvasPosition();
            dismissButton.hovered = dismissButton.getScreenBounds().contains(m.getX(), m.getY());

            dismissButton.setPreferredSize(new Dimension(CORNER_BTN_WIDTH, CORNER_BTN_HEIGHT));
            dismissButton.setPreferredLocation(new Point(
                    dim.width - CORNER_BTN_WIDTH - CORNER_BTN_MARGIN,
                    CORNER_BTN_MARGIN));
            dismissButton.render(graphics);
        }

        return dim;
    }
    private ArrayList<BufferedImage> runePouchIcons(int[] runes)
    {
        ArrayList<BufferedImage> icons = new ArrayList<>();
        for (int r : runes)
        {
            BufferedImage img = runePouchData.GetIcon(r);
            if (img != null) icons.add(img);
        }
        return icons;
    }

    private ArrayList<BufferedImage> prayerIcons(int varbit)
    {
        ArrayList<BufferedImage> icons = new ArrayList<>();
        for (int p : plugin.prayersFromVarbit(varbit))
        {
            BufferedImage img = prayerData.GetIcon(p);
            if (img != null) icons.add(img);
        }
        return icons;
    }

    public void SetupImageData(){
        this.runePouchData = new RunePouchData(itemManager);
        this.prayerData = new PrayerData(spriteManager);
    }

    public void SetupPrayers()
    {
        cachedQuickPrayerRow = new IconLineComponent(MESSAGE_QUICK_PRAYERS, Color.WHITE, prayerIcons(plugin.data.quickPrayers),20,2);
    }

    public void SetupRunePouch()
    {
        cachedRunePouchRow = new IconLineComponent(MESSAGE_RUNE_POUCH, Color.WHITE, runePouchIcons(plugin.data.runePouch),33,0);
    }
}
