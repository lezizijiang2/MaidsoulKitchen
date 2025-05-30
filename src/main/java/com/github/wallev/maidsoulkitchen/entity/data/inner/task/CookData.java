package com.github.wallev.maidsoulkitchen.entity.data.inner.task;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CookData implements ITaskData {
    public static final Codec<List<String>> LIST_CODEC = Codec.STRING.listOf().xmap(Lists::newArrayList, Function.identity());
    public static final Codec<CookData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("Mode").forGetter(CookData::mode),
            LIST_CODEC.fieldOf("WhitelistRecs").forGetter(CookData::whitelistRecs),
            LIST_CODEC.fieldOf("BlacklistRecs").forGetter(CookData::blacklistRecs),
            Codec.STRING.fieldOf("SortMode").forGetter(CookData::sortMode)
    ).apply(instance, CookData::new));
    private String mode;
    private List<String> whitelistRecs;
    private List<String> blacklistRecs;
    private String sortMode;
    // 新增排序模式字段

    public CookData() {
        this(Lists.newArrayList());
    }

    public CookData(List<String> blacklistRecs) {
        this(Lists.newArrayList(), blacklistRecs);
    }

    public CookData(List<String> whitelistRecs, List<String> blacklistRecs) {
        this(Mode.BLACKLIST.name, whitelistRecs, blacklistRecs);
    }

    public CookData(String mode, List<String> whitelistRecs, List<String> blacklistRecs) {
        this(mode, whitelistRecs, blacklistRecs, RecipeSortMode.DEFAULT.name);
    }

    public CookData(String mode, List<String> whitelistRecs, List<String> blacklistRecs, String sortMode) {
        this.mode = mode;
        this.whitelistRecs = whitelistRecs;
        this.blacklistRecs = blacklistRecs;
        this.sortMode = sortMode;
    }

    public void addRec(String rec, String mode) {
        if (mode.equals(Mode.WHITELIST.name)) {
            this.whitelistRecs.add(rec);
        } else if (mode.equals(Mode.BLACKLIST.name)) {
            this.blacklistRecs.add(rec);
        }
    }

    public void removeRec(String rec, String mode) {
        if (mode.equals(Mode.WHITELIST.name)) {
            this.whitelistRecs.remove(rec);
        } else if (mode.equals(Mode.BLACKLIST.name)) {
            this.blacklistRecs.remove(rec);
        }
    }

    public void addOrRemoveRec(String rec, String mode) {
        if (mode.equals(Mode.WHITELIST.name)) {
            if (this.whitelistRecs.contains(rec)) {
                this.whitelistRecs.remove(rec);
            } else {
                this.whitelistRecs.add(rec);
            }
        } else if (mode.equals(Mode.BLACKLIST.name)) {
            if (this.blacklistRecs.contains(rec)) {
                this.blacklistRecs.remove(rec);
            } else {
                this.blacklistRecs.add(rec);
            }
        }
    }

    public void setWhitelistRecs(List<String> whitelistRecs) {
        this.whitelistRecs = whitelistRecs;
    }

    public void setBlacklistRecs(List<String> blacklistRecs) {
        this.blacklistRecs = blacklistRecs;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
    }

    public List<String> recs(String mode) {
        if (mode.equals(Mode.WHITELIST.name)) {
            return this.whitelistRecs;
        } else if (mode.equals(Mode.BLACKLIST.name)) {
            return this.blacklistRecs;
        }
        return Collections.emptyList();
    }

    public List<String> whitelistRecs() {
        return whitelistRecs;
    }

    public List<String> blacklistRecs() {
        return blacklistRecs;
    }

    public String mode() {
        return mode;
    }

    public List<String> getRecs() {
        if (this.mode.equals(Mode.WHITELIST.name)) {
            return this.whitelistRecs;
        } else if (this.mode.equals(Mode.BLACKLIST.name)) {
            return this.blacklistRecs;
        } else {
            return Collections.emptyList();
        }
    }

    public String sortMode() {
        return sortMode;
    }

    public CookData withSortMode(RecipeSortMode sortMode) {
        return new CookData(this.mode, this.whitelistRecs, this.blacklistRecs, sortMode.name);
    }

    public enum Mode {
        WHITELIST("whitelist"),
        BLACKLIST("blacklist");

        public final String name;

        Mode(String name) {
            this.name = name;
        }
    }

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
        INGREDIENTS("ingredients"),

        /**
         * 按配方结果数量排序（少到多）
         */
        RESULT("result");

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
}
