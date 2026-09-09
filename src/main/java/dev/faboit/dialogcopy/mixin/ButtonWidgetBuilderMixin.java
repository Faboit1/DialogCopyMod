package dev.faboit.dialogcopy.mixin;

import dev.faboit.dialogcopy.client.DialogActionInfo;
import dev.faboit.dialogcopy.client.DialogActionInfoHolder;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla hands us a builder, not a widget, so the action rides along on the builder and steps
 * across onto the button the moment it is built.
 */
@Mixin(ButtonWidget.Builder.class)
public class ButtonWidgetBuilderMixin implements DialogActionInfoHolder {
	@Unique
	private DialogActionInfo dialogcopy$actionInfo;

	@Override
	public DialogActionInfo dialogcopy$getActionInfo() {
		return dialogcopy$actionInfo;
	}

	@Override
	public void dialogcopy$setActionInfo(DialogActionInfo info) {
		this.dialogcopy$actionInfo = info;
	}

	@Inject(method = "build()Lnet/minecraft/client/gui/widget/ButtonWidget;", at = @At("RETURN"))
	private void dialogcopy$carryActionInfo(CallbackInfoReturnable<ButtonWidget> cir) {
		if (dialogcopy$actionInfo != null) {
			((DialogActionInfoHolder) cir.getReturnValue()).dialogcopy$setActionInfo(dialogcopy$actionInfo);
		}
	}
}
