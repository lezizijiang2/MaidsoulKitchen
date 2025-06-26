package com.github.wallev.maidsoulkitchen.util.classana;

import com.github.wallev.maidsoulkitchen.util.ModUtil;
import com.github.wallev.maidsoulkitchen.util.modutility.Mods;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class TaskLoadError {
    private static final List<Info> ERRORS = new ArrayList<>();

    public static void putError(ResourceLocation taskUid, Mods mod, Type type, String message) {
        ERRORS.add(new Info(taskUid, mod, type, message));
    }

    public static void reportError(Player player) {
        if (ERRORS.isEmpty()) {
            return;
        }

        MutableComponent parent = Component.literal("----------------女仆厨房警告---------------").withStyle(ChatFormatting.DARK_RED);
        parent.append(CommonComponents.NEW_LINE);
        parent.append(Component.literal("当前有部分mod兼容失败，已自动为您拦截！"));
        parent.append(CommonComponents.NEW_LINE);
        MutableComponent component = Component.literal("请点击此处，向作者反馈，不然永远不会得到解决！")
                .withStyle(ChatFormatting.YELLOW)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Wall-ev/MaidsoulKitchen/issues")));
        parent.append(component);
        for (Info error : ERRORS) {
            parent.append(CommonComponents.NEW_LINE);
            ResourceLocation taskUid = error.taskUid;
            Mods mod = error.mod;
            Type type = error.type;
            String message = error.message;
            MutableComponent mutableComponent = Component.literal(">- 任务：" + Component.translatable("task.maidsoulkitchen" + taskUid.toString()).getString());
            mutableComponent.append(CommonComponents.NEW_LINE);
            mutableComponent.append(Component.literal("- 模组ID：" + mod.modId + "，加载版本: " + ModUtil.getModVersion(mod.modId)));
            mutableComponent.append(CommonComponents.NEW_LINE);
            mutableComponent.append(Component.literal("- 失败类型为" + type + ": " + message));
            parent.append(mutableComponent);
        }
        parent.append(CommonComponents.NEW_LINE);
        parent.append(Component.literal("-----------------------------------------"));
        player.sendSystemMessage(parent);
    }

    public enum Type {
        MIXIN,
        CLAZZ,
    }

    public record Info(ResourceLocation taskUid, Mods mod, Type type, String message) {
    }

}
