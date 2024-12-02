package net.igneo.imv.worldgen.dimension.effects;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.igneo.imv.IMV;
import net.igneo.imv.worldgen.dimension.ModDimensions;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeDimensionSpecialEffects;

import javax.annotation.Nullable;

import static net.igneo.imv.worldgen.dimension.ModDimensions.IGNEO_DIM_TYPE;

public abstract class ModDimensionSpecialEffects implements IForgeDimensionSpecialEffects {

    @OnlyIn(Dist.CLIENT)
    public static class CrystalEffects extends DimensionSpecialEffects {
        public CrystalEffects() {
            super(Float.NaN, false, DimensionSpecialEffects.SkyType.END, true, false);
        }

        public Vec3 getBrightnessDependentFogColor(Vec3 p_108894_, float p_108895_) {
            return p_108894_.scale(0.15000000596046448);
        }

        public boolean isFoggyAt(int p_108891_, int p_108892_) {
            return false;
        }

        @Nullable
        public float[] getSunriseColor(float p_108888_, float p_108889_) {
            return null;
        }
    }

    public static final Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> EFFECTS = (Object2ObjectMap) Util.make(new Object2ObjectArrayMap(), (p_108881_) -> {
        p_108881_.put("crystal", new CrystalEffects());
    });

    public static void applyCustomEffects(ClientLevel world, Player player) {
        if (world.dimension() == ModDimensions.IGNEODIM_LEVEL_KEY && player.level().isClientSide) {
            FogRenderer.setupFog(Minecraft.getInstance().gameRenderer.getMainCamera(), FogRenderer.FogMode.FOG_TERRAIN,1,false,1);
            //Minecraft.getInstance().gameRenderer.getMainCamera()
            System.out.println("foggin it up");
            // Custom fog effect
            //Minecraft.getInstance().gameRenderer.currentEffect(). .setFogColor(0.0f, 0.1f, 0.2f); // Change to desired RGB

            // Custom sky color
            //Minecraft.getInstance().render.setSkyColor(0.1f, 0.2f, 0.3f); // Change sky color

            // Optional: Change time of day or lighting effects here
            //world.setDayTime(12000); // Set to a specific time if desired
        }
    }

}
