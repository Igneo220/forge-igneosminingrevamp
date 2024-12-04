package net.igneo.imv.networking.packet;

import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyEntity;
import net.igneo.imv.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DetectSoundS2CPacket {
    public DetectSoundS2CPacket(){
    }
    public DetectSoundS2CPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            Minecraft.getInstance().level.playLocalSound(player.getX(),player.getY(),player.getZ(), ModSounds.CRYSTAL_DETECT.get(), SoundSource.HOSTILE, 1F, 1,false);
        });
        return true;
    }
}
