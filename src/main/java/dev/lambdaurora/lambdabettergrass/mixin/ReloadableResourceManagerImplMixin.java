package dev.lambdaurora.lambdabettergrass.mixin;

import dev.lambdaurora.lambdabettergrass.LambdaBetterGrass;
import dev.lambdaurora.lambdabettergrass.resource.LBGResourcePack;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerImplMixin {
    @Shadow @Final private ResourceType type;

    @ModifyArgs(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/MultiPackResourceManager;<init>(Lnet/minecraft/resource/ResourceType;Ljava/util/List;)V"
            )
    )
    private void onReload(Args args) {
        if (this.type == ResourceType.CLIENT_RESOURCES) {
            LambdaBetterGrass mod = LambdaBetterGrass.get();

            mod.log("Inject generated resource pack.");

            List<ResourcePack> packs = args.get(1);

            if (!(packs instanceof ArrayList<ResourcePack>)) {
                packs = new ArrayList<>(packs);
            }

            packs.add(mod.resourcePack = new LBGResourcePack(mod));

            args.set(1, packs);
        }
    }
}
