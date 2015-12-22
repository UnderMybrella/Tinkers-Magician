package org.abimon.mods.minecraft.tinkersMagician;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import tconstruct.library.crafting.ToolRecipe;
import tconstruct.library.tools.ToolCore;
import tconstruct.tools.TinkerTools;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.items.wands.ItemWandCap;

public class WandRecipe extends ToolRecipe {
	public WandRecipe(){
		super(TinkersMagician.inertCap, TinkerTools.toolRod, TinkersMagician.inertCap, TinkersMagician.wandTemplate);
	}

	/**
	 * Check for InertCap OR WandCap
	 */
	public boolean validHead (Item input)
	{
		if(super.validHead(input))
			return true;
		for (WandCap wc : WandCap.caps.values())
			if(input != null)
				if(wc.getItem().getItem().getUnlocalizedName().equals(input.getUnlocalizedName()))
					return true;
		//			else
		//				System.out.println(input.getUnlocalizedName() + ":" + wc.getItem().getItem().getUnlocalizedName());
		return false;
	}

	/**
	 * Check for Tool Rod OR Wand Core
	 */
	public boolean validHandle (Item input)
	{
		if(super.validHandle(input))
			return true;
		if(input.getUnlocalizedName().toLowerCase().contains("toolrod"))
			return true;
		for (WandRod wc : WandRod.rods.values())
			if(input != null)
				if(wc.getItem().getItem().getUnlocalizedName().equals(input.getUnlocalizedName()))
					return true;
		return false;
	}

	/**
	 * Check for InertCap OR WandCap
	 */
	public boolean validAccessory (Item input)
	{
		return validHead(input);
	}
}
