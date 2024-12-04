package net.igneo.imv.dimensionmanagers;

import net.igneo.imv.networking.ModMessages;
import net.igneo.imv.networking.packet.DetectSoundS2CPacket;
import net.igneo.imv.networking.packet.ScreenshakeS2CPacket;
import net.igneo.imv.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import team.lodestar.lodestone.network.screenshake.ScreenshakePacket;

import java.util.ArrayList;
import java.util.List;

public class CrystalManager {
    private static List<ServerPlayer> detected = new ArrayList<ServerPlayer>();

    public static void detect(ServerPlayer player) {
        if (!detected.contains(player) && !player.isCreative()) {
            ModMessages.sendToPlayer(new DetectSoundS2CPacket(),player);
            ModMessages.sendToPlayer(new ScreenshakeS2CPacket(240,3),player);
            detected.add(player);
        }
    }

    public static void forgive(ServerPlayer player) {
        detected.remove(player);
    }

    public static List<ServerPlayer> getDetected() {
        return detected;
    }
}
