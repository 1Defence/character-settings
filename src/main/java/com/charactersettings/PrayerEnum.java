/*
 * Copyright (c) 2020, Hydrox6 <ikada@protonmail.ch>
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.SpriteID;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
enum PrayerEnum
{
    THICK_SKIN(0, SpriteID.Prayeron.THICK_SKIN,0),
    BURST_OF_STRENGTH(1, SpriteID.Prayeron.BURST_OF_STRENGTH,1),
    CLARITY_OF_THOUGHT(2, SpriteID.Prayeron.CLARITY_OF_THOUGHT,2),
    ROCK_SKIN(3, SpriteID.Prayeron.ROCK_SKIN,5),
    SUPERHUMAN_STRENGTH(4, SpriteID.Prayeron.SUPERHUMAN_STRENGTH,6),
    IMPROVED_REFLEXES(5, SpriteID.Prayeron.IMPROVED_REFLEXES,7),
    RAPID_RESTORE(6, SpriteID.Prayeron.RAPID_RESTORE,8),
    RAPID_HEAL(7, SpriteID.Prayeron.RAPID_HEAL,9),
    PROTECT_ITEM(8, SpriteID.Prayeron.PROTECT_ITEM,10),
    STEEL_SKIN(9, SpriteID.Prayeron.STEEL_SKIN,13),
    ULTIMATE_STRENGTH(10, SpriteID.Prayeron.ULTIMATE_STRENGTH,14),
    INCREDIBLE_REFLEXES(11, SpriteID.Prayeron.INCREDIBLE_REFLEXES,15),
    PROTECT_FROM_MAGIC(12, SpriteID.Prayeron.PROTECT_FROM_MAGIC,16),
    PROTECT_FROM_MISSILES(13, SpriteID.Prayeron.PROTECT_FROM_MISSILES,17),
    PROTECT_FROM_MELEE(14, SpriteID.Prayeron.PROTECT_FROM_MELEE,18),
    RETRIBUTION(15, SpriteID.Prayeron.RETRIBUTION,21),
    REDEMPTION(16, SpriteID.Prayeron.REDEMPTION,22),
    SMITE(17,  SpriteID.Prayeron.SMITE,23),
    SHARP_EYE(18, SpriteID.Prayeron.SHARP_EYE,3),
    MYSTIC_WILL(19, SpriteID.Prayeron.MYSTIC_WILL,4),
    HAWK_EYE(20, SpriteID.Prayeron.HAWK_EYE,11),
    MYSTIC_LORE(21, SpriteID.Prayeron.MYSTIC_LORE,12),
    EAGLE_EYE(22, SpriteID.Prayeron.EAGLE_EYE,19),
    MYSTIC_MIGHT(23, SpriteID.Prayeron.MYSTIC_MIGHT,20),
    RIGOUR(24, SpriteID.Prayeron.RIGOUR,27),
    CHIVALRY(25, SpriteID.Prayeron.CHIVALRY,25),
    PIETY(26, SpriteID.Prayeron.PIETY,26),
    AUGURY(27, SpriteID.Prayeron.AUGURY,28),
    PRESERVE(28, SpriteID.Prayeron.PRESERVE,24);

    private final int bit;
    private final int spriteID;
    private final int originalIndex;

    private static final Map<Integer, PrayerEnum> PRAYERS = new HashMap<>();
    private static final int[] BIT_FOR_ORIGINAL_INDEX = new int[29];
    static
    {
        for (PrayerEnum p : values())
        {
            PRAYERS.put(p.bit, p);
            BIT_FOR_ORIGINAL_INDEX[p.originalIndex] = p.bit;
        }
    }

    static PrayerEnum get(int idx)
    {
        return PRAYERS.get(idx);
    }

    static int GetOriginalWidgetIndex(int bit){
        return get(bit).getOriginalIndex();
    }

    static int WidgetIndexToBit(int widgetIndex)
    {
        return BIT_FOR_ORIGINAL_INDEX[widgetIndex];
    }

    //Groups of prayers that can't be active together
    static final int[][] CONFLICT_GROUPS = {
            //Defence Thick Skin, Rock Skin, Steel Skin, Chivalry, Piety, Rigour, Augury
            {0, 5, 13, 25, 26, 27, 28},
            //Attack/Strength/Ranged/Magic boosts
            {1, 2, 3, 4, 6, 7, 11, 12, 14, 15, 19, 20, 25, 26, 27, 28},
            //Overheads Prot Magic, Prot Missiles, Prot Melee, Retribution, Redemption, Smite
            {16, 17, 18, 21, 22, 23},
    };

    static boolean conflicts(int a, int b)
    {
        if (a == b) return false;
        for (int[] group : CONFLICT_GROUPS)
        {
            boolean hasA = false, hasB = false;
            for (int p : group)
            {
                if (p == a) hasA = true;
                if (p == b) hasB = true;
            }
            if (hasA && hasB) return true;
        }
        return false;
    }
}