package net.igneo.imv.events;

import net.igneo.imv.IMV;
import net.igneo.imv.entity.ModEntities;
import net.igneo.imv.entity.crystalsentry.CrystalSentryEntity;
import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyEntity;
import net.igneo.imv.entity.sundewpede.head.SundewpedeHeadEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IMV.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CRYSTAL_SENTRY.get(), CrystalSentryEntity.createAttributes().build());
        event.put(ModEntities.SUNDEWPEDE_HEAD.get(), SundewpedeHeadEntity.createAttributes().build());
        event.put(ModEntities.SUNDEWPEDE_BODY.get(), SundewpedeBodyEntity.createAttributes().build());
        event.put(ModEntities.SUNDEWPEDE_TAIL.get(), SundewpedeBodyEntity.createAttributes().build());
    }


}
