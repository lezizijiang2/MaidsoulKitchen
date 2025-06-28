package com.github.wallev.maidsoulkitchen.util.classana;

import com.github.wallev.maidsoulkitchen.MaidsoulKitchen;
import com.github.wallev.maidsoulkitchen.task.TaskInfo;
import com.github.wallev.maidsoulkitchen.util.classana.clazz.MultiClassAnalysisResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TaskLoadError {
    private static final Set<ResourceLocation> ERROR_TASKS = new HashSet<>();

    public static void putError(ResourceLocation taskUid) {
        ERROR_TASKS.add(taskUid);
    }

    public static void reportError(Consumer<Component> consumer) {
        if (ERROR_TASKS.isEmpty()) {
            return;
        }

        MutableComponent parent = Component.translatable("message.maidsoulkitchen.warning.title").withStyle(ChatFormatting.DARK_RED);
        parent.append(CommonComponents.NEW_LINE);
        parent.append(Component.translatable("message.maidsoulkitchen.warning.compat_failed"));
        parent.append(CommonComponents.NEW_LINE);
        MutableComponent issueUrlComponent = Component.translatable("message.maidsoulkitchen.warning.clicked_to_report")
                .withStyle(ChatFormatting.GOLD)
                .withStyle(ChatFormatting.UNDERLINE)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, MaidsoulKitchen.ISSUE_URL)))
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(MaidsoulKitchen.ISSUE_URL))));
        parent.append(issueUrlComponent);
        parent.append(CommonComponents.NEW_LINE);
        MutableComponent fileUrlComponent = Component.translatable("message.maidsoulkitchen.warning.clicked_to_open_file")
                .withStyle(ChatFormatting.GOLD)
                .withStyle(ChatFormatting.UNDERLINE)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, MultiClassAnalysisResult.ROOT_FOLDER.toString())))
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(MultiClassAnalysisResult.LOG_FILE_PATH.toString()))));
        parent.append(fileUrlComponent);
        parent.append(CommonComponents.NEW_LINE);
        MutableComponent component1 = Component.translatable("message.maidsoulkitchen.warning.feedbacked")
                .withStyle(ChatFormatting.GRAY);
        parent.append(component1);
        parent.append(CommonComponents.NEW_LINE);
        for (ResourceLocation errorTask : ERROR_TASKS) {
            TaskInfo task = TaskInfo.by(errorTask);
            assert task != null;
            MutableComponent mutableComponent = Component.translatable("message.maidsoulkitchen.warning.failed_task")
                    .append(getName(errorTask))
                    .append(Component.translatable("message.maidsoulkitchen.warning.failed_modid"))
                    .append(task.getBindMod().modId);
            parent.append(mutableComponent);
            parent.append(CommonComponents.NEW_LINE);
        }
        parent.append(Component.translatable("message.maidsoulkitchen.warning.end"));
        consumer.accept(parent);

        ERROR_TASKS.clear();
    }

    private static MutableComponent getName(ResourceLocation taskUid) {
        String key = String.format("task.%s.%s", taskUid.getNamespace(), taskUid.getPath());
        return Component.translatable(key);
    }
}
