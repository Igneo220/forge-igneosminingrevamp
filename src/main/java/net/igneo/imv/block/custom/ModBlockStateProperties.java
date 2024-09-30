package net.igneo.imv.block.custom;

import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ModBlockStateProperties {
    public static final EnumProperty<HuestoneThickness> HUESTONE_THICKNESS;
    public static final EnumProperty<ValueniteThickness> VALUENITE_THICKNESS;

    public ModBlockStateProperties() {
    }

    static {
        HUESTONE_THICKNESS = EnumProperty.create("thickness", HuestoneThickness.class);
        VALUENITE_THICKNESS = EnumProperty.create("thickness", ValueniteThickness.class);
    }
}
