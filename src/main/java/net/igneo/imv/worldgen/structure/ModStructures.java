package net.igneo.imv.worldgen.structure;

import net.igneo.imv.IMV;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIndexSavedData;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public abstract class ModStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, IMV.MOD_ID);

    public static final RegistryObject<StructureType<CrystalBehemothStructure>> CRYSTAL_BEHEMOTH = STRUCTURES.register("crystalbehemoth", () -> () -> CrystalBehemothStructure.CODEC);
    public static final RegistryObject<StructureType<CrystalFlowerStructure>> CRYSTAL_FLOWER = STRUCTURES.register("crystalflower", () -> () -> CrystalFlowerStructure.CODEC);
    public static final RegistryObject<StructureType<CrystalLotusStructure>> CRYSTAL_LOTUS = STRUCTURES.register("crystallotus", () -> () -> CrystalLotusStructure.CODEC);
    public static final RegistryObject<StructureType<CrystalPitcherStructure>> CRYSTAL_PITCHER = STRUCTURES.register("crystalpitcher", () -> () -> CrystalPitcherStructure.CODEC);


    public static void register(IEventBus eventBus) { STRUCTURES.register(eventBus); }
}
