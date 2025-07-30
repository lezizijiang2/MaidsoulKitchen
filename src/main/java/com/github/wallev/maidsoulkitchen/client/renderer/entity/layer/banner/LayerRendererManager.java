package com.github.wallev.maidsoulkitchen.client.renderer.entity.layer.banner;


import com.github.wallev.maidsoulkitchen.modclazzchecker.manager.TaskInfo;
import com.google.common.collect.ImmutableList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class LayerRendererManager {
    private static LayerRendererManager INSTANCE = new LayerRendererManager();

    private List<IBannerRenderer> bannerRenderers;

    private LayerRendererManager() {
        bannerRenderers = new ArrayList<>();
        bannerRenderers = ImmutableList.copyOf(bannerRenderers);
    }

    public static void init() {
        INSTANCE = new LayerRendererManager();
    }

    public static List<IBannerRenderer> getBannerRenderers() {
        return INSTANCE.bannerRenderers;
    }

    private void addBannerRenderer(TaskInfo taskInfo, Supplier<IBannerRenderer> renderer) {
        if (taskInfo.canLoad()) {
            bannerRenderers.add(renderer.get());
        }
    }
}
