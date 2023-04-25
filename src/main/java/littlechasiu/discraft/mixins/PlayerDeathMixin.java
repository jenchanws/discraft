package littlechasiu.discraft.mixins;

import littlechasiu.discraft.PlayerEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayer.class)
public class PlayerDeathMixin {
  @Inject(method = "die",
    at = @At(
      target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
      value = "INVOKE",
      ordinal = 0),
    locals = LocalCapture.CAPTURE_FAILSOFT
  )
  private void die$triggerEvent(DamageSource damageSource, CallbackInfo ci, boolean bl,
                                Component msg) {
    PlayerEvents.PLAYER_DIED.invoker().onPlayerDeath((ServerPlayer) (Object) this, msg);
  }
}
