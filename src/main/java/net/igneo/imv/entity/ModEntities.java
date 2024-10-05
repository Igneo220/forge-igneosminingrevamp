package net.igneo.imv.entity;

import net.igneo.imv.IMV;
import net.igneo.imv.entity.crystalsentry.CrystalSentryEntity;
import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyEntity;
import net.igneo.imv.entity.sundewpede.head.SundewpedeHeadEntity;
import net.igneo.imv.entity.sundewpede.tail.SundewpedeTailEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, IMV.MOD_ID);

    public static final RegistryObject<EntityType<CrystalSentryEntity>> CRYSTAL_SENTRY =
            ENTITY_TYPES.register("crystal_sentry", () -> EntityType.Builder.of(CrystalSentryEntity::new, MobCategory.MONSTER)
                    .sized(1f,2.5f).build("crystal_sentry"));
    public static final RegistryObject<EntityType<SundewpedeHeadEntity>> SUNDEWPEDE_HEAD =
            ENTITY_TYPES.register("sundewpede_head", () -> EntityType.Builder.of(SundewpedeHeadEntity::new, MobCategory.MONSTER)
                    .sized(0.5f,1f).build("sundewpede_head"));
    public static final RegistryObject<EntityType<SundewpedeBodyEntity>> SUNDEWPEDE_BODY =
            ENTITY_TYPES.register("sundewpede_body", () -> EntityType.Builder.of(SundewpedeBodyEntity::new, MobCategory.MONSTER)
                    .sized(0.5f,1f).build("sundewpede_body"));
    public static final RegistryObject<EntityType<SundewpedeTailEntity>> SUNDEWPEDE_TAIL =
            ENTITY_TYPES.register("sundewpede_tail", () -> EntityType.Builder.of(SundewpedeTailEntity::new, MobCategory.MONSTER)
                    .sized(0.5f,1f).build("sundewpede_tail"));

    public static void register (IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
