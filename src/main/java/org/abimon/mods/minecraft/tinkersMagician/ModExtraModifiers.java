package org.abimon.mods.minecraft.tinkersMagician;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;

public class ModExtraModifiers extends ItemModifier {

	String[] tools;
	
	public ModExtraModifiers(ItemStack[] recipe, String[] tools, int effect, String dataKey) {
		super(recipe, effect, dataKey);
		
		this.tools = tools;
	}

    protected boolean canModify (ItemStack input, ItemStack[] recipe){
		NBTTagCompound nbt = input.getTagCompound().getCompoundTag("InfiTool");
    	return nbt.getInteger(key) < 8;
    }
    
	@Override
	public void modify(ItemStack[] recipe, ItemStack input) {
		NBTTagCompound nbt = input.getTagCompound().getCompoundTag("InfiTool");
		nbt.setInteger("Modifiers", nbt.getInteger("Modifiers") + 1);
	}
	
	public boolean validType (IModifyable input)
	{
		for(String s : tools)
			if(input.getModifyType().equals(s))
				return true;
		return false;
	}

}
