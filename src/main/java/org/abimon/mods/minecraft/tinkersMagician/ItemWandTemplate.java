package org.abimon.mods.minecraft.tinkersMagician;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolCore;
import tconstruct.tools.TinkerTools;
import thaumcraft.api.aspects.Aspect;

public class ItemWandTemplate extends ToolCore {

	public ItemWandTemplate() {
		super(0);
	}

	@Override
	public String getIconSuffix(int partType) {
		switch(partType){
		case 3:
			return "_cap_rear";
		case 2:
			return "_wand_rod";
		case 0:
			return "_cap_head";
		default:
			return "";
		}
	}

	@Override
	public String getModifyType ()
	{
		return "Wand";
	}

	@Override
	public String getEffectSuffix() {
		return "_effect";
	}

	@Override
	public String getDefaultFolder() {
		return "wand";
	}

	@Override
	public Item getHeadItem() {
		return TinkersMagician.inertCap;
	}

	@Override
	public Item getAccessoryItem() {
		return TinkersMagician.inertCap;
	}

	@Override
	public Item getHandleItem ()
	{
		return TinkerTools.toolRod;
	}

	@Override
	public String[] getTraits() {
		return new String[]{"wand"};
	}

	public int durabilityTypeHandle ()
	{
		return 2;
	}

	public float getDurabilityModifier ()
	{
		return 1f;
	}


	public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4){
		super.addInformation(stack, player, list, par4);
		if(stack.getItem() == this){
			list.add("Drain Rate Per Tick");
			String rates = "";
			for(Aspect aspect : Aspect.getPrimalAspects())
				rates += "§" + aspect.getChatcolor() + (ItemWand.getDrainRate(stack, aspect) + 1) + EnumChatFormatting.WHITE + " | ";
			list.add(rates.substring(0, rates.length() - 2));
			list.add(EnumChatFormatting.GOLD + "Capacity: " + TConstructRegistry.getMaterial(ItemWand.getTag(stack).getInteger("Handle")).durability * 8 / 100);
		}
	}

	public ItemStack onItemRightClick(ItemStack item, World p_77659_2_, EntityPlayer p_77659_3_)
	{
		ItemStack itemStack = new ItemStack(TinkersMagician.wand, 1, item.getItemDamage());
		itemStack.setTagCompound(item.getTagCompound());
		return itemStack;
	}

	public String getToolName(){
		return "wand";
	}

	public String getDefaultTexturePath()
	{
		return TinkersMagician.MODID + ":" + getDefaultFolder();
	}

}
