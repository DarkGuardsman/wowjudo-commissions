package com.builtbroken.wowjudo.content.wall;

import com.builtbroken.mc.api.explosive.IBlast;
import com.builtbroken.mc.api.explosive.IBlastEdit;
import com.builtbroken.mc.api.explosive.IExplosiveDamageable;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.MaterialDict;
import com.builtbroken.mc.lib.world.edit.BlockEdit;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.ex.HPBlockEdit;
import io.netty.buffer.ByteBuf;
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
    private float hp = -1;
    private WallMaterial mat_cache;
    private StructureType struct_cache;

    public TileNodeWall()
    {
        super("blast.wall", SurvivalMod.DOMAIN);
    }

    @Override
    public String uniqueContentID()
    {
        return "structure." + getMaterial().materialName + "." + getStructureType().name().toLowerCase();
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

    public StructureType getStructureType()
    {
        if (struct_cache == null)
        {
            int meta = world().unwrap().getBlockMetadata(xi(), yi(), zi());
            if (meta > 0 && meta < StructureType.values().length)
            {
                struct_cache = StructureType.values()[meta];
            }
            else
            {
                struct_cache = StructureType.WALL;
            }
        }
        return struct_cache;
    }

    public float getHp()
    {
        if (hp == -1)
        {
            hp = getMaxHp();
        }
        return hp;
    }

    public float getMaxHp()
    {
        return getMaterial().getHp(getStructureType());
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
        nbt.setFloat("hp", getHp());
        return nbt;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        float old = getHp();
        hp = buf.readFloat();
        if (Math.abs(old - getHp()) > 0.001)
        {
            world().unwrap().markBlockRangeForRenderUpdate(xi(), yi(), zi(), xi(), yi(), zi());
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeFloat(getHp());
    }

    @Override
    public float getEnergyCostOfTile(IExplosiveHandler explosive, IBlast blast, EnumFacing facing, float energy, float distance)
    {
        return Math.max(getHp(), 1) * getMaterial().energyPerType[getStructureType().ordinal()];
    }

    @Override
    public IBlastEdit getBlockEditOnBlastImpact(IExplosiveHandler explosive, IBlast blast, EnumFacing facing, float energy, float distance)
    {
        float lostHP = Math.min(getHp(), Math.max(0, energy / getMaterial().energyPerType[getStructureType().ordinal()]));
        if (getHp() > 0 && getHp() - lostHP > 1)
        {
            if (lostHP > 0)
            {
                return new HPBlockEdit(world().unwrap(), xi(), yi(), zi(), lostHP);
            }
            return null;
        }
        return new BlockEdit(world().unwrap(), xi(), yi(), zi()).set(Blocks.air, 0);
    }

    public void reduceHP(float hp)
    {
        this.hp -= hp;
        sendDescPacket(); //TODO find way to fire only 1 time per tick
    }

    public enum WallMaterial
    {
        WOOD("wood", 5, 100),
        STONE("rock", 25, 100),
        IRON("iron", 62, 100);

        public float hp;
        public float energyCostPerHP;

        private final String materialName;
        private Material material;

        private float[] hpPerType;
        private float[] energyPerType;

        WallMaterial(String materialName, int hp, float energyCostPerHP)
        {
            this.materialName = materialName;
            this.hp = hp;
            this.energyCostPerHP = energyCostPerHP;
        }

        public static void loadConfig(Configuration configuration)
        {
            for (WallMaterial material : values())
            {
                material.hpPerType = new float[StructureType.values().length];
                material.energyPerType = new float[StructureType.values().length];
                for (StructureType type : StructureType.values())
                {
                    material.hpPerType[type.ordinal()] = configuration.getFloat(LanguageUtility.capitalizeFirst(type.name().toLowerCase()) + "_HP", material.name().toLowerCase() + "_structures", material.hp * type.multi, 1, 100000, "How many hits of damage does the structure take before being destroyed.");
                    material.energyPerType[type.ordinal()] = configuration.getFloat(LanguageUtility.capitalizeFirst(type.name().toLowerCase()) + "_energyPerHP", material.name().toLowerCase() + "_structures", material.hp * type.multi, 1, 1000000, "How much blast energy does it take to do 1 hp damage.");
                }
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

        public float getHp(StructureType structureType)
        {
            return hpPerType[structureType.ordinal()];
        }
    }

    public enum StructureType
    {
        WALL(1),
        FLOOR(1.2f),
        ROOF(0.8f);

        float multi;

        StructureType(float multi)
        {
            this.multi = multi;
        }
    }
}
