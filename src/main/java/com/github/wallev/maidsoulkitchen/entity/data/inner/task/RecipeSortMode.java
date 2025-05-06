package com.github.wallev.maidsoulkitchen.entity.data.inner.task;

/**
 * 配方排序模式枚举
 */
public enum RecipeSortMode {
    /**
     * 默认排序（随机）
     */
    DEFAULT("default"),

    /**
     * 按饱食度排序（高到低）
     */
    NUTRITION("nutrition"),

    /**
     * 按饱和度排序（高到低）
     */
    SATURATION("saturation"),

    /**
     * 按可用食材数量排序（多到少）
     */
    INGREDIENTS("ingredients");

    /**
     * 排序模式名称
     */
    public final String name;

    RecipeSortMode(String name) {
        this.name = name;
    }

    /**
     * 通过名称获取排序模式
     *
     * @param name 名称
     * @return 排序模式，如果找不到则返回默认模式
     */
    public static RecipeSortMode byName(String name) {
        for (RecipeSortMode mode : values()) {
            if (mode.name.equals(name)) {
                return mode;
            }
        }
        return DEFAULT;
    }

    /**
     * 获取下一个排序模式
     *
     * @return 下一个排序模式
     */
    public RecipeSortMode next() {
        RecipeSortMode[] modes = values();
        return modes[(this.ordinal() + 1) % modes.length];
    }
}
