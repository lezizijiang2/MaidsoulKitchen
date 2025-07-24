package com.github.wallev.maidsoulkitchen.util.modanalysis;

import com.github.wallev.maidsoulkitchen.util.modutility.Mods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Enhanced mod compatibility analyzer for class, method, and field analysis
 * Ported from upstream 1.20.1 branch commit 15688eb259abdbc033c2c181c5c23482483a567a
 */
public class ModCompatibilityAnalyzer {
    private static final Logger LOGGER = LogManager.getLogger("ModCompatAnalyzer");
    
    private static final Map<String, AnalysisResult> classAnalysisCache = new HashMap<>();
    private static final Map<String, Set<String>> modClassMapping = new HashMap<>();
    private static final Set<String> vanillaClassPrefixes = Set.of(
        "net.minecraft",
        "com.mojang",
        "net.minecraftforge", 
        "org.spongepowered"
    );
    
    public static class AnalysisResult {
        public final boolean exists;
        public final boolean accessible;
        public final String version;
        public final List<String> availableMethods;
        public final List<String> availableFields;
        
        public AnalysisResult(boolean exists, boolean accessible, String version, 
                            List<String> methods, List<String> fields) {
            this.exists = exists;
            this.accessible = accessible;
            this.version = version;
            this.availableMethods = methods != null ? new ArrayList<>(methods) : new ArrayList<>();
            this.availableFields = fields != null ? new ArrayList<>(fields) : new ArrayList<>();
        }
    }
    
    /**
     * Analyzes a class for compatibility with mixin double control system
     */
    public static AnalysisResult analyzeClass(String className) {
        if (classAnalysisCache.containsKey(className)) {
            return classAnalysisCache.get(className);
        }
        
        try {
            Class<?> clazz = Class.forName(className);
            boolean accessible = isClassAccessible(clazz);
            String version = detectClassVersion(clazz);
            
            List<String> methods = new ArrayList<>();
            List<String> fields = new ArrayList<>();
            
            // Analyze methods
            for (Method method : clazz.getDeclaredMethods()) {
                if (!isVanillaMethod(method)) {
                    methods.add(method.getName() + ":" + method.getParameterCount());
                }
            }
            
            // Analyze fields  
            for (Field field : clazz.getDeclaredFields()) {
                if (!isVanillaField(field)) {
                    fields.add(field.getName() + ":" + field.getType().getSimpleName());
                }
            }
            
            AnalysisResult result = new AnalysisResult(true, accessible, version, methods, fields);
            classAnalysisCache.put(className, result);
            
            // Map class to mod
            String modId = detectModForClass(clazz);
            if (modId != null) {
                modClassMapping.computeIfAbsent(modId, k -> new HashSet<>()).add(className);
            }
            
            LOGGER.debug("Analyzed class {} - accessible: {}, methods: {}, fields: {}", 
                        className, accessible, methods.size(), fields.size());
            
            return result;
            
        } catch (ClassNotFoundException e) {
            AnalysisResult result = new AnalysisResult(false, false, null, null, null);
            classAnalysisCache.put(className, result);
            return result;
        } catch (Exception e) {
            LOGGER.warn("Failed to analyze class {}: {}", className, e.getMessage());
            AnalysisResult result = new AnalysisResult(false, false, null, null, null);
            classAnalysisCache.put(className, result);
            return result;
        }
    }
    
    /**
     * Checks if a mod is compatible for mixin application
     */
    public static boolean isModCompatible(Mods mod, String targetClass) {
        if (!mod.versionLoaded) {
            LOGGER.debug("Mod {} version not compatible", mod.modId);
            return false;
        }
        
        AnalysisResult analysis = analyzeClass(targetClass);
        if (!analysis.exists) {
            LOGGER.debug("Target class {} does not exist for mod {}", targetClass, mod.modId);
            return false;
        }
        
        if (!analysis.accessible) {
            LOGGER.debug("Target class {} not accessible for mod {}", targetClass, mod.modId);
            return false;
        }
        
        return true;
    }
    
    /**
     * Enhanced method to check method existence and compatibility
     */
    public static boolean hasCompatibleMethod(String className, String methodName, int paramCount) {
        AnalysisResult analysis = analyzeClass(className);
        if (!analysis.exists) return false;
        
        String methodSignature = methodName + ":" + paramCount;
        return analysis.availableMethods.contains(methodSignature);
    }
    
    /**
     * Enhanced method to check field existence and compatibility  
     */
    public static boolean hasCompatibleField(String className, String fieldName, String fieldType) {
        AnalysisResult analysis = analyzeClass(className);
        if (!analysis.exists) return false;
        
        String fieldSignature = fieldName + ":" + fieldType;
        return analysis.availableFields.contains(fieldSignature);
    }
    
    private static boolean isClassAccessible(Class<?> clazz) {
        try {
            // Try to create an instance or access static members
            clazz.getDeclaredMethods();
            clazz.getDeclaredFields();
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }
    
    private static String detectClassVersion(Class<?> clazz) {
        // Try to extract version information from package or annotations
        Package pkg = clazz.getPackage();
        if (pkg != null) {
            String version = pkg.getImplementationVersion();
            if (version != null) return version;
            
            version = pkg.getSpecificationVersion();
            if (version != null) return version;
        }
        return "unknown";
    }
    
    private static String detectModForClass(Class<?> clazz) {
        String packageName = clazz.getPackage().getName();
        
        // Common mod package patterns
        if (packageName.contains("kitchenkarrot")) return "kitchenkarrot";
        if (packageName.contains("youkaishomecoming")) return "youkaishomecoming";
        if (packageName.contains("farmersdelight")) return "farmersdelight";
        if (packageName.contains("brewinandchewin")) return "brewinandchewin";
        if (packageName.contains("barbequesdelight")) return "barbequesdelight";
        
        return null;  
    }
    
    private static boolean isVanillaMethod(Method method) {
        String className = method.getDeclaringClass().getName();
        return vanillaClassPrefixes.stream().anyMatch(className::startsWith);
    }
    
    private static boolean isVanillaField(Field field) {
        String className = field.getDeclaringClass().getName();  
        return vanillaClassPrefixes.stream().anyMatch(className::startsWith);
    }
    
    /**
     * Gets all analyzed classes for a specific mod
     */
    public static Set<String> getClassesForMod(String modId) {
        return modClassMapping.getOrDefault(modId, Collections.emptySet());
    }
    
    /**
     * Clears analysis cache (useful for development/testing)
     */
    public static void clearCache() {
        classAnalysisCache.clear();
        modClassMapping.clear();
        LOGGER.info("Cleared mod compatibility analysis cache");
    }
    
    /**
     * Gets analysis statistics
     */
    public static void logAnalysisStatistics() {
        LOGGER.info("=== Mod Compatibility Analysis Statistics ===");
        LOGGER.info("Total classes analyzed: {}", classAnalysisCache.size());
        LOGGER.info("Mods with mapped classes: {}", modClassMapping.size());
        
        for (Map.Entry<String, Set<String>> entry : modClassMapping.entrySet()) {
            LOGGER.info("  {}: {} classes", entry.getKey(), entry.getValue().size());
        }
    }
}