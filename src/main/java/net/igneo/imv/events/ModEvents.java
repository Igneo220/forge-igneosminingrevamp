package net.igneo.imv.events;

import com.eliotlash.mclib.math.functions.limit.Min;
import net.igneo.imv.IMV;
import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.igneo.imv.worldgen.dimension.effects.ModDimensionSpecialEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.client.DimensionSpecialEffectsManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

@Mod.EventBusSubscriber(modid = IMV.MOD_ID)
public class ModEvents {



    public static DimensionSpecialEffects forType(DimensionType pDimensionType) {
        return DimensionSpecialEffectsManager.getForType(pDimensionType.effectsLocation());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide) {
            ServerLevel level = (ServerLevel) event.player.level();
            boolean playerDetected = false;
            for (ServerPlayer player : CrystalManager.getDetected()) {
                if (player == event.player) {
                    playerDetected = true;
                    break;
                }
            }
            if (playerDetected) {
                if (event.player.isCreative()) {
                    CrystalManager.forgive((ServerPlayer) event.player);
                } else {
                    for (ServerPlayer player : level.players()) {
                        if (event.player.distanceTo(player) <= 5 && event.player != player) {
                            CrystalManager.detect(player);
                        }
                    }
                }
            }
        } else {
            ScreenshakeHandler.clientTick(Minecraft.getInstance().gameRenderer.getMainCamera(), RandomSource.create());
        }
    }
}
