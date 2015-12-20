package org.abimon.mods.minecraft.tinkersMagician;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import tconstruct.TConstruct;
import tconstruct.client.ToolCoreRenderer;
import tconstruct.items.tools.Pickaxe;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.PatternBuilder.MaterialSet;
import tconstruct.library.crafting.StencilBuilder;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.crafting.ToolRecipe;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.IPattern;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.TinkerTools.MaterialID;
import tconstruct.util.ItemHelper;
import tconstruct.weaponry.TinkerWeaponry;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.renderers.item.ItemWandRenderer;
import thaumcraft.client.renderers.models.gear.ModelWand;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.abimon.jscout.jScout;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import static net.minecraft.util.EnumChatFormatting.*;

@Mod(modid = TinkersMagician.MODID, version = TinkersMagician.VERSION)
public class TinkersMagician
{
	public static final String MODID = "tinkersmagician";
	public static final String VERSION = "1.0";

	public static Item capsPattern = new ItemCapsPattern().setUnlocalizedName(MODID + ":pattern").setTextureName(MODID + ":pattern_caps");
	public static Item inertCap = new DynamicToolPart("_cap", "ToolCaps", MODID);
	public static Item cap = new ItemActiveWandCap();
	public static ItemWandTemplate wandTemplate = new ItemWandTemplate();
	public static Item wand = new ItemWand();

	public static final String AVERAGE_CONSUMPTION_KEY = "AverageConsumptionDecrease";

	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{
		//jScout.scout(new File("/Users/undermybrella/Thaumcraft.jar"));
		GameRegistry.registerItem(capsPattern, "capsPattern");
		GameRegistry.registerItem(inertCap, "inertCap");
		GameRegistry.registerItem(cap, "cap");
		GameRegistry.registerItem(wand, "wand");
		GameRegistry.registerItem(wandTemplate, "wandTemplate");
		PatternBuilder.instance.addToolPattern((IPattern) capsPattern);
		StencilBuilder.registerStencil(StencilBuilder.getStencilCount() + 1, inertCap, 0);

		ToolRecipe wandRecipe = new ToolRecipe(inertCap, TinkerTools.toolRod, inertCap, wandTemplate);

		List<ToolRecipe> clone = new LinkedList<ToolRecipe>();
		clone.addAll(ToolBuilder.instance.combos);
		try{
			Field field = ToolRecipe.class.getDeclaredField("handleList");
			field.setAccessible(true);
			for(ToolRecipe recipe : clone)
				if(recipe.getType() instanceof Pickaxe){
					List<Item> recipeHandles = (List<Item>) field.get(recipe);
					List<Item> wandHandles = (List<Item>) field.get(wandRecipe);
					
					wandHandles.addAll(recipeHandles);
					field.set(wandRecipe, wandHandles);
				}
		}
		catch(Throwable th){}
		ToolBuilder.addCustomToolRecipe(wandRecipe);

		MinecraftForgeClient.registerItemRenderer(wandTemplate, new ToolCoreRenderer());
		MinecraftForgeClient.registerItemRenderer(wand, new WandRenderer());

		ModifyBuilder.registerModifier(new ModAverageConsumption(new ItemStack[]{new ItemStack(ConfigItems.itemShard, 1, 6)}, -1, AVERAGE_CONSUMPTION_KEY, 1, 1, GRAY.toString(), "Balanced Average"));

		addPattern();

		//		TConstructRegistry.addToolMaterial(MaterialID.Thaumium + 1, "Void Metal", 6, 800, 1400, 4, 2.6F, 0, 0f, "\u00A75", 0x321153);
		//		PatternBuilder.instance.registerFullMaterial(new ItemStack((Item) ItemHelper.getStaticItem("itemResource", "thaumcraft.common.config.ConfigItems"), 1, 16), 2, "Void Metal", new ItemStack(TinkerTools.toolShard, 1, 32), new ItemStack(TinkerTools.toolRod, 1, 32), 32);
		//		for (int meta = 0; meta < TinkerTools.patternOutputs.length; meta++)
		//		{
		//			if (TinkerTools.patternOutputs[meta] != null)
		//				TConstructRegistry.addPartMapping(TinkerTools.woodPattern, meta + 1, 32, new ItemStack(TinkerTools.patternOutputs[meta], 1, 32));
		//		}
		//
		//		TConstructRegistry.addDefaultToolPartMaterial(MaterialID.Thaumium + 1);
		//		TConstructRegistry.addDefaultShardMaterial(MaterialID.Thaumium + 1);
	}

	public static void addPattern(){
		for(MaterialSet material : PatternBuilder.instance.materialSets.values()){
			TConstructRegistry.addPartMapping(capsPattern, 0, material.materialID, new ItemStack(inertCap));
			ThaumcraftApi.addCrucibleRecipe("RESEARCH", new ItemStack(cap, 1, material.materialID), new ItemStack(inertCap, 1, material.materialID), new AspectList().add(Aspect.AIR, 8));
		}
	}

	public static TextureAtlasSprite createNew(String iconName){
		try{
			Constructor con = TextureAtlasSprite.class.getDeclaredConstructor(String.class);
			con.setAccessible(true);
			return (TextureAtlasSprite) con.newInstance(iconName);
		}
		catch(Throwable th){
			th.printStackTrace();
		}
		return null;
	}
}
