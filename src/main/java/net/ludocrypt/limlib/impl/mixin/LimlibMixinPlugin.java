package net.ludocrypt.limlib.impl.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class LimlibMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return List.of();
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	private boolean areMixinsTheSame(String mixinClassName, String otherMixinClassName) {
		return mixinClassName.equals(otherMixinClassName);
	}

	private String qualifyDefaultMixinName(String mixinClassName) {
		return "ludocrypt.limlib.impl.mixin." + mixinClassName;
	}

	private String qualifyServerMixinName(String mixinClassName) {
		return qualifyDefaultMixinName("server." + mixinClassName);
	}

	private String qualifyClientMixinName(String mixinClassName) {
		return qualifyDefaultMixinName("client." + mixinClassName);
	}
}
