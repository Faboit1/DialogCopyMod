package dev.faboit.dialogcopy;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DialogCopyMod {
	public static final String MOD_ID = "dialogcopy";
	public static final Logger LOGGER = LoggerFactory.getLogger("DialogCopyMod");

	private DialogCopyMod() {
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
