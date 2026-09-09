package dev.faboit.dialogcopy.mixin;

import net.minecraft.dialog.action.ParsedTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The raw, unsubstituted command template behind a dynamic action. */
@Mixin(ParsedTemplate.class)
public interface ParsedTemplateAccessor {
	@Accessor("raw")
	String dialogcopy$getRaw();
}
