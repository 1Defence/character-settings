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
import java.awt.geom.Area;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;


public class GenericWidgetOverlay extends Overlay
{
    private CharacterSettingsPlugin plugin;
    private final Client client;
    private final ItemManager itemManager;
    private final SpriteManager spriteManager;
    private static Color RED_COLOR = new Color(255,0,0);
    private static Color GREEN_COLOR = new Color(144,238,144);
    private static Color WHITE_COLOR_FILL = new Color(255, 255, 255, 90);


    @Inject
    public GenericWidgetOverlay(CharacterSettingsPlugin plugin, Client client, ItemManager itemManager, SpriteManager spriteManager)
    {
        this.plugin = plugin;
        this.client = client;
        this.itemManager = itemManager;
        this.spriteManager = spriteManager;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(PRIORITY_HIGHEST);
        drawAfterInterface(InterfaceID.TOPLEVEL_DISPLAY);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        if(!plugin.helperActive)
            return null;
        if(client.getTopLevelInterfaceId() == CharacterSettingsPlugin.TOPLEVEL_LOGIN_CONFIRM)
            return null;
        if(plugin.data == null)
            return null;

        int lockedBankSlots = client.getVarbitValue(VarbitID.BANK_SIDE_SLOT_OVERVIEW);
        if(plugin.configCheckBankLockSlots && lockedBankSlots != plugin.data.lockedBankSlots){

            Widget bankSlotsOverView = client.getWidget(906, 3);
            if(!isVisible(bankSlotsOverView)){
                //highlight the desired button if the bank slot overlay is not currently open.
                Widget bankSlotsButton = client.getWidget(12, 141);
                Widget bankToolBar = client.getWidget(12, 106);

                if(isVisible(bankSlotsButton)){
                    ColorWidget(bankSlotsButton, g,true);
                }else if(isVisible(bankToolBar)){
                    ColorWidget(bankToolBar, g,true);
                }

            }
        }

        if(plugin.FKeyWidgetsFound){
            //draw the intended fkey if drop down open
            if(!plugin.FkeyMatchesStoredSingle(plugin.modifiedFKeyIndex) && isVisible(plugin.modifiedFkeyDDWidget)){
                ColorWidgetWithinBounds(plugin.modifiedFkeyDDWidget,plugin.modifiedFkeyDDWidget.getParent(),g,GREEN_COLOR,false);
            }

            for(int i=0; i<plugin.FKeyTextWidgets.length; i++){
                if(!plugin.FkeyMatchesStoredSingle(i))
                {
                    if(isVisible(plugin.modifiedFkeyDDWidget)){
                        //dropdown open, draw whats not hidden by the dropdowns overlap
                        ColorWidgetOutside(plugin.FKeyTextWidgets[i], plugin.modifiedFkeyDDWidget.getParent(),g, RED_COLOR, false);
                    } else {
                        //dropdown closed, draw full bounds as normal
                        ColorWidget(plugin.FKeyTextWidgets[i], g, RED_COLOR, false);
                    }
                }
            }
        }



        if(plugin.configCheckPlayerAttackPriority && !plugin.PlayerAttackOptionsMatchWidget()){
            ColorDropDown(plugin.wPlayerAttackOptionCurrent,plugin.wPlayerAttackOptionDesired,g);
        }

        //dont show if player drop down open to prevent visual overlap
        if(plugin.configCheckNPCAttackPriority && !isVisible(plugin.wPlayerAttackOptionDesired) && !plugin.NPCAttackOptionsMatchWidget()){
            ColorDropDown(plugin.wNPCAttackOptionCurrent,plugin.wNPCAttackOptionDesired,g);
        }

        if(plugin.configCheckGameClient && !plugin.GameClientOptionsMatchWidget()){
            ColorDropDown(plugin.wGameClientOptionCurrent,plugin.wGameClientOptionDesired,g);
        }


        int acceptAid = client.getVarbitValue(VarbitID.OPTION_ACCEPTAID);
        if(plugin.configCheckAcceptAid && acceptAid != plugin.data.acceptAid)
        {
            Widget acceptAidWidget = client.getWidget(116, 29);

            if(isVisible(plugin.wNPCAttackOptionDesired)){
                //dropdown open, draw whats not hidden by the dropdowns overlap
                ColorWidgetOutside(acceptAidWidget,plugin.wNPCAttackOptionDesired.getParent(),g,GREEN_COLOR,true);
            }else if(isVisible(plugin.wPlayerAttackOptionDesired)){
                //dropdown open, draw whats not hidden by the dropdowns overlap
                ColorWidgetOutside(acceptAidWidget,plugin.wPlayerAttackOptionDesired.getParent(),g,GREEN_COLOR,true);
            }else {
                //dropdown closed, draw full bounds as normal
                ColorWidget(acceptAidWidget, g,true);
            }

        }

        int autoRetaliate = client.getVarpValue(VarPlayerID.OPTION_NODEF);
        if(plugin.configCheckAutoRetaliate && autoRetaliate != plugin.data.autoRetaliate)
        {
            Widget autoRetaliateWidget = client.getWidget(593, 34);
            ColorWidget(autoRetaliateWidget, g,true);
        }

        int masterVolume = client.getVarpValue(VarPlayerID.OPTION_MASTER_VOLUME);
        if(plugin.configCheckMasterVolumeMute && ((plugin.data.masterVolume > 0 && masterVolume == 0) || (plugin.data.masterVolume == 0 && masterVolume > 0)))
        {
            Widget masterVolumeWidget = client.getWidget(116, 96);
            ColorWidget(masterVolumeWidget, g,true);
        }

        int musicVolume = client.getVarpValue(VarPlayerID.OPTION_MUSIC);
        if(plugin.configCheckMusicVolumeMute && ((plugin.data.musicVolume > 0 && musicVolume == 0) || (plugin.data.musicVolume == 0 && musicVolume > 0)))
        {
            Widget musicVolumeWidget = client.getWidget(116, 110);
            ColorWidget(musicVolumeWidget, g,true);
        }

        int localVolume = client.getVarpValue(VarPlayerID.OPTION_SOUNDS);
        if(plugin.configCheckLocalVolumeMute && ((plugin.data.localSoundVolume > 0 && localVolume == 0) || (plugin.data.localSoundVolume == 0 && localVolume > 0)))
        {
            Widget localVolumeWidget = client.getWidget(116, 124);
            ColorWidget(localVolumeWidget, g,true);
        }

        int areaVolume = client.getVarpValue(VarPlayerID.OPTION_AREASOUNDS);
        if(plugin.configCheckAreaVolumeMute && ((plugin.data.areaSoundVolume > 0 && areaVolume == 0) || (plugin.data.areaSoundVolume == 0 && areaVolume > 0)))
        {
            Widget areaVolumeWidget = client.getWidget(116, 138);
            ColorWidget(areaVolumeWidget, g,true);
        }

        return null;
    }


