package net.igneo.imv.datagen.loot;

import net.igneo.imv.block.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.HUESTONE.get());
        this.dropSelf(ModBlocks.POINTED_HUESTONE.get());
        this.dropSelf(ModBlocks.VALUENITE.get());
        this.dropSelf(ModBlocks.POINTED_VALUENITE.get());
        this.dropSelf(ModBlocks.SATURINIUM.get());
        this.dropSelf(ModBlocks.MOSSY_SATURINIUM.get());
        this.dropSelf(ModBlocks.LUMINIDE.get());
        this.dropSelf(ModBlocks.COAL_SATURINIUM_ORE.get());
        this.dropSelf(ModBlocks.IRON_SATURINIUM_ORE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
