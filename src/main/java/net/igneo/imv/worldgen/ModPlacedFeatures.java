package net.igneo.imv.worldgen;

import net.igneo.imv.IMV;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> HUESTONE_CLUSTER_PLACED_KEY = registerKey("huestone_cluster");
    public static final ResourceKey<PlacedFeature> VALUENITE_CLUSTER_PLACED_KEY = registerKey("valuenite_cluster");
    public static final ResourceKey<PlacedFeature> COAL_SATURINIUM_ORE_PLACED_KEY = registerKey("coal_saturinium_ore");
    public static final ResourceKey<PlacedFeature> IRON_SATURINIUM_ORE_PLACED_KEY = registerKey("iron_saturinium_ore");
    public static final ResourceKey<PlacedFeature> COPPER_SATURINIUM_ORE_PLACED_KEY = registerKey("copper_saturinium_ore");
    public static final ResourceKey<PlacedFeature> ZINC_SATURINIUM_ORE_PLACED_KEY = registerKey("zinc_saturinium_ore");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context, HUESTONE_CLUSTER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.HUESTONE_CLUSTER_KEY), CountPlacement.of(UniformInt.of(140, 180)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());

        PlacementUtils.register(context, VALUENITE_CLUSTER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.VALUENITE_CLUSTER_KEY), CountPlacement.of(UniformInt.of(140, 180)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());

        PlacementUtils.register(context, COAL_SATURINIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.COAL_SATURINIUM_ORE_KEY),
                commonOrePlacement(12,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(200))));
        PlacementUtils.register(context, IRON_SATURINIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.IRON_SATURINIUM_ORE_KEY),
                commonOrePlacement(12,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(200))));
        PlacementUtils.register(context, COPPER_SATURINIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.COPPER_SATURINIUM_ORE_KEY),
                commonOrePlacement(12,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(200))));
        PlacementUtils.register(context, ZINC_SATURINIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.ZINC_SATURINIUM_ORE_KEY),
                commonOrePlacement(12,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(200))));

    }

    public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int pCount, PlacementModifier pHeightRange) {
        return orePlacement(CountPlacement.of(pCount), pHeightRange);
    }

    public static List<PlacementModifier> rareOrePlacement(int pChance, PlacementModifier pHeightRange) {
        return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(IMV.MOD_ID, name));
    }

    public static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?,?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key , new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
