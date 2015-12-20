package org.abimon.mods.minecraft.tinkersMagician;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import thaumcraft.api.wands.WandCap;

public class ItemActiveWandCap extends Item{

//	@SideOnly(Side.CLIENT)
//    public IIcon getIconFromDamage(int damage)
//    {
//        return TinkersMagician.inertCap.getIconFromDamage(damage);
//    }
    
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack item, int damage)
    {
        return TinkersMagician.inertCap.getColorFromItemStack(item, damage);
    }
    
    public void registerIcons (IIconRegister iconRegister){
    	try{
        	System.out.println(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        	File file = new File("WandCap.png");
        	System.out.println(file.getAbsolutePath());
        	TextureAtlasSprite itemIcon = TinkersMagician.createNew("Thingy");
        	BufferedImage img = ImageIO.read(file);
        	System.out.println(itemIcon);
        	itemIcon.loadSprite(new BufferedImage[]{img}, null, true);
        	this.itemIcon = itemIcon;
    	}
    	catch(Throwable th){
    		th.printStackTrace();
    	}
    	System.out.println(itemIcon);
    }
}
