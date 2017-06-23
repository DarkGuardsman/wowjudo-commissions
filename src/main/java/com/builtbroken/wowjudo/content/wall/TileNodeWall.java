package com.builtbroken.wowjudo.content.wall;

import com.builtbroken.mc.api.explosive.IBlast;
import com.builtbroken.mc.api.explosive.IBlastEdit;
import com.builtbroken.mc.api.explosive.IExplosiveDamageable;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.wowjudo.SurvivalMod;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.config.Configuration;

/**
 * Damageable wall system that is slowly broken down rather than instantly destroyed
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/12/2017.
 */
@TileWrapped(className = "TileEntityWrappedWall")
public class TileNodeWall extends TileNode implements IExplosiveDamageable
{
    /** Cost of energy to take away 1 HP */
    public static float energyCostPerHP = 10;

    private int hp = -1;
    private WallMaterial mat_cache;

    public TileNodeWall()
    {
        super("blast.wall", SurvivalMod.DOMAIN);
    }

    @Override
    public boolean requiresPerTickUpdate()
    {
        return false;
    }

    public WallMaterial getMaterial()
    {
        if (mat_cache == null)
        {
            Material material = world().getBlock(xi(), yi(), zi()).getMaterial();
            for (WallMaterial mat : WallMaterial.values())
            {
                if (mat.material == material)
                {
                    mat_cache = mat;
                    break;
                }
            }
        }
        return mat_cache;
    }

    public int getHp()
    {
        if (hp == -1)
        {
            hp = getMaterial().hp;
        }
        return hp;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        if (nbt.hasKey("hp"))
        {
            hp = nbt.getInteger("hp");
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setInteger("hp", hp);
        return nbt;
    }

    @Override
    public float getEnergyCostOfTile(IExplosiveHandler explosive, IBlast blast, EnumFacing facing, float energy, float distance)
    {
        float energyCost = getHp() * energyCostPerHP;
        hp -= Math.floor(Math.max(1, energy / energyCostPerHP));
        return energyCost;
    }

    @Override
    public IBlastEdit getBlockEditOnBlastImpact(IExplosiveHandler explosive, IBlast blast, EnumFacing facing, float energy, float distance)
    {
        if (hp > 0)
        {
            return null;
        }
        return new BlockEdit(world(), xi(), yi(), zi());
    }

    public enum WallMaterial
    {
        WOOD(Material.wood, 20),
        STONE(Material.rock, 40),
        IRON(Material.iron, 100);

        public int hp;
        public Material material;

        WallMaterial(Material material, int hp)
        {
            this.material = material;
            this.hp = hp;
        }

        public static void loadConfig(Configuration configuration)
        {
            for (WallMaterial material : values())
            {
                material.hp = configuration.getInt(material.name().toLowerCase(), "Wall_HP", material.hp, 1, 10000, "How many hits of damage the wall can take before being destroyed.");
            }
        }
    }
}
