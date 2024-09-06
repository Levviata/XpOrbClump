package surreal.fixeroo.core;

import com.google.common.base.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import surreal.fixeroo.FixerooConfig;

import java.util.List;

@SuppressWarnings("unused")
public class FixerooHooks {

    // XP Orb Clump
    public static void EntityXPOrb$onUpdate(EntityXPOrb orb) {
        World world = orb.world;
        double a = FixerooConfig.xpOrbClump.areaSize/2;

        List<EntityXPOrb> orbs = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(orb.posX-a, orb.posY-a, orb.posZ-a, orb.posX+a, orb.posY+a, orb.posZ+a), entity -> entity != null && entity.posX != orb.posX && entity.posY != orb.posY && entity.posZ != orb.posZ);
        if (orbs.size() >= FixerooConfig.xpOrbClump.maxOrbCount && !world.isRemote) {
            EntityXPOrb xpOrb = orbs.get(0);
            xpOrb.xpValue += orb.xpValue;
            orb.setDead();
        }
    }

    public static float RenderXPOrb$getSize(EntityXPOrb orb) {
        int xpValue = orb.xpValue;
        return xpValue > 2487 ? 0.3F * orb.getTextureByXP() : 0.3F * Math.max(1, xpValue & 20);
    }

    // Golem Tweaks
    public static Predicate<BlockWorldState> BlockPumpkin$predicateAny() {
        return state -> true;
    }

    public static void BlockPumpkin$trySpawnGolem(BlockPattern snowman, BlockPattern ironGolem, World worldIn, BlockPos pos) {
        Block blockBelow = worldIn.getBlockState(pos.down()).getBlock();
        boolean isSnowMan = false;

        BlockPattern.PatternHelper pattern = null;
        if (blockBelow == Blocks.SNOW) {
            pattern = snowman.match(worldIn, pos);
            isSnowMan = true;
        }
        else if (blockBelow == Blocks.IRON_BLOCK) pattern = ironGolem.match(worldIn, pos);

        if (pattern != null) {
            if (isSnowMan) {
                int i;
                for (i = 0; i < snowman.getThumbLength(); i++) {
                    BlockPos p = pattern.translateOffset(0, i, 0).getPos();
                    worldIn.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
                    worldIn.notifyNeighborsRespectDebug(p, Blocks.AIR, false);
                }

                EntitySnowman entitysnowman = new EntitySnowman(worldIn);
                BlockPos blockpos1 = pattern.translateOffset(0, 2, 0).getPos();
                entitysnowman.setLocationAndAngles((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.05D, (double)blockpos1.getZ() + 0.5D, 0.0F, 0.0F);
                worldIn.spawnEntity(entitysnowman);

                for (EntityPlayerMP player : worldIn.getEntitiesWithinAABB(EntityPlayerMP.class, entitysnowman.getEntityBoundingBox().grow(5.0D))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entitysnowman);
                }
            } else {
                for (int i = 0; i < ironGolem.getThumbLength(); i++) {
                    if (i == 1) {
                        for (int g = 0; g < ironGolem.getPalmLength(); g++) {
                            BlockPos p = pattern.translateOffset(g, i, 0).getPos();
                            worldIn.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
                            worldIn.notifyNeighborsRespectDebug(p, Blocks.AIR, false);
                        }
                    } else {
                        BlockPos p = pattern.translateOffset(1, i, 0).getPos();
                        worldIn.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
                        worldIn.notifyNeighborsRespectDebug(p, Blocks.AIR, false);
                    }
                }

                BlockPos blockpos = pattern.translateOffset(1, 2, 0).getPos();
                EntityIronGolem entityirongolem = new EntityIronGolem(worldIn);
                entityirongolem.setPlayerCreated(true);
                entityirongolem.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
                worldIn.spawnEntity(entityirongolem);

                for (EntityPlayerMP player : worldIn.getEntitiesWithinAABB(EntityPlayerMP.class, entityirongolem.getEntityBoundingBox().grow(5.0D))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entityirongolem);
                }
            }
        }
    }

    // Elytra Tweaks
    public static float EntityPlayer$getEyeHeight(float original, EntityPlayer player) {
        return player.isElytraFlying() ? 0.4F : original;
    }

    public static boolean RenderPlayer$isSneak(boolean original, EntityLivingBase entity) {
        return original && !entity.isElytraFlying();
    }

    public static float ModelPlayer$setRotationAngles(Entity entity, float original) {
        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isElytraFlying()) return 0.0F;
        return original;
    }

    // Shulker Coloring
    public static EnumDyeColor EntityShulker$getColorFromStack(EntityShulker shulker, ItemStack stack) {
        if (stack.isEmpty()) return null;
        int[] ids = OreDictionary.getOreIDs(stack);
        if (ids.length == 0) return null;

        EnumDyeColor color = null;

        for (int i : ids) {
            if (i == OreDictionary.getOreID("dyeWhite")) { color = EnumDyeColor.WHITE; break; }
            if (i == OreDictionary.getOreID("dyeOrange")) { color = EnumDyeColor.ORANGE; break; }
            if (i == OreDictionary.getOreID("dyeMagenta")) { color = EnumDyeColor.MAGENTA; break; }
            if (i == OreDictionary.getOreID("dyeLightBlue")) { color = EnumDyeColor.LIGHT_BLUE; break; }
            if (i == OreDictionary.getOreID("dyeYellow")) { color = EnumDyeColor.YELLOW; break; }
            if (i == OreDictionary.getOreID("dyeLime")) { color = EnumDyeColor.LIME; break; }
            if (i == OreDictionary.getOreID("dyePink")) { color = EnumDyeColor.PINK; break; }
            if (i == OreDictionary.getOreID("dyeGray")) { color = EnumDyeColor.GRAY; break; }
            if (i == OreDictionary.getOreID("dyeLightGray")) { color = EnumDyeColor.SILVER; break; }
            if (i == OreDictionary.getOreID("dyeCyan")) { color = EnumDyeColor.CYAN; break; }
            if (i == OreDictionary.getOreID("dyePurple")) { color = EnumDyeColor.PURPLE; break; }
            if (i == OreDictionary.getOreID("dyeBlue")) { color = EnumDyeColor.BLUE; break; }
            if (i == OreDictionary.getOreID("dyeBrown")) { color = EnumDyeColor.BROWN; break; }
            if (i == OreDictionary.getOreID("dyeGreen")) { color = EnumDyeColor.GREEN; break; }
            if (i == OreDictionary.getOreID("dyeRed")) { color = EnumDyeColor.RED; break; }
            if (i == OreDictionary.getOreID("dyeBlack")) { color = EnumDyeColor.BLACK; break; }
        }

        if (shulker.getColor() == color) return null;
        if (color != null) stack.shrink(1);
        return color;
    }
}