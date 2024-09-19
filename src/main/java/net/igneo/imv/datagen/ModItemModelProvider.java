package net.igneo.imv.datagen;

import net.igneo.imv.IMV;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, IMV.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
}
