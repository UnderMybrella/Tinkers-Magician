package org.abimon.mods.minecraft.tinkersMagician;

import net.minecraft.item.ItemStack;
import tconstruct.library.modifier.IModifyable;
import tconstruct.modifiers.tools.ModInteger;

public class ModAverageConsumption extends ModInteger {

	public ModAverageConsumption(ItemStack[] items, int effect, String dataKey, int increase1, int increase2, String c,
			String tip) {
		super(items, effect, dataKey, increase1, increase2, c, tip);
	}
	
    public boolean validType (IModifyable input)
    {
        return input.getModifyType().equals("Wand");
    }
}
