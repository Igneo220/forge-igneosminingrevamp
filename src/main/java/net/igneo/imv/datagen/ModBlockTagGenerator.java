package net.igneo.imv.datagen;

import net.igneo.imv.IMV;
import net.igneo.imv.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, IMV.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.DIRT)
                .add(ModBlocks.MOSSY_SATURINIUM.get());

        this.tag(BlockTags.MOSS_REPLACEABLE)
                .add(ModBlocks.MOSSY_SATURINIUM.get());
        this.tag(BlockTags.MOSS_REPLACEABLE)
                .add(ModBlocks.SATURINIUM.get());
        this.tag(BlockTags.MOSS_REPLACEABLE)
                .add(ModBlocks.VALUENITE.get());
        this.tag(BlockTags.MOSS_REPLACEABLE)
                .add(ModBlocks.HUESTONE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.MOSSY_SATURINIUM.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SATURINIUM.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.VALUENITE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.HUESTONE.get());

        this.tag(BlockTags.DRIPSTONE_REPLACEABLE)
                .add(ModBlocks.MOSSY_SATURINIUM.get())
                .add(ModBlocks.SATURINIUM.get());
    }
}
