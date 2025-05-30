package com.github.wallev.maidsoulkitchen.task.farm.handler;

import com.github.wallev.maidsoulkitchen.api.task.farm.ICompatFarmHandler;
import com.github.wallev.maidsoulkitchen.api.task.farm.IHandlerInfo;

public interface IFarmHandlerManager<T extends ICompatFarmHandler & IHandlerInfo> {

    T getFarmHandler();

}
