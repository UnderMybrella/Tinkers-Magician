package org.abimon.mods.minecraft.tinkersMagician;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.tools.ToolCore;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.blocks.BlockAiry;
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
		list.add("Drain Rate Per Tick");
		String rates = "";
		for(Aspect aspect : Aspect.getPrimalAspects())
			rates += "§" + aspect.getChatcolor() + (getDrainRate(stack, aspect) + 1) + EnumChatFormatting.WHITE + " | ";
		list.add(rates.substring(0, rates.length() - 2));
		super.addInformation(stack, player, list, par4);
	}

	public int getMaxVis(ItemStack item){
		if(getTag(item).hasKey("TotalDurability"))
			return TConstructRegistry.getMaterial(getTag(item).getInteger("Handle")).durability * 25;
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
	
	public static int getDrainRate(ItemStack stack, Aspect aspect){
		float baseAmount = getTag(stack).getFloat("MiningSpeed") / 100 / 5;
		int a = (int) (baseAmount * getRate((aspect.getTag().charAt(0) + "").toUpperCase() + aspect.getTag().substring(1), getTag(stack)));
		return a;
	}

	public static float getRate(String key, NBTTagCompound nbt){
		if(nbt.hasKey(key + "Rate"))
			return nbt.getFloat(key + "Rate");
		else
			return 1.0f;
	}

	//Fancy
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
		if(world.isRemote)
			return false;

		getTag(stack).removeTag("UsingX");
		getTag(stack).removeTag("UsingY");
		getTag(stack).removeTag("UsingZ");

		if(world.getBlock(x, y, z) instanceof BlockAiry){
			getTag(stack).setInteger("UsingX", x);
			getTag(stack).setInteger("UsingY", y);
			getTag(stack).setInteger("UsingZ", z);
		}

		return false;
	}

	//Hacks
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		super.onUsingTick(stack, player, count);
		if(player.getEntityWorld().isRemote)
			return;

		Block block = player.worldObj.getBlock(getTag(stack).getInteger("UsingX"), getTag(stack).getInteger("UsingY"), getTag(stack).getInteger("UsingZ"));
		if(block instanceof BlockAiry){
			BlockAiry airy = (BlockAiry) block;
			TileEntity te = player.worldObj.getTileEntity(getTag(stack).getInteger("UsingX"), getTag(stack).getInteger("UsingY"), getTag(stack).getInteger("UsingZ"));
			if(te instanceof INode){
				INode node = (INode) te;
				for(Aspect aspect : Aspect.getPrimalAspects()){
					
					int a = getDrainRate(stack, aspect);

					if(node.getAspects().getAmount(aspect) > a){
						int remaining =  this.addVis(stack, aspect, a - 1, true);
						node.takeFromContainer(aspect, a - remaining - 1);
					}
				}

			}
		}
	}

	//Await
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int p_77615_4_) {
		super.onPlayerStoppedUsing(stack, world, player, p_77615_4_);

		if(world.isRemote)
			return;

		getTag(stack).removeTag("UsingX");
		getTag(stack).removeTag("UsingY");
		getTag(stack).removeTag("UsingZ");
	}

	@Override
	public int getFocusFrugal(ItemStack item){
		int frugal = super.getFocusFrugal(item);
		frugal += getTag(item).getInteger("FrugalAddition");	
		return frugal;
	}

	@Override
	public int getFocusPotency(ItemStack item){
		int potency = super.getFocusPotency(item);
		potency += getTag(item).getInteger("PotencyAddition");
		return potency;
	}
	
	@Override
	public int getFocusEnlarge(ItemStack item){
		int enlarge = super.getFocusEnlarge(item);
		enlarge += getTag(item).getInteger("EnlargeAddition");
		return enlarge;
	}
}
