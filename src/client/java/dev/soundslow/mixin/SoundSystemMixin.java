package dev.soundslow.mixin;

import dev.soundslow.SoundSlowClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Scales the pitch of every sound right before it is handed to OpenAL.
 *
 * We hook the RETURN of getAdjustedPitch and multiply, which lands AFTER vanilla's
 * 0.5..2.0 clamp - so deeper slowdowns (e.g. tick rate 5 -> x0.25) actually reach the
 * audio engine instead of being clamped back to 0.5.
 */
@Mixin(SoundSystem.class)
public class SoundSystemMixin {

	@Inject(method = "getAdjustedPitch", at = @At("RETURN"), cancellable = true)
	private void soundslow$scalePitch(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
		float mult = SoundSlowClient.currentMultiplier();
		if (mult != 1.0f) {
			cir.setReturnValue(cir.getReturnValue() * mult);
		}
	}
}
