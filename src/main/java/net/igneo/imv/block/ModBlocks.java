package net.igneo.imv.block;

import net.igneo.imv.IMV;
import net.igneo.imv.block.custom.MossySaturiniumBlock;
import net.igneo.imv.block.custom.PointedHuestoneBlock;
import net.igneo.imv.block.custom.PointedValueniteBlock;
import net.igneo.imv.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, IMV.MOD_ID);

    public static final RegistryObject<Block> HUESTONE = registerBlock("huestone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.AMETHYST).lightLevel(state -> 5)));
    public static final RegistryObject<Block> POINTED_HUESTONE = registerBlock("pointed_huestone",
            () -> new PointedHuestoneBlock(BlockBehaviour.Properties.copy(Blocks.POINTED_DRIPSTONE).sound(SoundType.AMETHYST).lightLevel(state -> 7)));
    public static final RegistryObject<Block> VALUENITE = registerBlock("valuenite",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.AMETHYST).lightLevel(state -> 5)));
    public static final RegistryObject<Block> POINTED_VALUENITE = registerBlock("pointed_valuenite",
            () -> new PointedValueniteBlock(BlockBehaviour.Properties.copy(Blocks.POINTED_DRIPSTONE).sound(SoundType.AMETHYST).lightLevel(state -> 7)));
    public static final RegistryObject<Block> SATURINIUM = registerBlock("saturinium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.AMETHYST)));
    public static final RegistryObject<Block> MOSSY_SATURINIUM = registerBlock("mossy_saturinium",
            () -> new MossySaturiniumBlock(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.AMETHYST)));
    public static final RegistryObject<Block> LUMINIDE = registerBlock("luminide",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.AMETHYST).lightLevel(state -> 15)));
    public static final RegistryObject<Block> CRYSTAL_ORE = registerBlock("crystal_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.AMETHYST).noLootTable()));
    public static final RegistryObject<Block> COAL_SATURINIUM_ORE = registerBlock("coal_saturinium_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.AMETHYST)));
    public static final RegistryObject<Block> IRON_SATURINIUM_ORE = registerBlock("iron_saturinium_ore",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).sound(SoundType.AMETHYST)));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
