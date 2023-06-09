package littlechasiu.discraft.mixins;

import littlechasiu.discraft.PlayerEvents;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class AdvancementMixin {
  @Shadow
  private ServerPlayer player;

  @Inject(method = "award",
    at = @At(
      target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V",
      value = "INVOKE"
    )
  )
  private void award$triggerEvent(Advancement advancement, String criterion,
                                  CallbackInfoReturnable<Boolean> cir) {
    PlayerEvents.ADVANCEMENT_MADE.invoker().onAdvancementMade(player, advancement);
  }
}
