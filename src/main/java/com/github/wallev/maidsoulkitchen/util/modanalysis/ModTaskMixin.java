package com.github.wallev.maidsoulkitchen.util.modanalysis;

import com.github.wallev.maidsoulkitchen.util.modutility.Mods;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

/**
 * Record representing mod task mixin relationships from upstream 1.20.1 integration
 * Enhanced from commit 24440d9bf0b8c4622afb7ad6c459b3a0194ad660
 */
public record ModTaskMixin(String taskUid, Mods compatMod, List<String> mixinList) {

    public static ModTaskMixin create(String taskUid, Mods compatMod, Set<String> mixinList) {
        return new ModTaskMixin(taskUid, compatMod, Lists.newArrayList(mixinList));
    }
}