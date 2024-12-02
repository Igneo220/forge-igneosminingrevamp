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
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> HUESTONE_CLUSTER_KEY = registerKey("huestone_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> VALUENITE_CLUSTER_KEY = registerKey("valuenite_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COAL_SATURINIUM_ORE_KEY = registerKey("coal_saturinium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> IRON_SATURINIUM_ORE_KEY = registerKey("iron_saturinium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COPPER_SATURINIUM_ORE_KEY = registerKey("copper_saturinium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ZINC_SATURINIUM_ORE_KEY = registerKey("zinc_saturinium_ore");

    public static void boostrap(BootstapContext<ConfiguredFeature<?,?>> context) {
        RuleTest oreReplaceable = new BlockMatchTest(ModBlocks.SATURINIUM.get());

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

        List<OreConfiguration.TargetBlockState> CoalSaturiniumOre = List.of(OreConfiguration.target(oreReplaceable,
                        ModBlocks.COAL_SATURINIUM_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> IronSaturiniumOre = List.of(OreConfiguration.target(oreReplaceable,
                ModBlocks.IRON_SATURINIUM_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> CopperSaturiniumOre = List.of(OreConfiguration.target(oreReplaceable,
                ModBlocks.COPPER_SATURINIUM_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> ZincSaturiniumOre = List.of(OreConfiguration.target(oreReplaceable,
                ModBlocks.ZINC_SATURINIUM_ORE.get().defaultBlockState()));

        register(context, COAL_SATURINIUM_ORE_KEY, Feature.ORE, new OreConfiguration(CoalSaturiniumOre, 2));
        register(context, IRON_SATURINIUM_ORE_KEY, Feature.ORE, new OreConfiguration(IronSaturiniumOre, 5));
        register(context, COPPER_SATURINIUM_ORE_KEY, Feature.ORE, new OreConfiguration(CopperSaturiniumOre, 9));
        register(context, ZINC_SATURINIUM_ORE_KEY, Feature.ORE, new OreConfiguration(ZincSaturiniumOre, 2));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(IMV.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

}
