package org.abimon.mods.minecraft.tinkersMagician;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModInteger;

public abstract class ModCappedInteger extends ModInteger{

	int cap;
	int increase;
	
	String c;
	String tip;
	
	String[] tools;

	public ModCappedInteger(ItemStack[] items, String[] tools, int effect, String dataKey, int cap, int increase, String c, String tip) {
		super(items, effect, dataKey, increase, c, tip);
		
		this.tools = tools;
		
		this.cap = cap;
		this.increase = increase;
		
		this.c = c;
		this.tip = tip;
	}

	@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		if (tool.getItem() instanceof IModifyable)
		{
			IModifyable toolItem = (IModifyable) tool.getItem();
			if (!validType(toolItem))
				return false; 

			NBTTagCompound tags = tool.getTagCompound().getCompoundTag(toolItem.getBaseTagName());
			if (!tags.hasKey(key)){
				return tags.getInteger("Modifiers") > 0;
			}

			int keyPair[] = tags.getIntArray(key);
			if (keyPair[0] + increase <= keyPair[1])
				return true;

			else if (keyPair[0] == keyPair[1])
				return tags.getInteger("Modifiers") > 0;
		}

		return false;
	}

	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		int[] keyPair;
		int current = 0;
		if (tags.hasKey(key))
		{
			keyPair = tags.getIntArray(key);
			if (keyPair[0] % cap == 0)
			{
				keyPair[0] += increase;
				keyPair[1] += cap;
				tags.setIntArray(key, keyPair);

				int modifiers = tags.getInteger("Modifiers");
				modifiers -= 1;
				tags.setInteger("Modifiers", modifiers);
			}
			else
			{
				keyPair[0] += increase;
				tags.setIntArray(key, keyPair);
			}
			current = keyPair[0];
			updateModTag(tool, keyPair);
			realModify(input, tool);
		}
		else
		{
			int modifiers = tags.getInteger("Modifiers");
			modifiers -= 1;
			tags.setInteger("Modifiers", modifiers);
			String modName = c + tip + " (" + increase + "/" + cap + ")";
			int tooltipIndex = addToolTip(tool, c + tip, modName);
			keyPair = new int[] { increase, cap, tooltipIndex };
			current = keyPair[0];
			tags.setIntArray(key, keyPair);
		}
	}

	public abstract void realModify(ItemStack[] input, ItemStack tool);

	void updateModTag (ItemStack tool, int[] keys)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		String tip = "ModifierTip" + keys[2];
		String modName = c + this.tip + " (" + keys[0] + "/" + keys[1] + ")";
		tags.setString(tip, modName);
	}

	public boolean validType (IModifyable input)
	{
		for(String s : tools)
			if(input.getModifyType().equals(s))
				return true;
		return false;
	}
}
