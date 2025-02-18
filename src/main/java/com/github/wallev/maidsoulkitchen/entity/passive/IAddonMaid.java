package com.github.wallev.maidsoulkitchen.entity.passive;

import net.neoforged.neoforge.common.util.FakePlayer;

import java.lang.ref.WeakReference;

public interface IAddonMaid {

    WeakReference<FakePlayer> getFakePlayer$tlma();

    void initFakePlayer$tlma();

}
