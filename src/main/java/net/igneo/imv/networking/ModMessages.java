package net.igneo.imv.networking;

import net.igneo.imv.networking.packet.SundewpedeSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    public ModMessages() {
    }

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("icv", "messages")).networkProtocolVersion(() -> {
            return "1.0";
        }).clientAcceptedVersions((s) -> {
            return true;
        }).serverAcceptedVersions((s) -> {
            return true;
        }).simpleChannel();
        INSTANCE = net;
        net.messageBuilder(SundewpedeSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SundewpedeSyncS2CPacket::new)
                .encoder(SundewpedeSyncS2CPacket::toBytes)
                .consumerMainThread(SundewpedeSyncS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> {
            return player;
        }), message);
    }
}
