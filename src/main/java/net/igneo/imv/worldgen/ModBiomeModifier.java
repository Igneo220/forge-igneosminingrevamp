package net.igneo.imv.worldgen;

import io.netty.bootstrap.Bootstrap;
import net.igneo.imv.IMV;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBiomeModifier {
    public static final ResourceKey<BiomeModifier> ADD_HUESTONE_CLUSTER = registerKey("add_huestone_cluster");
    /*
    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_HUESTONE_CLUSTER, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(

        ))
    }*/

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(IMV.MOD_ID, name));
    }
}
