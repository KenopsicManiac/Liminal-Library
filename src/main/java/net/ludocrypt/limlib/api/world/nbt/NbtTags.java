package net.ludocrypt.limlib.api.world.nbt;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiPredicate;

public class NbtTags {

	// Map<Group, Map<Structure, Map<Key, Tag>>>
	public final Map<String, Map<ResourceLocation, Map<String, CompoundTag>>> tags;

	// Map<Cache, Map<Group, Set<Structure>>>
	public final Map<String, Map<String, Set<ResourceLocation>>> matchingGroupCache;

	// Map<Cache, Set<Structure>>
	public final Map<String, Set<ResourceLocation>> matchingCache;

	public NbtTags() {
		this.tags = Maps.newHashMap();
		this.matchingGroupCache = Maps.newHashMap();
		this.matchingCache = Maps.newHashMap();
	}

	public static NbtTags parse(NbtGroup group, ResourceManager manager) {
		NbtTags tags = new NbtTags();

		group.forEachGroup((groupId, id) -> {
			CompoundTag readTags = NbtPlacerUtil.loadTags(id, manager);
			Map<String, CompoundTag> tagsMap = tags.tags
				.computeIfAbsent(groupId, (g) -> Maps.newHashMap())
				.computeIfAbsent(id, (g) -> Maps.newHashMap());

			for (String tagKey : readTags.getAllKeys()) {
				tagsMap.put(tagKey, readTags.getCompound(tagKey));
			}

		});

		return tags;
	}

	public Map<String, Set<ResourceLocation>> matching(TriFunction<String, String, CompoundTag, Boolean> matcher) {

		Map<String, Set<ResourceLocation>> matching = Maps.newHashMap();

		for (Entry<String, Map<ResourceLocation, Map<String, CompoundTag>>> groupEntry : this.tags.entrySet()) {
			Set<ResourceLocation> groupSet = Sets.newHashSet();

			for (Entry<ResourceLocation, Map<String, CompoundTag>> tagsEntry : groupEntry.getValue().entrySet()) {

				for (Entry<String, CompoundTag> tagEntry : tagsEntry.getValue().entrySet()) {

					if (matcher.apply(groupEntry.getKey(), tagEntry.getKey(), tagEntry.getValue())) {
						groupSet.add(tagsEntry.getKey());
					}

				}

			}

			matching.put(groupEntry.getKey(), groupSet);
		}

		return matching;
	}

	public Set<ResourceLocation> matching(BiPredicate<String, CompoundTag> matcher) {

		Set<ResourceLocation> matching = Sets.newHashSet();

		for (Entry<String, Map<ResourceLocation, Map<String, CompoundTag>>> groupEntry : this.tags.entrySet()) {

			for (Entry<ResourceLocation, Map<String, CompoundTag>> tagsEntry : groupEntry.getValue().entrySet()) {

				for (Entry<String, CompoundTag> tagEntry : tagsEntry.getValue().entrySet()) {

					if (matcher.test(tagEntry.getKey(), tagEntry.getValue())) {
						matching.add(tagsEntry.getKey());
					}

				}

			}

		}

		return matching;
	}

	public Map<String, Set<ResourceLocation>> matching(String cache, TriFunction<String, String, CompoundTag, Boolean> matcher) {
		return this.matchingGroupCache.computeIfAbsent(cache, (c) -> matching(matcher));
	}

	public Set<ResourceLocation> matching(String cache, BiPredicate<String, CompoundTag> matcher) {
		this.matchingGroupCache.computeIfAbsent(cache, (c) -> matching((group, tagKey, nbt) -> matcher.test(tagKey, nbt)));
		return this.matchingCache.computeIfAbsent(cache, (c) -> matching(matcher));
	}

	public Set<ResourceLocation> matching(String... cache) {
		Set<ResourceLocation> all = null;

		for (String c : cache) {
			Set<ResourceLocation> cacheSet = this.matchingCache.getOrDefault(c, Sets.newHashSet());

			if (all == null) {
				all = cacheSet;
			} else {
				all.retainAll(cacheSet);
			}

		}

		return all;
	}

	public Map<String, Set<ResourceLocation>> matchingGroups(String... cache) {

		Map<String, Set<ResourceLocation>> matching = Maps.newHashMap();

		for (String key : this.tags.keySet()) {
			Set<ResourceLocation> all = null;

			for (String c : cache) {
				Set<ResourceLocation> cacheSet = this.matchingGroupCache
					.getOrDefault(key, Maps.newHashMap())
					.getOrDefault(c, Sets.newHashSet());

				if (all == null) {
					all = cacheSet;
				} else {
					all.retainAll(cacheSet);
				}

			}

			matching.put(key, all);
		}

		return matching;
	}

	public void closeCache() {
		this.matchingGroupCache.clear();
		this.matchingCache.clear();
	}

}
