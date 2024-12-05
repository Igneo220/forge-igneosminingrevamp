package net.igneo.imv.networking.packet;

import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.util.function.Supplier;

public class ScreenshakeS2CPacket {
    private final int duration;
    private final int intensity;
    public ScreenshakeS2CPacket(int dur, int inten){
        this.duration = dur;
        this.intensity = inten;
    }
    public ScreenshakeS2CPacket(FriendlyByteBuf buf) {
        this.duration = buf.readInt();
        this.intensity = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(duration);
        buf.writeInt(intensity);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ScreenshakeHandler.addScreenshake(new ScreenshakeInstance(duration).setIntensity(intensity));
        });
        return true;
    }
}