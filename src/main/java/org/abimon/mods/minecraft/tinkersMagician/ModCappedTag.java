package org.abimon.mods.minecraft.tinkersMagician;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModCappedTag extends ModCappedInteger {
	
	public String tagIncrease;

	public ModCappedTag(ItemStack[] items, String[] tools, int effect, String dataKey, int cap, int increase, String c,
			String tip, String tagIncrease) {
		super(items, tools, effect, dataKey, cap, increase, c, tip);
		this.tagIncrease = tagIncrease;
	}

	@Override
	public void realModify(ItemStack[] input, ItemStack tool) {
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		tags.setInteger(tagIncrease, tags.getInteger(tagIncrease) + 1);
	}

}
