package com.builtbroken.wowjudo.content.logs;

import com.builtbroken.wowjudo.SurvivalMod;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.event.world.BlockEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Replacement for vanilla log block drop
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class ItemLog extends Item implements IFuelHandler
{
    public ItemLog()
    {
        setTextureName(SurvivalMod.PREFX + "log");
        setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        LogTypes type = LogTypes.get(meta);
        if (type != LogTypes.GENERIC)
        {
            return type.icon;
        }
        return this.itemIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(this.getIconString());
        LogTypes.OAK.icon = reg.registerIcon(this.getIconString() + "Oak");
        LogTypes.SPRUCE.icon = reg.registerIcon(this.getIconString() + "Spruce");
        LogTypes.BIRCH.icon = reg.registerIcon(this.getIconString() + "Birch");
        LogTypes.JUNGLE.icon = reg.registerIcon(this.getIconString() + "Jungle");
        LogTypes.ACACIA.icon = reg.registerIcon(this.getIconString() + "Acacia");
        LogTypes.DARK_OAK.icon = reg.registerIcon(this.getIconString() + "DarkOak");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        LogTypes type = LogTypes.get(stack.getItemDamage());
        if (type != LogTypes.GENERIC)
        {
            return "tile.log." + type.localization;
        }
        return "tile.log";
    }

    @SubscribeEvent
    public void onDrop(BlockEvent.HarvestDropsEvent event)
    {
        if (event.drops != null)
        {
            List<ItemStack> toAdd = new ArrayList();
            Iterator<ItemStack> it = event.drops.iterator();
            while (it.hasNext())
            {
                ItemStack stack = it.next();
                if (stack != null && stack.getItem() instanceof ItemBlock)
                {
                    Block block = Block.getBlockFromItem(stack.getItem());
                    if (block != null)
                    {
                        if (block == Blocks.log)
                        {
                            it.remove();
                            switch (stack.getItemDamage())
                            {
                                case 0:
                                    toAdd.add(new ItemStack(this, stack.stackSize, LogTypes.OAK.ordinal()));
                                    break;
                                case 1:
                                    toAdd.add(new ItemStack(this, stack.stackSize, LogTypes.SPRUCE.ordinal()));
                                    break;
                                case 2:
                                    toAdd.add(new ItemStack(this, stack.stackSize, LogTypes.BIRCH.ordinal()));
                                    break;
                                case 3:
                                    toAdd.add(new ItemStack(this, stack.stackSize, LogTypes.JUNGLE.ordinal()));
                                    break;
                                default:
                                    toAdd.add(new ItemStack(this, stack.stackSize, LogTypes.GENERIC.ordinal()));
                                    break;
                            }
                        }
                        else if (block == Blocks.log2)
                        {
                            it.remove();
                            switch (stack.getItemDamage())
                            {
                                case 0:
                                    toAdd.add(new ItemStack(this, stack.stackSize, LogTypes.ACACIA.ordinal()));
                                    break;
                                case 1:
                                    toAdd.add(new ItemStack(this, stack.stackSize, LogTypes.DARK_OAK.ordinal()));
                                    break;
                                default:
                                    toAdd.add(new ItemStack(this, stack.stackSize, LogTypes.GENERIC.ordinal()));
                                    break;
                            }
                        }
                    }
                }
            }

            for (ItemStack stack : toAdd)
            {
                event.drops.add(stack);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list)
    {
        for (LogTypes types : LogTypes.values())
        {
            list.add(new ItemStack(item, 1, types.ordinal()));
        }
    }

    @Override
    public int getBurnTime(ItemStack fuel)
    {
        if (fuel != null && fuel.getItem() == this)
        {
            return 300;
        }
        return 0;
    }

    /**
     * List of logs
     */
    public enum LogTypes
    {
        GENERIC(""),
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("big_oak");

        public final String localization;

        @SideOnly(Side.CLIENT)
        public IIcon icon;

        LogTypes(String localization)
        {
            this.localization = localization;
        }

        public static LogTypes get(int meta)
        {
            if (meta >= 0 && meta < values().length)
            {
                return values()[meta];
            }
            return GENERIC;
        }
    }
}
