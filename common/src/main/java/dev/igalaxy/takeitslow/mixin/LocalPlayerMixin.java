package dev.igalaxy.takeitslow.mixin;

import dev.igalaxy.takeitslow.TakeItSlow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow @Final protected Minecraft minecraft;
    @Shadow public abstract boolean isUnderWater();
    @Shadow public abstract void setSprinting(boolean bl);

    @ModifyVariable(method = "setSprinting(Z)V", at = @At("HEAD"))
    private boolean modifySetSprinting(boolean bl) {
        GameType localPlayerMode = ((MultiPlayerGameModeAccessor)this.minecraft.gameMode).getLocalPlayerMode();

        boolean allowSwimming = TakeItSlow.getConfig().allowSwimming;
        boolean allowCreative = TakeItSlow.getConfig().allowCreative;

        boolean isUnderwater = this.isUnderWater();
        boolean isCreative = localPlayerMode == GameType.CREATIVE;
        boolean isSpectator = localPlayerMode == GameType.SPECTATOR;

        return (isUnderwater && bl && allowSwimming) || (isCreative && bl && allowCreative) || (isSpectator && bl && allowCreative);
    }

    @Inject(method = "updateIsUnderwater", at = @At("TAIL"))
    private void modifyUpdateIsUnderwater(CallbackInfoReturnable<Boolean> cir) {
        GameType localPlayerMode = ((MultiPlayerGameModeAccessor)this.minecraft.gameMode).getLocalPlayerMode();

        boolean isUnderwater = this.isUnderWater();
        boolean isCreative = localPlayerMode == GameType.CREATIVE;
        boolean isSpectator = localPlayerMode == GameType.SPECTATOR;

        if (!isUnderwater && !isCreative && !isSpectator) {
            this.setSprinting(true);
        }
    }
}