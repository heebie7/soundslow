package dev.soundslow;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Sound Slow - lowers the pitch (i.e. resamples slower) of every sound the game plays.
 *
 * Why pitch and not a "smart" time-stretch: pitch-based slowdown is a plain resample, which is the
 * exact inverse of speeding the clip back up in a video editor. So: record at a low /tick rate with
 * this on, then speed the footage up (with pitch correction OFF in the editor) and the audio lands
 * back at its original pitch and length.
 *
 * Two modes, both via the /soundslow command:
 *   /soundslow auto       - multiplier follows the current /tick rate (tps / 20)
 *   /soundslow <0.01..2>  - fixed manual multiplier
 *   /soundslow off        - back to normal (x1.0)
 */
public class SoundSlowClient implements ClientModInitializer {

	// 1.0 = normal. < 1.0 = slower & lower. Read/written from the render & command threads, hence volatile.
	public static volatile float factor = 1.0f;
	public static volatile boolean auto = false;

	/** The pitch multiplier that should be applied to sounds right now. */
	public static float currentMultiplier() {
		if (auto) {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc != null && mc.world != null) {
				float tps = mc.world.getTickManager().getTickRate();
				if (tps > 0.0f) {
					return MathHelper.clamp(tps / 20.0f, 0.01f, 2.0f);
				}
			}
			return 1.0f;
		}
		return factor;
	}

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(literal("soundslow")
				.then(literal("off").executes(ctx -> {
					auto = false;
					factor = 1.0f;
					feedback(ctx.getSource(), "OFF (pitch x1.0)");
					return 1;
				}))
				.then(literal("auto").executes(ctx -> {
					auto = true;
					feedback(ctx.getSource(), "AUTO - follows /tick rate");
					return 1;
				}))
				.then(argument("multiplier", FloatArgumentType.floatArg(0.01f, 2.0f)).executes(ctx -> {
					auto = false;
					factor = FloatArgumentType.getFloat(ctx, "multiplier");
					feedback(ctx.getSource(), "MANUAL (pitch x" + factor + ")");
					return 1;
				}))
			)
		);
	}

	private static void feedback(FabricClientCommandSource source, String msg) {
		source.sendFeedback(Text.literal("[SoundSlow] " + msg));
	}
}
