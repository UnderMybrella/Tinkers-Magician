package org.abimon.mods.minecraft.tinkersMagician;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.tools.ToolCore;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ItemWand extends ItemWandCasting implements IModifyable {

	public static final ItemWandTemplate base = TinkersMagician.wandTemplate;

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs p_150895_2_, List p_150895_3_)
	{
		p_150895_3_.add(new ItemStack(item, 1, 0));
	}

	@Override
	public String getBaseTagName() {
		return getBaseTag();
	}
	
	public static String getBaseTag() {
		return "InfiTool";
	}

	@Override
	public String getModifyType() {
		return "Wand";
	}

	@Override
	public String[] getTraits() {
		return new String[]{"casting", "wand"};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon (ItemStack stack, int renderPass)
	{
		return base.getIcon(stack, renderPass);
	}

	public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4){
		base.addInformation(stack, player, list, par4);
		System.out.println(stack.getTagCompound().toString());
		super.addInformation(stack, player, list, par4);
	}

	public int getMaxVis(ItemStack item){
		if(getTag(item).hasKey("TotalDurability"))
			return getTag(item).getInteger("TotalDurability") * 25;
		return 25000;
	}
	
	public float getConsumptionModifier(ItemStack item, EntityPlayer player, Aspect aspect, boolean something){
		float base = 1.0f + ((float) getTag(item).getInteger("HarvestLevel") / 10f);
		float baseModified = 110 / base;
		int levels = getTag(item).getInteger(TinkersMagician.AVERAGE_CONSUMPTION_KEY);
		baseModified /= (1.0f + (levels / 10f));
		return baseModified / 100f;
	}

	public AspectList getAspectsWithRoom(ItemStack item){
		return super.getAspectsWithRoom(item);
	}
	
	public static NBTTagCompound getTag(ItemStack item){
		if(!item.hasTagCompound())
			item.setTagCompound(new NBTTagCompound());
		if(!item.getTagCompound().hasKey(getBaseTag()))
			item.getTagCompound().setTag(getBaseTag(), new NBTTagCompound());
		return item.getTagCompound().getCompoundTag(getBaseTag());
	}
	
	@Override
	public WandCap getCap(ItemStack item){
		if(getTag(item).getInteger("HarvestLevel") < 3)
			return ConfigItems.WAND_CAP_IRON;
		return ConfigItems.WAND_CAP_GOLD;
	}
	@Override
	public WandRod getRod(ItemStack item){
		if(getTag(item).getInteger("HarvestLevel") < 3)
			return ConfigItems.WAND_ROD_WOOD;
		return ConfigItems.WAND_ROD_GREATWOOD;
	}
}
