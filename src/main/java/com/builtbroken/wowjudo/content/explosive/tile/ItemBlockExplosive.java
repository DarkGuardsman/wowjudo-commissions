package com.builtbroken.wowjudo.content.explosive.tile;

import com.builtbroken.wowjudo.SurvivalMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class ItemBlockExplosive extends ItemBlock
{
    public ItemBlockExplosive(Block p_i45328_1_)
    {
        super(p_i45328_1_);
        setTextureName(SurvivalMod.PREFX + "C4_item");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int p_77617_1_)
    {
        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(this.getIconString());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber()
    {
        return 1;
    }
}
