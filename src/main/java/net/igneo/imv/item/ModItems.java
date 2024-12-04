package net.igneo.imv.item;

import net.igneo.imv.IMV;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, IMV.MOD_ID);

    public static final RegistryObject<Item> CRYSTAL_HEART = ITEMS.register("crystal_heart_item",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
