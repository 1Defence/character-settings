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

import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class RunePouchData
{
    public static int[] runeIds = new int[] {11526,556,555,557,554,558,562,560,565,564,561,563,559,566,9075,4695,4698,4696,4699,4694,4697,21880,28929,30843};
    private static final Map<Integer, Integer> ITEM_TO_INDEX = new HashMap<>();
    static
    {
        for (int i = 0; i < runeIds.length; i++)
        {
            ITEM_TO_INDEX.put(runeIds[i], i);
        }
    }

    public BufferedImage[] icons = new BufferedImage[24];
    int iconWidth = 18;
    int iconHeight = 16;

    public RunePouchData(ItemManager itemManager)
    {
        for (int i = 0; i < runeIds.length; i++)
        {
            final int idx = i;
            final AsyncBufferedImage img = itemManager.getImage(runeIds[i]);

            icons[idx] = ImageUtil.resizeImage(img,iconWidth,iconHeight);
        }
    }

    public BufferedImage GetIcon(int runeVarbit){
        return icons[runeVarbit];
    }

    public static int GetVarbit(int itemId)
    {
        return ITEM_TO_INDEX.getOrDefault(itemId, -1);
    }
}
