package net.igneo.imv.networking.packet;

import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SundewpedeSyncS2CPacket {
    private final int id;
    private final int id2;
    public SundewpedeSyncS2CPacket(int id, int id2){
        this.id = id;
        this.id2 = id2;
    }
    public SundewpedeSyncS2CPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.id2 = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(id2);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            SundewpedeBodyEntity entity = (SundewpedeBodyEntity) Minecraft.getInstance().level.getEntity(id);
            entity.setParent(Minecraft.getInstance().level.getEntity(id2));
        });
        return true;
    }
}
