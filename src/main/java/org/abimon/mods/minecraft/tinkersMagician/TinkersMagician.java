package org.abimon.mods.minecraft.tinkersMagician;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import sun.util.resources.cldr.wae.CalendarData_wae_CH;
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
import tconstruct.library.event.ToolBuildEvent;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.util.IPattern;
import tconstruct.library.util.IToolPart;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.TinkerTools.MaterialID;
import tconstruct.util.ItemHelper;
import tconstruct.weaponry.TinkerWeaponry;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.client.renderers.item.ItemWandRenderer;
import thaumcraft.client.renderers.models.gear.ModelWand;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import static net.minecraft.util.EnumChatFormatting.*;

@Mod(modid = TinkersMagician.MODID, version = TinkersMagician.VERSION, dependencies = "required-after:TConstruct; required-after:Thaumcraft")
public class TinkersMagician
{
	public static final String MODID = "tinkersmagician";
	public static final String VERSION = "1.0";

	public static Item capsPattern = new ItemCapsPattern().setUnlocalizedName(MODID + ":pattern").setTextureName(MODID + ":pattern_caps");
	public static Item inertCap = new DynamicToolPart("_cap", "ToolCaps", MODID);
	public static ItemWandTemplate wandTemplate = new ItemWandTemplate();
	public static Item wand = new ItemWand();

	public static final WandRecipe wandRecipe = new WandRecipe();

	public static final String AVERAGE_CONSUMPTION_KEY = "AverageConsumptionDecrease";

	public static Map<String, Integer> materialCapIDs = new HashMap<String, Integer>();
	public static Map<String, Integer> materialRodIDs = new HashMap<String, Integer>();

	public static final int CAP_MATERIAL = 16384;
	public static final int ROD_MATERIAL = 17385;
	
	public static final String CATEGORY_STRING = MODID + ":tinkersCategory";
	public static final String TINKERS_RESEARCH_STRING = MODID + ":tinkersResearch";
	
	public static ResearchItem tinkersResearch;
	
	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{

		MinecraftForge.EVENT_BUS.register(this);

		GameRegistry.registerItem(capsPattern, "capsPattern");
		GameRegistry.registerItem(inertCap, "inertCap");
		GameRegistry.registerItem(wand, "wand");
		GameRegistry.registerItem(wandTemplate, "wandTemplate");
		PatternBuilder.instance.addToolPattern((IPattern) capsPattern);
		StencilBuilder.registerStencil(StencilBuilder.getStencilCount() + 1, inertCap, 0);

		ToolBuilder.addCustomToolRecipe(wandRecipe);

		MinecraftForgeClient.registerItemRenderer(wandTemplate, new ToolCoreRenderer());
		MinecraftForgeClient.registerItemRenderer(wand, new WandRenderer());

		ModifyBuilder.registerModifier(new ModAverageConsumption(new ItemStack[]{new ItemStack(ConfigItems.itemShard, 1, 6)}, -1, AVERAGE_CONSUMPTION_KEY, 1, 1, GRAY.toString(), "Balanced Average"));
		ModifyBuilder.registerModifier(new ModFasterDrain(new ItemStack[]{new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 0)}, new String[]{"Wand"}, 2, "Fast Drain", 16, 1, RED.toString(), "Fast Drain"));

