package dev.faboit.dialogcopy.mixin;

import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.dialog.type.Dialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Gives us read access to the {@link Dialog} a {@link DialogScreen} is currently showing,
 * plus the vanilla warning button so our own button can dodge it.
 */
@Mixin(DialogScreen.class)
public interface DialogScreenAccessor {
	@Accessor("dialog")
	Dialog dialogcopy$getDialog();

	@Accessor("warningButton")
	ButtonWidget dialogcopy$getWarningButton();
}
