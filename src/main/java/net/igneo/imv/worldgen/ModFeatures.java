package net.igneo.imv.worldgen;

import com.google.common.collect.Maps;
import net.igneo.imv.IMV;
import net.igneo.imv.worldgen.feature.HuestoneClusterConfiguration;
import net.igneo.imv.worldgen.feature.HuestoneClusterFeature;
import net.igneo.imv.worldgen.feature.ValueniteClusterConfiguration;
import net.igneo.imv.worldgen.feature.ValueniteClusterFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.Map;
@Mod.EventBusSubscriber(modid = IMV.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, IMV.MOD_ID);
    public static final RegistryObject<Feature<HuestoneClusterConfiguration>> HUESTONE_CLUSTER = FEATURES.register("huestone_cluster", () -> new HuestoneClusterFeature(HuestoneClusterConfiguration.CODEC));
    public static final RegistryObject<Feature<ValueniteClusterConfiguration>> VALUENITE_CLUSTER = FEATURES.register("valuenite_cluster", () -> new ValueniteClusterFeature(ValueniteClusterConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
