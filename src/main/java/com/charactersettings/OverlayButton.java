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
import java.awt.geom.AffineTransform;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

public class OverlayButton implements LayoutableRenderableEntity
{
    private final String text;
    private final Rectangle bounds = new Rectangle();
    private final Rectangle screenBounds = new Rectangle();
    private Point preferredLocation = new Point();
    private Dimension preferredSize = new Dimension(0, 18);
    public boolean hovered = false;

    private Color colorNormal = new Color(70, 60, 45);
    private Color colorHovered = new Color(110, 90, 60);

    public OverlayButton(String text)
    {
        this.text = text;
    }

    public OverlayButton(String text,Color colorHovered)
    {
        this.text = text;
        this.colorHovered = colorHovered;
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        int w = preferredSize.width > 0 ? preferredSize.width : 100;
        int h = preferredSize.height > 0 ? preferredSize.height : 18;

        AffineTransform t = g.getTransform();
        screenBounds.setBounds(
                (int) t.getTranslateX() + preferredLocation.x,
                (int) t.getTranslateY() + preferredLocation.y,
                w, h);

        int x = preferredLocation.x;
        int y = preferredLocation.y;

        g.setColor(hovered ? colorHovered : colorNormal);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);

        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString(text,
                x + (w - fm.stringWidth(text)) / 2,
                y + (h + fm.getAscent() - fm.getDescent()) / 2);

        bounds.setBounds(x, y, w, h);
        return new Dimension(w, h);
    }

    public Rectangle getScreenBounds() { return screenBounds; }

    @Override public Rectangle getBounds() { return bounds; }
    @Override public void setPreferredLocation(Point p) { this.preferredLocation = p; }
    @Override public void setPreferredSize(Dimension d) { this.preferredSize = d; }
}