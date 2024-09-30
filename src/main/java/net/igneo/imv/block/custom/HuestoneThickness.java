package net.igneo.imv.block.custom;

import net.minecraft.util.StringRepresentable;

public enum HuestoneThickness implements StringRepresentable {
    TIP_MERGE("tip_merge"),
    TIP("tip"),
    FRUSTUM("frustum"),
    MIDDLE("middle"),
    BASE("base");

    private final String name;

    private HuestoneThickness(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
