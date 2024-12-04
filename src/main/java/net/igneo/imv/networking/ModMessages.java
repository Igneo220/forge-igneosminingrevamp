package net.igneo.imv.networking;

import net.igneo.imv.networking.packet.*;
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
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("imv", "messages")).networkProtocolVersion(() -> {
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
        net.messageBuilder(ScreenshakeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ScreenshakeS2CPacket::new)
                .encoder(ScreenshakeS2CPacket::toBytes)
                .consumerMainThread(ScreenshakeS2CPacket::handle)
                .add();
        net.messageBuilder(DetectSoundS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(DetectSoundS2CPacket::new)
                .encoder(DetectSoundS2CPacket::toBytes)
                .consumerMainThread(DetectSoundS2CPacket::handle)
                .add();
        net.messageBuilder(CrystalSentrySummonSoundS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CrystalSentrySummonSoundS2CPacket::new)
                .encoder(CrystalSentrySummonSoundS2CPacket::toBytes)
                .consumerMainThread(CrystalSentrySummonSoundS2CPacket::handle)
                .add();
        net.messageBuilder(FloraSummonSoundS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(FloraSummonSoundS2CPacket::new)
                .encoder(FloraSummonSoundS2CPacket::toBytes)
                .consumerMainThread(FloraSummonSoundS2CPacket::handle)
                .add();
        net.messageBuilder(RafflSummonSoundS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(RafflSummonSoundS2CPacket::new)
                .encoder(RafflSummonSoundS2CPacket::toBytes)
                .consumerMainThread(RafflSummonSoundS2CPacket::handle)
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
