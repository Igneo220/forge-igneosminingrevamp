package net.igneo.imv.worldgen;

import net.igneo.imv.IMV;
import net.igneo.imv.block.ModBlocks;
import net.igneo.imv.worldgen.feature.HuestoneClusterConfiguration;
import net.igneo.imv.worldgen.feature.ValueniteClusterConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> HUESTONE_CLUSTER_KEY = registerKey("huestone_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VALUENITE_CLUSTER_KEY = registerKey("valuenite_cluster");

    public static void boostrap(BootstapContext<ConfiguredFeature<?,?>> context) {
        RuleTest spikeReplacable = new BlockMatchTest(ModBlocks.SATURINIUM.get());

        FeatureUtils.register(context, HUESTONE_CLUSTER_KEY, ModFeatures.HUESTONE_CLUSTER.get(), new HuestoneClusterConfiguration(
                12,
                UniformInt.of(3,6),
                UniformInt.of(2,8),
                1,
                3,
                UniformInt.of(2,4),
                UniformFloat.of(0.15F,0.35F),
                ClampedNormalFloat.of(0.1F,0.3F,0.1F,0.9F),
                0.1F,
                3,
                8));
        FeatureUtils.register(context, VALUENITE_CLUSTER_KEY, ModFeatures.VALUENITE_CLUSTER.get(), new ValueniteClusterConfiguration(
                12,
                UniformInt.of(3,6),
                UniformInt.of(2,8),
                1,
                3,
                UniformInt.of(2,4),
                UniformFloat.of(0.15F,0.35F),
                ClampedNormalFloat.of(0.1F,0.3F,0.1F,0.9F),
                0.1F,
                3,
                8));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(IMV.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

}