    public void ColorDropDown(Widget currentOption, Widget desiredOption,Graphics2D g){
        if(isVisible(currentOption)){
            ColorWidget(currentOption,g,RED_COLOR,false);
        }
        if(isVisible(desiredOption)){
            ColorWidget(desiredOption,g,GREEN_COLOR,false);
        }
    }

    private static boolean isVisible(Widget w)
    {
        return w != null && !w.isHidden();
    }

    public void ColorWidget(Widget w, Graphics2D g,boolean fill){
        ColorWidget(w,g,GREEN_COLOR,fill);
    }
    public void ColorWidget(Widget w, Graphics2D g,Color color,boolean fill){
        if (w != null && !w.isHidden())
        {
            Rectangle bounds = w.getBounds();
            //temporarily valid but off screen during resorting, dont render.
            if(bounds.x == -1 && bounds.y == -1)
                return;

            if(fill)
            {
                g.setColor(WHITE_COLOR_FILL);
                g.fill(bounds);
            }

            g.setColor(color);
            g.setStroke(new BasicStroke(2));
            g.draw(w.getBounds());
        }
    }



    public void ColorWidgetOutside(Widget w, Widget container, Graphics2D g, Color color, boolean fill) {
        if (w == null || w.isHidden() || container == null || container.isHidden()) {
            return;
        }

        Rectangle bounds = w.getBounds();
        //temporarily valid but off screen during resorting, dont render.
        if(bounds.x == -1 && bounds.y == -1)
            return;

        Rectangle clip = container.getBounds();

        //fully within bounds early return
        if (clip.contains(bounds)) {
            return;
        }

        boolean fullyOutside = !bounds.intersects(clip);

        Shape oldClip = g.getClip();
        Stroke oldStroke = g.getStroke();
        Color oldColor = g.getColor();

        //partial overlap, clip bounds
        if (!fullyOutside) {
            Area outside = new Area(bounds);
            outside.subtract(new Area(clip));
            g.setClip(outside);
        }

        if (fill) {
            g.setColor(WHITE_COLOR_FILL);
            g.fill(bounds);
        }

        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        g.draw(bounds);

        // Restore graphics state
        g.setClip(oldClip);
        g.setStroke(oldStroke);
        g.setColor(oldColor);
    }

    public void ColorWidgetWithinBounds(Widget w, Widget container, Graphics2D g, Color color, boolean fill) {
        if (w == null || w.isHidden() || container == null || container.isHidden()) {
            return;
        }

        Rectangle bounds = w.getBounds();
        //temporarily valid but off screen during resorting, dont render.
        if(bounds.x == -1 && bounds.y == -1)
            return;

        Rectangle clip = container.getBounds();

        //nothing to draw
        if (!bounds.intersects(clip)) {
            return;
        }

        Rectangle visible = bounds.intersection(clip);

        if (fill) {
            g.setColor(WHITE_COLOR_FILL);
            g.fill(visible);
        }

        g.setColor(color);
        g.setStroke(new BasicStroke(2));

        int left   = visible.x;
        int right  = visible.x + visible.width;
        int top    = visible.y;
        int bottom = visible.y + visible.height;

        // Only draw an edge if it's the widget's real edge (not clipped off)
        boolean drawTop    = bounds.y == visible.y;
        boolean drawBottom = bounds.y + bounds.height == bottom;
        boolean drawLeft   = bounds.x == visible.x;
        boolean drawRight  = bounds.x + bounds.width == right;

        if (drawTop)    g.drawLine(left, top, right, top);
        if (drawBottom) g.drawLine(left, bottom, right, bottom);
        if (drawLeft)   g.drawLine(left, top, left, bottom);
        if (drawRight)  g.drawLine(right, top, right, bottom);
    }

}
