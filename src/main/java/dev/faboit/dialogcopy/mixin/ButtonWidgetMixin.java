package dev.faboit.dialogcopy.mixin;

import dev.faboit.dialogcopy.client.DialogActionInfo;
import dev.faboit.dialogcopy.client.DialogActionInfoHolder;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/** Lets a finished dialog button remember the action it was built from. */
@Mixin(ButtonWidget.class)
public class ButtonWidgetMixin implements DialogActionInfoHolder {
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
}
