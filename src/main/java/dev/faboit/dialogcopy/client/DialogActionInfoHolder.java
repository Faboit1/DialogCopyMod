package dev.faboit.dialogcopy.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Implemented by mixin on both {@code ButtonWidget.Builder} and {@code ButtonWidget}, so the action
 * behind a dialog button survives the trip from builder to finished widget.
 */
@Environment(EnvType.CLIENT)
public interface DialogActionInfoHolder {
	DialogActionInfo dialogcopy$getActionInfo();

	void dialogcopy$setActionInfo(DialogActionInfo info);
}
