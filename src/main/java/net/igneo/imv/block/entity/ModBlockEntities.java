package net.igneo.imv.block.entity;

import net.igneo.imv.IMV;
import net.igneo.imv.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, IMV.MOD_ID);

    public static final RegistryObject<BlockEntityType<CrystalHeartBlockEntity>> CRYSTAL_HEART_BE =
            BLOCK_ENTITIES.register("crystal_heart_be", () ->
                    BlockEntityType.Builder.of(CrystalHeartBlockEntity::new,
                            ModBlocks.CRYSTAL_HEART.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
