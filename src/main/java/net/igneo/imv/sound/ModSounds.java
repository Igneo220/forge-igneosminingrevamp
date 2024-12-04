package net.igneo.imv.sound;

import net.igneo.imv.IMV;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, IMV.MOD_ID);

    public static final RegistryObject<SoundEvent> CRYSTAL_SENTRY_ATTACK = registerSoundEvents("crystal_sentry_attack");
    public static final RegistryObject<SoundEvent> CRYSTAL_SENTRY_APPEAR = registerSoundEvents("crystal_sentry_appear");
    public static final RegistryObject<SoundEvent> CRYSTAL_SENTRY_DEATH = registerSoundEvents("crystal_sentry_death");
    public static final RegistryObject<SoundEvent> CRYSTAL_SENTRY_HIDE = registerSoundEvents("crystal_sentry_hide");
    public static final RegistryObject<SoundEvent> CRYSTAL_SENTRY_HURT = registerSoundEvents("crystal_sentry_hurt");
    public static final RegistryObject<SoundEvent> CRYSTAL_SENTRY_IDLE = registerSoundEvents("crystal_sentry_idle");
    public static final RegistryObject<SoundEvent> SP_IDLE = registerSoundEvents("sp_idle");
    public static final RegistryObject<SoundEvent> SP_DEATH = registerSoundEvents("sp_death");
    public static final RegistryObject<SoundEvent> SP_HURT = registerSoundEvents("sp_hurt");
    public static final RegistryObject<SoundEvent> SP_SCARE = registerSoundEvents("sp_scare");
    public static final RegistryObject<SoundEvent> RAFFL_DEATH = registerSoundEvents("raffl_death");
    public static final RegistryObject<SoundEvent> RAFFL_FLY = registerSoundEvents("raffl_fly");
    public static final RegistryObject<SoundEvent> RAFFL_HURT = registerSoundEvents("raffl_hurt");
    public static final RegistryObject<SoundEvent> RAFFL_IDLE = registerSoundEvents("raffl_idle");
    public static final RegistryObject<SoundEvent> RAFFL_RECOVER = registerSoundEvents("raffl_recover");
    public static final RegistryObject<SoundEvent> RAFFL_SHOOT = registerSoundEvents("raffl_shoot");
    public static final RegistryObject<SoundEvent> FLORA_ATTACK = registerSoundEvents("flora_attack");
    public static final RegistryObject<SoundEvent> FLORA_DEATH = registerSoundEvents("flora_death");
    public static final RegistryObject<SoundEvent> FLORA_HURT = registerSoundEvents("flora_hurt");
    public static final RegistryObject<SoundEvent> FLORA_SHOOT = registerSoundEvents("flora_shoot");
    public static final RegistryObject<SoundEvent> CRYSTAL_DETECT = registerSoundEvents("crystal_detect");
    public static final RegistryObject<SoundEvent> HEART_BEAT = registerSoundEvents("heart_beat");
    public static final RegistryObject<SoundEvent> HEART_ANGER = registerSoundEvents("heart_anger");
    public static final RegistryObject<SoundEvent> FLORA_SUMMON = registerSoundEvents("flora_spawn");
    public static final RegistryObject<SoundEvent> CS_SUMMON = registerSoundEvents("cs_spawn");
    public static final RegistryObject<SoundEvent> RAFFL_SUMMON = registerSoundEvents("raffl_spawn");



    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(IMV.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
