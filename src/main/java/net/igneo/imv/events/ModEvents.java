package net.igneo.imv.events;

import net.igneo.imv.IMV;
import net.igneo.imv.dimensionmanagers.CrystalManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid = IMV.MOD_ID)
public class ModEvents {


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
        }
    }
}
