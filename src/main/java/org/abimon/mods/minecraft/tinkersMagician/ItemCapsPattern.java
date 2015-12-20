package org.abimon.mods.minecraft.tinkersMagician;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.crafting.PatternBuilder.MaterialSet;
import tconstruct.library.util.IPattern;

public class ItemCapsPattern extends Item implements IPattern{

	@Override
	public int getPatternCost(ItemStack pattern) {
		return 1;
	}

	@Override
	public ItemStack getPatternOutput(ItemStack pattern, ItemStack input, MaterialSet set) {
		return new ItemStack(TinkersMagician.inertCap, 1, set.materialID);
	}
	
}
