package com.github.wallev.maidsoulkitchen.mixinmanager;

import com.github.wallev.maidsoulkitchen.util.modanalysis.ModCompatibilityAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Enhanced MixinPlugin with upstream 1.20.1 integration improvements
 * Implements class/method/field analysis and mixin dual control system
 */
public class MixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("MixinPlugin");
    
    @Override
    public void onLoad(String mixinPackage) {
        LOGGER.info("Loading enhanced mixin plugin with mod compatibility analysis");
        // Initialize any necessary analysis systems
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Enhanced mixin application logic with compatibility analysis
        ModCompatibilityAnalyzer.AnalysisResult analysis = ModCompatibilityAnalyzer.analyzeClass(targetClassName);
        
        if (!analysis.exists) {
            LOGGER.debug("Skipping mixin {} - target class {} does not exist", mixinClassName, targetClassName);
            return false;
        }
        
        if (!analysis.accessible) {
            LOGGER.debug("Skipping mixin {} - target class {} not accessible", mixinClassName, targetClassName);
            return false;
        }
        
        LOGGER.debug("Applying mixin {} to {} - analysis passed", mixinClassName, targetClassName);
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Log target analysis for debugging
        for (String target : myTargets) {
            ModCompatibilityAnalyzer.analyzeClass(target);
        }
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        LOGGER.debug("Pre-applying mixin {} to {}", mixinClassName, targetClassName);
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        LOGGER.debug("Successfully applied mixin {} to {}", mixinClassName, targetClassName);
        
        // Log analysis statistics periodically
        if (targetClassName.hashCode() % 100 == 0) {
            ModCompatibilityAnalyzer.logAnalysisStatistics();
        }
    }
}
