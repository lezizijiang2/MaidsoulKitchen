package com.github.wallev.maidsoulkitchen.task.farm.handler.berry;


import com.github.wallev.maidsoulkitchen.task.farm.handler.IFarmHandlerManager;

public enum BerryHandlerManager implements IFarmHandlerManager<BerryHandler> {

    MINECRAFT(new VanillaBerryHandler()),
    COMPAT(new CompatBerryHandler());

    private final BerryHandler berryHandler;

    BerryHandlerManager(BerryHandler berryHandler) {
        this.berryHandler = berryHandler;
    }

    public BerryHandler getFarmHandler() {
        return berryHandler;
    }
}
