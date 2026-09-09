package dev.faboit.dialogcopy.mixin;

import dev.faboit.dialogcopy.client.DialogActionInfo;
import dev.faboit.dialogcopy.client.DialogActionInfoHolder;
import dev.faboit.dialogcopy.client.DialogInputTooltips;
import net.minecraft.client.gui.screen.dialog.DialogControls;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.text.ClickEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Catches every dialog control as vanilla builds it, so we can say what it is wired to.
 */
@Mixin(DialogControls.class)
public class DialogControlsMixin {
	@Shadow
	@Final
	private Map<String, DialogAction.ValueGetter> valueGetters;

	@SuppressWarnings("rawtypes")
	@Shadow
	@Final
	private DialogScreen screen;

	@Inject(method = "createButton(Lnet/minecraft/dialog/DialogActionButtonData;)"
			+ "Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", at = @At("RETURN"))
	private void dialogcopy$recordAction(DialogActionButtonData button,
			CallbackInfoReturnable<ButtonWidget.Builder> cir) {
		Optional<DialogAction> action = button.action();

		// Lazy on purpose: a dynamic command only resolves once the inputs hold real values, and
		// the value getters keep filling in after this button is built.
		Supplier<Optional<ClickEvent>> clickEvent =
				() -> action.flatMap(present -> present.createClickEvent(valueGetters));

		AfterAction afterAction = ((DialogScreenAccessor) screen).dialogcopy$getDialog().common().afterAction();

		((DialogActionInfoHolder) cir.getReturnValue()).dialogcopy$setActionInfo(
				new DialogActionInfo(action, clickEvent, button.data().tooltip(), afterAction));
	}

	@ModifyVariable(method = "addInput(Lnet/minecraft/dialog/type/DialogInput;Ljava/util/function/Consumer;)V",
			at = @At("HEAD"), argsOnly = true, index = 2)
	private Consumer<Widget> dialogcopy$describeInput(Consumer<Widget> original, DialogInput input) {
		return widget -> {
			DialogInputTooltips.apply(widget, input);
			original.accept(widget);
		};
	}
}
