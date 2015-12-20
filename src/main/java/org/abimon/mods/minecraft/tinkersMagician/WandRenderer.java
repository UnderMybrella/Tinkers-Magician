package org.abimon.mods.minecraft.tinkersMagician;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import tconstruct.TConstruct;
import tconstruct.client.ToolCoreRenderer;

public class WandRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
//		if (!item.hasTagCompound())
//            return false;

        switch (type)
        {
        case ENTITY:
            return true;
        case EQUIPPED:
            GL11.glTranslatef(0.03f, 0F, -0.09375F);
        case EQUIPPED_FIRST_PERSON:
            return true;
        case INVENTORY:
            return true;
        default:
        case FIRST_PERSON_MAP:
            return false;
        }
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		 return handleRenderType(item, type) & helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal();
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if(item.getItem() instanceof ItemWand){
			ItemWand wand = (ItemWand) item.getItem();
			ItemStack itm = new ItemStack(wand.base, 1, item.getItemDamage());
			itm.setTagCompound(item.getTagCompound());
			IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(itm, type);
			if(renderer != null)
				renderer.renderItem(type, itm, data);
		}
	}

}
