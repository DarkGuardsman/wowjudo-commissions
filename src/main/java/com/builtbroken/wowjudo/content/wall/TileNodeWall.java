package com.builtbroken.wowjudo.content.wall;

import com.builtbroken.mc.api.explosive.IBlast;
import com.builtbroken.mc.api.explosive.IBlastEdit;
import com.builtbroken.mc.api.explosive.IExplosiveDamageable;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.block.BlockBase;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.lib.helper.MaterialDict;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.wowjudo.SurvivalMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
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

    private float hp = -1;
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
            Block block = getHost().getHostBlock();
            Material material = block.getMaterial();

            if(material != ((BlockBase)block).data.getMaterial())
            {
                System.out.println("Error mats do not match");
            }

            for (WallMaterial mat : WallMaterial.values())
            {
                if (mat.getMaterial() == material)
                {
                    mat_cache = mat;
                    break;
                }
            }
            if (mat_cache == null)
            {
                mat_cache = WallMaterial.WOOD;
            }
        }
        return mat_cache;
    }

    public float getHp()
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
            hp = nbt.getFloat("hp");
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setFloat("hp", hp);
        return nbt;
    }

    @Override
    public float getEnergyCostOfTile(IExplosiveHandler explosive, IBlast blast, EnumFacing facing, float energy, float distance)
    {
        energyCostPerHP = 20;
        float energyCost = Math.max(getHp(), 1) * energyCostPerHP;
        hp -= Math.min(hp, Math.max(0, energy / energyCostPerHP));
        return energyCost;
    }

    @Override
    public IBlastEdit getBlockEditOnBlastImpact(IExplosiveHandler explosive, IBlast blast, EnumFacing facing, float energy, float distance)
    {
        if (hp > 0)
        {
            return null;
        }
        return new BlockEdit(world(), xi(), yi(), zi()).set(Blocks.air, 0);
    }

    public enum WallMaterial
    {
        WOOD("wood", 10),
        STONE("rock", 40),
        IRON("iron", 100);

        public int hp;
        private final String materialName;
        private Material material;

        WallMaterial(String materialName, int hp)
        {
            this.materialName = materialName;
            this.hp = hp;
        }

        public static void loadConfig(Configuration configuration)
        {
            for (WallMaterial material : values())
            {
                material.hp = configuration.getInt(material.name().toLowerCase(), "Wall_HP", material.hp, 1, 10000, "How many hits of damage the wall can take before being destroyed.");
            }
        }

        public Material getMaterial()
        {
            if (material == null)
            {
                material = MaterialDict.get(materialName);
            }
            return material;
        }
    }
}
