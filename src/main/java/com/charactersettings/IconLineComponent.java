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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

public class IconLineComponent implements LayoutableRenderableEntity
{

    private static final int TEXT_GAP = 4;

    private final String label;
    private final Color labelColor;
    private final List<BufferedImage> icons;

    private final Rectangle bounds = new Rectangle();
    private Point preferredLocation = new Point();
    private Dimension preferredSize = new Dimension(ComponentConstants.STANDARD_WIDTH, 0);

    private int offSetX;
    private int iconGap;

    public IconLineComponent(String label, Color labelColor, List<BufferedImage> icons, int offsetX, int iconGap)
    {
        this.label = label;
        this.labelColor = labelColor;
        this.icons = icons;
        this.offSetX = offsetX;
        this.iconGap = iconGap;
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        FontMetrics fm = g.getFontMetrics();
        int iconH = icons.stream().mapToInt(BufferedImage::getHeight).max().orElse(0);
        int lineH = Math.max(fm.getHeight(), iconH);
        int x = preferredLocation.x;
        int y = preferredLocation.y;

        TextComponent text = new TextComponent();
        text.setText(label);
        text.setColor(labelColor);
        text.setPosition(new Point(x, y + (lineH + fm.getAscent() - fm.getDescent()) / 2));
        text.render(g);

        int ix = x + fm.stringWidth(label) + TEXT_GAP + offSetX;
        for (BufferedImage icon : icons)
        {
            g.drawImage(icon, ix, y + (lineH - icon.getHeight()) / 2, null);
            ix += icon.getWidth() + iconGap;
        }

        Dimension d = new Dimension(preferredSize.width, lineH);
        bounds.setLocation(preferredLocation);
        bounds.setSize(d);
        return d;
    }

    @Override public Rectangle getBounds() { return bounds; }
    @Override public void setPreferredLocation(Point p) { preferredLocation = p; }
    @Override public void setPreferredSize(Dimension d) { preferredSize = d; }
}