		ModifyBuilder.registerModifier(new ModCappedTag(new ItemStack[]{new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 1)}, new String[]{"Wand"}, 8, "Potency", 8, 1, DARK_PURPLE.toString(), "Potency", "PotencyAddition"));
		ModifyBuilder.registerModifier(new ModCappedTag(new ItemStack[]{new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 3)}, new String[]{"Wand"}, 8, "Frugal", 8, 1, DARK_PURPLE.toString(), "Frugal", "FrugalAddition"));
		ModifyBuilder.registerModifier(new ModCappedTag(new ItemStack[]{new ItemStack(ConfigItems.itemResource, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 5)}, new String[]{"Wand"}, 8, "Enlarge", 8, 1, DARK_PURPLE.toString(), "Enlarge", "EnlargeAddition"));

		ModifyBuilder.registerModifier(new ModExtraModifiers(new ItemStack[]{new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4), new ItemStack(ConfigItems.itemResource, 1, 7)}, new String[]{"Wand"}, -1, "ExtraModifiers"));

		addPattern();

		int i = 0;
		for(WandCap cap : WandCap.caps.values()){
			materialCapIDs.put(cap.getTag(), CAP_MATERIAL + (i++));
			TConstructRegistry.addtoolMaterial(materialCapIDs.get(cap.getTag()), new ToolMaterial((cap.getTag().charAt(0) + "").toUpperCase() + cap.getTag().substring(1).toLowerCase(), (cap.getTag().charAt(0) + "").toUpperCase() + cap.getTag().substring(1).toLowerCase(), (int) ((1.2 - cap.getBaseCostModifier()) * 10), 0, (int) ((1.2 - cap.getBaseCostModifier()) * 20), 0, 1.0f, 0, 0.0f, "§a", getColorFromLocation(true,cap.getItem(), cap.getTexture())));
		}

		i = 0;
		for(WandRod rod : WandRod.rods.values()){
			materialRodIDs.put(rod.getTag(), ROD_MATERIAL + (i++));
			TConstructRegistry.addtoolMaterial(materialRodIDs.get(rod.getTag()), new ToolMaterial((rod.getTag().charAt(0) + "").toUpperCase() + rod.getTag().substring(1).toLowerCase(), (rod.getTag().charAt(0) + "").toUpperCase() + rod.getTag().substring(1).toLowerCase(), 0, rod.getCapacity() * 100 / 8, 0, 0, 1.0f, 0, 0.0f, "§a", getColorFromLocation(false,rod.getItem(), rod.getTexture())));
		}
		
		CrucibleRecipe patternRecipe = new CrucibleRecipe(TINKERS_RESEARCH_STRING,new ItemStack(capsPattern), new ItemStack(TinkerTools.blankPattern), new AspectList().add(Aspect.MAGIC, 8));
		ThaumcraftApi.getCraftingRecipes().add(patternRecipe);//
		ItemStack itm = ToolBuilder.instance.buildTool(new ItemStack(inertCap), new ItemStack(TinkerTools.toolRod), new ItemStack(inertCap), "");
		//InfusionToolRecipes toolRecipe = new InfusionToolRecipes(TINKERS_RESEARCH_STRING, itm, 1, new AspectList().add(Aspect.MAGIC, 8), new ItemStack(ConfigItems.itemResource, 1, 4), new ItemStack[]{new ItemStack(inertCap), new ItemStack(TinkerTools.toolRod), new ItemStack(inertCap)});
		//ThaumcraftApi.getCraftingRecipes().add(toolRecipe);
		
		ResearchCategories.registerCategory(CATEGORY_STRING, new ResourceLocation("thaumcraft", "textures/items/alumentum.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
		tinkersResearch = new ResearchItem(TINKERS_RESEARCH_STRING, CATEGORY_STRING, new AspectList().add(Aspect.TOOL, 4).add(Aspect.MAGIC, 4).add(Aspect.EXCHANGE, 4), 0, 0, 1, new ItemStack(Items.diamond_sword)).setParents("ROD_greatwood").setPages(new ResearchPage(MODID + ":tinkersResearch.1"), new ResearchPage(patternRecipe), new ResearchPage(MODID + ":tinkersResearch.2")).registerResearchItem();
	}

	public static void addPattern(){
		for(MaterialSet material : PatternBuilder.instance.materialSets.values()){
			TConstructRegistry.addPartMapping(capsPattern, 0, material.materialID, new ItemStack(inertCap));
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

	@SubscribeEvent
	public void toolBuild(ToolBuildEvent event){
		if(event.headStack != null && wandRecipe.validHead(event.headStack.getItem()))
			if(event.handleStack != null && wandRecipe.validHandle(event.handleStack.getItem()))
				if(event.accessoryStack != null && wandRecipe.validAccessory(event.accessoryStack.getItem())){
					if(event.headStack.getItem() instanceof IToolPart);
					else
						event.headStack = new ItemStack(inertCap, 1, getCapMaterial(event.headStack));
					if(event.handleStack.getItem() instanceof IToolPart);
					else
						event.handleStack = new ItemStack(TinkerTools.toolRod, 1, getRodMaterial(event.handleStack));
					if(event.accessoryStack.getItem() instanceof IToolPart);
					else
						event.accessoryStack = new ItemStack(inertCap, 1, getCapMaterial(event.accessoryStack));
				}
	}

	public static ToolMaterial constructCapMaterial(ItemStack cap){
		for (WandCap wc : WandCap.caps.values()) {
			if (checkItemEquals(cap, wc.getItem())){
				return new ToolMaterial((wc.getTag().charAt(0) + "").toUpperCase() + wc.getTag().substring(1).toLowerCase(), (wc.getTag().charAt(0) + "").toUpperCase() + wc.getTag().substring(1).toLowerCase(), (int) ((1.2 - wc.getBaseCostModifier()) * 10), 0, (int) ((1.2 - wc.getBaseCostModifier()) * 20), 0, 1.0f, 0, 0.0f, "§a", getColorFromLocation(true,wc.getItem(), wc.getTexture()));
			}
		}
		return null;
	}

	public static int getCapMaterial(ItemStack cap){
		for (WandCap wc : WandCap.caps.values()) {
			if (checkItemEquals(cap, wc.getItem())){
				if(materialCapIDs.get(wc.getTag()) == null){
					materialCapIDs.put(wc.getTag(), CAP_MATERIAL + materialCapIDs.size());
					TConstructRegistry.addtoolMaterial(materialCapIDs.get(wc.getTag()), new ToolMaterial((wc.getTag().charAt(0) + "").toUpperCase() + wc.getTag().substring(1).toLowerCase(), (wc.getTag().charAt(0) + "").toUpperCase() + wc.getTag().substring(1).toLowerCase(), (int) ((wc.getBaseCostModifier() - 1.0f) * 10), 0, 0, 0, 1.0f, 0, 0.0f, "§a", getColorFromLocation(true,wc.getItem(), wc.getTexture())));
				}
				return materialCapIDs.get(wc.getTag());
			}
		}
		return -1;
	}

	public static int getRodMaterial(ItemStack rod){
		for (WandRod wc : WandRod.rods.values()) {
			if (checkItemEquals(rod, wc.getItem())){
				if(materialRodIDs.get(wc.getTag()) == null){
					materialRodIDs.put(wc.getTag(), ROD_MATERIAL + materialCapIDs.size());
					TConstructRegistry.addtoolMaterial(materialRodIDs.get(wc.getTag()), new ToolMaterial((wc.getTag().charAt(0) + "").toUpperCase() + wc.getTag().substring(1).toLowerCase(), (wc.getTag().charAt(0) + "").toUpperCase() + wc.getTag().substring(1).toLowerCase(), 0, wc.getCapacity() * 100 / 8, 0, 0, 1.0f, 0, 0.0f, "§a", getColorFromLocation(false, wc.getItem(), wc.getTexture())));
				}
				return materialRodIDs.get(wc.getTag());
			}
		}
		return -1;
	}

	public static int getColorFromLocation(boolean cap, ItemStack item, ResourceLocation textureLoc){
		//TextureAtlasSprite sprite = ((TextureAtlasSprite) item.getItem().getIconFromDamage(0));
		try{
			Class<?> itemClass = item.getItem().getClass();
			File jar = new File(itemClass.getProtectionDomain().getCodeSource().getLocation().getPath().split("!")[0].replace("%20", " ").substring(5));
			ZipFile jarFile = new ZipFile(jar.getAbsolutePath());
			InputStream in = jarFile.getInputStream(new ZipEntry("assets/" + textureLoc.getResourceDomain() + "/" + textureLoc.getResourcePath()));
			BufferedImage img = ImageIO.read(in);
			jarFile.close();
			in.close();
			if(cap){
				Color color = new Color(img.getRGB(img.getWidth() / 8, img.getHeight() / 8));
				return color.getRGB();
			}
			else{
				Color color = new Color(img.getRGB(img.getWidth() / 8, img.getHeight() / 2));
				return color.getRGB();
			}
		}
		catch(Throwable th){
			th.printStackTrace();
		}
		return 0;
	}

	public static boolean checkItemEquals(ItemStack one, ItemStack two){
		if(((one == null) && (two != null)) || ((one != null) && (two == null))) {
			return false;
		} 

		return (one.getItem() == two.getItem()) && ((!one.hasTagCompound()) || (ItemStack.areItemStackTagsEqual(one, two))) && ((one.getItemDamage() == 32767) || (one.getItemDamage() == two.getItemDamage()));
	}
}
