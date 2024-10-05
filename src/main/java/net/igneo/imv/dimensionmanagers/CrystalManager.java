package net.igneo.imv.dimensionmanagers;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class CrystalManager {
    private static List<ServerPlayer> detected = new ArrayList<ServerPlayer>();

    public static void detect(ServerPlayer player) {
        if (!detected.contains(player)) {
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
