package org.abimon.mods.minecraft.tinkersMagician;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.crafting.ToolBuilder;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;

public class InfusionToolRecipes extends InfusionRecipe {

	public static final String INFUSION_KEY = "ToolToBuildThroughInfusion";
	
	public InfusionToolRecipes(String research, Object output, int inst, AspectList aspects2, ItemStack input,
			ItemStack[] recipe) {
		super(research, output, inst, aspects2, input, recipe);
	}

	public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
		System.out.println(input);
		for(int i = 0; i < input.size(); i++)
		{
			ItemStack head = input.get(i);
			ItemStack accessory = input.get(i+1 >= input.size() ? i+1-input.size() : i+1);
			ItemStack handle = input.size() > 2 ? input.get(i+2 >= input.size() ? i+2-input.size() : i+2) : null;
			ItemStack extra = input.size() > 3 ? input.get(i+3 >= input.size() ? i+3-input.size() : i+3) : null;
			ItemStack item = ToolBuilder.instance.buildTool(head, handle, accessory, extra, "");
			if(item != null){
//				if(!central.hasTagCompound())
//					central.setTagCompound(new NBTTagCompound());
//				central.getTagCompound().removeTag(INFUSION_KEY);
//				central.getTagCompound().setTag(INFUSION_KEY, item.writeToNBT(new NBTTagCompound()));
				return true;
			}
		}
		return true;
	}

	public Object getRecipeOutput() {
		return super.getRecipeOutput(getRecipeInput());
	}

	public Object getRecipeOutput(ItemStack input) {
		System.out.println("Attempting Recipe Output");
		return input.hasTagCompound() ? input.getTagCompound().hasKey(INFUSION_KEY) ? ItemStack.loadItemStackFromNBT(input.getTagCompound().getCompoundTag(INFUSION_KEY)) : super.getRecipeOutput(input): super.getRecipeOutput(input);
	}

}
