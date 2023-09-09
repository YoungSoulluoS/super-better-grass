package dev.lambdaurora.lambdabettergrass.mixin;

import dev.lambdaurora.lambdabettergrass.LambdaBetterGrass;
import net.minecraft.resource.MultiPackResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MultiPackResourceManager.class)
public abstract class MultiPackResourceManagerMixin {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "TAIL"
            )
    )
    private void sbg$2addReloader(ResourceType type, List<ResourcePack> packs, CallbackInfo ci) {
        if (type == ResourceType.CLIENT_RESOURCES) {
            LambdaBetterGrass mod = LambdaBetterGrass.get();

            mod.log("Rebuilding resources...");
            mod.resourceReloader.reload((MultiPackResourceManager) (Object) this);
        }
    }
}
