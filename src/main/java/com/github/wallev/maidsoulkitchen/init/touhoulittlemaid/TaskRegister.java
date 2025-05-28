package com.github.wallev.maidsoulkitchen.init.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.wallev.maidsoulkitchen.config.subconfig.RegisterConfig;
import com.github.wallev.maidsoulkitchen.foundation.utility.Mods;
import com.github.wallev.maidsoulkitchen.task.cook.barbequesdelight.TaskBdBasin;
import com.github.wallev.maidsoulkitchen.task.cook.barbequesdelight.TaskBdGrill;
import com.github.wallev.maidsoulkitchen.task.cook.cuisine.TaskCdCuisineSkillet;
import com.github.wallev.maidsoulkitchen.task.cook.drinkbeer.TaskDbBeerBarrel;
import com.github.wallev.maidsoulkitchen.task.cook.farmersdelight.TaskFdCookPot;
import com.github.wallev.maidsoulkitchen.task.cook.farmersdelight.TaskFdCuttingBoard;
import com.github.wallev.maidsoulkitchen.task.cook.farmersdelight.TaskFdSkillet;
import com.github.wallev.maidsoulkitchen.task.cook.kitchencarrot.TaskKkAirCompressor;
import com.github.wallev.maidsoulkitchen.task.cook.kitchencarrot.TaskKkBrewingBarrel;
import com.github.wallev.maidsoulkitchen.task.cook.minecraft.TaskFurnace;
import com.github.wallev.maidsoulkitchen.task.cook.youkaishomecoming.TaskYhcDryingRack;
import com.github.wallev.maidsoulkitchen.task.cook.youkaishomecoming.TaskYhcFermentationTank;
import com.github.wallev.maidsoulkitchen.task.cook.youkaishomecoming.TaskYhcMoka;
import com.github.wallev.maidsoulkitchen.task.cook.youkaishomecoming.TaskYhcTeaKettle;
import com.github.wallev.maidsoulkitchen.task.farm.*;
import com.github.wallev.maidsoulkitchen.task.other.TaskFeedAnimalT;

public final class TaskRegister {
    private TaskRegister() {
    }

    public static void init(TaskManager manager) {
        if (Mods.MC.isLoaded() && RegisterConfig.COMPAT_MELON_FARM_TASK_ENABLED.get()) {
            manager.add(new TaskCompatMelonFarm());
        }
        if (Mods.MC.isLoaded() && RegisterConfig.BERRY_FARM_TASK_ENABLED.get()) {
            manager.add(new TaskBerryFarm());
        }
        if (Mods.MC.isLoaded() && RegisterConfig.FRUIT_FARM_TASK_ENABLED.get()) {
            manager.add(new TaskFruitFarm());
        }
        if (Mods.MC.isLoaded() && RegisterConfig.FEED_ANIMAL_T_TASK_ENABLED.get()) {
            manager.add(new TaskFeedAnimalT());
        }

        if (Mods.SS.isLoaded() && RegisterConfig.SERENESEASONS_FARM_TASK_ENABLED.get()) {
            manager.add(new TaskSsFarm());
        }
        if (Mods.ES.isLoaded() && RegisterConfig.ECLIPTICSEASONS_FARM_TASK_ENABLED.get()) {
            manager.add(new TaskEsFarm());
        }

        if (Mods.MC.isLoaded() && RegisterConfig.FURNACE_TASK_ENABLED.get()) {
            manager.add(new TaskFurnace());
        }

        if (Mods.FD.isLoaded() && RegisterConfig.FD_COOK_POT_TASK_ENABLED.get()) {
            manager.add(new TaskFdCookPot());
        }
        if (Mods.FD.isLoaded() && RegisterConfig.FD_CUTTING_BOARD_TASK_ENABLED.get()) {
            manager.add(new TaskFdCuttingBoard());
        }
        if (Mods.FD.isLoaded() && RegisterConfig.FD_SKILLET_TASK_ENABLED.get()) {
            manager.add(new TaskFdSkillet());
        }
        if (Mods.CD.isLoaded() && RegisterConfig.CD_CUISINE_SKILLET_TASK_ENABLED.get()) {
            manager.add(new TaskCdCuisineSkillet());
        }
        if (Mods.KK.isLoaded() && RegisterConfig.KK_AIR_COMPRESSOR.get()) {
            manager.add(new TaskKkAirCompressor());
        }
        if (Mods.KK.isLoaded() && RegisterConfig.KK_BREW_BARREL.get()) {
            manager.add(new TaskKkBrewingBarrel());
        }
//        if (Mods.FRD.isLoaded() && RegisterConfig.FR_KETTLE_TASK_ENABLED.get()) {
//            manager.add(new TaskFrKettle());
//        }
        if (Mods.BNCD.isLoaded() && RegisterConfig.BNC_KEY_TASK_ENABLED.get()) {
//            manager.add(new TaskBncKeg());
        }
        if (Mods.BD.isLoaded() && RegisterConfig.BD_BASIN_TASK_ENABLED.get()) {
            manager.add(new TaskBdBasin());
        }
        if (Mods.BD.isLoaded() && RegisterConfig.BD_GRILL_TASK_ENABLED.get()) {
            manager.add(new TaskBdGrill());
        }
        if (Mods.YHCD.isLoaded() && RegisterConfig.YHC_MOKA_TASK_ENABLED.get()) {
            manager.add(new TaskYhcMoka());
        }
        if (Mods.YHCD.isLoaded() && RegisterConfig.YHC_TEA_KETTLE_TASK_ENABLED.get()) {
            manager.add(new TaskYhcTeaKettle());
        }
        if (Mods.YHCD.isLoaded() && RegisterConfig.YHC_DRYING_RACK_TASK_ENABLED.get()) {
            manager.add(new TaskYhcDryingRack());
        }
        if (Mods.YHCD.isLoaded() && RegisterConfig.YHC_FERMENTATION_TANK_TASK_ENABLED.get()) {
            manager.add(new TaskYhcFermentationTank());
        }

        if (Mods.DB.isLoaded() && RegisterConfig.DB_BEER_TASK_ENABLED.get()) {
            manager.add(new TaskDbBeerBarrel());
        }

    }
}
