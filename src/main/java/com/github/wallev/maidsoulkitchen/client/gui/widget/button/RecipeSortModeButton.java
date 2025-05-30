package com.github.wallev.maidsoulkitchen.client.gui.widget.button;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.entity.data.inner.task.CookData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;

/**
 * 配方排序模式按钮
 */
public class RecipeSortModeButton extends NormalTooltipButton {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MaidsoulKitchen.MOD_ID, "textures/gui/cook_sort.png");
    private CookData.RecipeSortMode currentMode;

    public RecipeSortModeButton(int x, int y, int pWidth, int pHeight, CookData.RecipeSortMode initialMode) {
        super(x, y, pWidth, pHeight, Component.empty(), Collections.emptyList(), (b) -> {
        });
        this.currentMode = initialMode;
        this.setTooltip(getTooltip(initialMode));
    }

    /**
     * 根据排序模式获取提示文本
     *
     * @param mode 排序模式
     * @return 提示组件
     */
    private static Tooltip getTooltip(CookData.RecipeSortMode mode) {
        Component text = switch (mode) {
            case DEFAULT -> Component.translatable("gui.maidsoulkitchen.sort_mode.default");
            case NUTRITION -> Component.translatable("gui.maidsoulkitchen.sort_mode.nutrition");
            case SATURATION -> Component.translatable("gui.maidsoulkitchen.sort_mode.saturation");
            case INGREDIENTS -> Component.translatable("gui.maidsoulkitchen.sort_mode.ingredients");
            case RESULT -> Component.translatable("gui.maidsoulkitchen.sort_mode.result");
        };
        return Tooltip.create(text);
    }

    @Override
    public void onPress() {
        this.currentMode = currentMode.next();
        this.setTooltip(getTooltip(currentMode));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        int textureX = 0;
        int textureY = currentMode.ordinal() * 16;
        // 按钮背景
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(MaidsoulKitchen.MOD_ID, "textures/gui/cook_guide.png"), getX(), getY(), 0, 232, 18, 18);
        // 渲染按钮图标
        guiGraphics.blit(TEXTURE, this.getX() + 1, this.getY() + 1, textureX, textureY, 16, 16, 64, 64);
    }

    /**
     * 获取当前排序模式
     *
     * @return 排序模式
     */
    public CookData.RecipeSortMode getCurrentMode() {
        return currentMode;
    }

    /**
     * 设置排序模式
     *
     * @param mode 新的排序模式
     */
    public void setMode(CookData.RecipeSortMode mode) {
        this.currentMode = mode;
        this.setTooltip(getTooltip(mode));
    }

    /**
     * 按钮点击回调接口
     */
    public interface OnPress {
        /**
         * 当按钮被点击时调用
         *
         * @param button 按钮实例
         */
        void onPress(RecipeSortModeButton button);
    }
}
