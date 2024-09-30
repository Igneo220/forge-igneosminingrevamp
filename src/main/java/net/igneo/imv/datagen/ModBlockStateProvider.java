package net.igneo.imv.datagen;

import net.igneo.imv.IMV;
import net.igneo.imv.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, IMV.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.HUESTONE);
        blockWithItem(ModBlocks.POINTED_HUESTONE);
        blockWithItem(ModBlocks.VALUENITE);
        blockWithItem(ModBlocks.POINTED_VALUENITE);
        blockWithItem(ModBlocks.SATURINIUM);
        blockWithItem(ModBlocks.MOSSY_SATURINIUM);
        blockWithItem(ModBlocks.LUMINIDE);
        blockWithItem(ModBlocks.COAL_SATURINIUM_ORE);
        blockWithItem(ModBlocks.IRON_SATURINIUM_ORE);
        blockWithItem(ModBlocks.CRYSTAL_ORE);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
