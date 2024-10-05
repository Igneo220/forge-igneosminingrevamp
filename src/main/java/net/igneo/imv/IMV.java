package net.igneo.imv;

import com.mojang.logging.LogUtils;
import net.igneo.imv.block.ModBlocks;
import net.igneo.imv.entity.ModEntities;
import net.igneo.imv.entity.crystalsentry.CrystalSentryRenderer;
import net.igneo.imv.entity.sundewpede.body.SundewpedeBodyRenderer;
import net.igneo.imv.entity.sundewpede.head.SundewpedeHeadEntity;
import net.igneo.imv.entity.sundewpede.head.SundewpedeHeadRenderer;
import net.igneo.imv.entity.sundewpede.tail.SundewpedeTailRenderer;
import net.igneo.imv.item.ModItems;
import net.igneo.imv.networking.ModMessages;
import net.igneo.imv.sound.ModSounds;
import net.igneo.imv.worldgen.ModConfiguredFeatures;
import net.igneo.imv.worldgen.ModFeatures;
import net.igneo.imv.worldgen.ModPlacedFeatures;
import net.igneo.imv.worldgen.structure.ModStructures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Display;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IMV.MOD_ID)
public class IMV
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "igneosminingrevamp";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public IMV(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModStructures.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        ModMessages.register();
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.MOSSY_SATURINIUM.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.POINTED_HUESTONE.get(),RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.POINTED_VALUENITE.get(),RenderType.cutout());

            EntityRenderers.register(ModEntities.CRYSTAL_SENTRY.get(), CrystalSentryRenderer::new);
            EntityRenderers.register(ModEntities.SUNDEWPEDE_HEAD.get(), SundewpedeHeadRenderer::new);
            EntityRenderers.register(ModEntities.SUNDEWPEDE_BODY.get(), SundewpedeBodyRenderer::new);
            EntityRenderers.register(ModEntities.SUNDEWPEDE_TAIL.get(), SundewpedeTailRenderer::new);
        }
    }
}
