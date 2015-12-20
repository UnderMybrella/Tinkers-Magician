package org.abimon.mods.minecraft.tinkersMagician;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModRedstone;

public class ModFasterDrain extends ModCappedInteger {

	public ModFasterDrain(ItemStack[] items, String[] tools, int effect, String dataKey, int cap, int increase,
			String c, String tip) {
		super(items, tools, effect, dataKey, cap, increase, c, tip);
	}

	@Override
	public void realModify(ItemStack[] input, ItemStack tool) {}
	
	public void modify (ItemStack[] input, ItemStack tool){
		super.modify(input, tool);
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		int miningSpeed = tags.getInteger("MiningSpeed");
        int boost = 8 + ((tags.getIntArray(key)[0] - 1) / cap * 2);
        Item temp = tool.getItem();
        miningSpeed += (increase * boost);
        tags.setInteger("MiningSpeed", miningSpeed);

        String[] type = { "MiningSpeed2", "MiningSpeedHandle", "MiningSpeedExtra" };

        for (int i = 0; i < 3; i++)
        {
            if (tags.hasKey(type[i]))
            {
                int speed = tags.getInteger(type[i]);
                speed += (increase * boost);
                tags.setInteger(type[i], speed);
            }
        }
	}

}
