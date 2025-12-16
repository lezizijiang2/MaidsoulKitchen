# Remaining Files Analysis for NeoForge 1.21.1 Migration

**Generated**: 2025-12-16  
**Current Progress**: 76/~175 files (43%)  
**Remaining**: ~99 files

---

## Executive Summary

With `maid_storage_manager` NeoForge 1.21.1 now available, all previously blocked files can be ported. This document provides a comprehensive analysis of the remaining 99 files that need to be migrated.

### Key Findings

1. **Infrastructure Dependencies**: 60 files form the core infrastructure needed by remaining mods
2. **Mod Integrations**: 39 files are mod-specific integrations that depend on infrastructure
3. **Complexity**: Infrastructure files must be ported first, then mod integrations in order
4. **Estimated Effort**: 10-12 additional batches (3-5 weeks at current pace)

---

## Phase Structure

### Phase 3: Core Infrastructure (60 files)
Must be completed before any Phase 4 work can begin.

### Phase 4: Storage-Dependent Mods (39 files)
Can only be ported after Phase 3 completion.

---

## Detailed File Breakdown

## ✅ **Already Ported (76 files)**

### Infrastructure (13 files) ✅
- **common/autocraftguide/base/**: 4 files - Core guide generator interfaces
- **common/autocraftguide/click/**: 1 file - Click guide generator interface
- **common/autocraftguide/cookingpot/**: 1 file - Cooking pot guide interface
- **common/autocraftguide/**: 1 file - Lang.java
- **common/inv/**: 4 files - Inventory handler system
- **common/util/lang/**: 3 files - Language utilities (ModLang, MsmLangUtil, TypeLang)

### Mods (63 files) ✅
1. **baker/** (3 files) - Do Bakery mod
2. **bakeries/** (10 files) - Bakeries mod (expanded)
3. **beachparty/** (6 files) - Beach Party mod (expanded)
4. **candlelight/** (6 files) - Candlelight mod (expanded)
5. **copperpot/** (3 files) - Copper Pot mod
6. **cuisinedelight/** (2 files) - Cuisine Delight mod
7. **drinkbeer/** (4 files) - Drink Beer mod
8. **farmersdelight/** (7 files) - Farmers Delight mod
9. **minersdelight/** (3 files) - Miner's Delight mod
10. **simplefarming/** (3 files) - Simple Farming mod
11. **tea_aroma/** (1 file) - Lang.java only (partial)
12. **vinery/** (2 files) - Vinery mod
13. **vintagedelight/** (2 files) - Vintage Delight mod

---

## 🔴 **Phase 3: Core Infrastructure Files (60 files)**

These files MUST be ported before any Phase 4 mods can be completed. They provide the foundation for craft actions, storage management, and utility functions.

### 3.1 Utility Actions (6 files) - **Priority: HIGH**
**Purpose**: Core utilities for action steps, item handling, and targeting

| File | Location | Lines (Est.) | Complexity | Dependencies |
|------|----------|--------------|------------|--------------|
| `EmptyUseStepUtil.java` | common/util/action/ | ~50 | Low | None |
| `IdleStepUtil.java` | common/util/action/ | ~40 | Low | None |
| `ItemPickupUtil.java` | common/util/action/ | ~80 | Medium | None |
| `ItemUseStepUtil.java` | common/util/action/ | ~100 | Medium | storage_manager.craft.action |
| `TargetUtil.java` | common/util/action/ | ~120 | Medium | storage_manager.storage.Target |
| `ToolUseStepUtil.java` | common/util/action/ | ~90 | Medium | storage_manager.craft.action |

**Total**: ~480 lines  
**Dependencies**: `studio.fantasyit.maid_storage_manager.craft.action.*`, `storage.Target`

---

### 3.2 General Utilities (3 files) - **Priority: MEDIUM**
**Purpose**: Recipe finding, slot limiting, and predicate wrapping

| File | Location | Lines (Est.) | Complexity | Dependencies |
|------|----------|--------------|------------|--------------|
| `RecipeFinderUtil.java` | common/util/ | ~150 | High | Recipe system |
| `SlotLimitInvWrapper.java` | common/util/ | ~80 | Medium | IItemHandler |
| `WrappedPredicateHandler.java` | common/util/ | ~60 | Low | IItemHandler |

**Total**: ~290 lines  
**Dependencies**: Minecraft recipe system, NeoForge item handlers

---

### 3.3 Craft Action Base Classes (23 files) - **Priority: CRITICAL**
**Purpose**: Foundation for all craft actions used by mods

**Location**: `common/craft/base/` (all 23 files missing)

These files define the core action types that all mod integrations depend on:

#### Estimated Structure (based on typical craft action patterns):
- **Container Actions** (~8 files): Actions that interact with block entity containers
- **Item Actions** (~5 files): Actions for item manipulation (place, pickup, use)
- **Tool Actions** (~4 files): Actions for tool usage (cutting, hammering, etc.)
- **Context Actions** (~3 files): Actions that provide context data
- **Utility Actions** (~3 files): Helper actions for common patterns

**Total Estimated**: ~2,300 lines (avg 100 lines/file)  
**Complexity**: HIGH - These are abstract base classes  
**Dependencies**: `studio.fantasyit.maid_storage_manager.craft.*` extensively

**Critical Note**: Without these files, NO storage-dependent mods can be ported.

---

### 3.4 Craft Custom Actions (7 files) - **Priority: HIGH**
**Purpose**: Custom specialized actions for specific mod patterns

**Location**: `common/craft/custom/` (all 7 files missing)

| File Category | Estimated Files | Lines (Est.) | Purpose |
|---------------|-----------------|--------------|---------|
| Sound Actions | 2 files | ~120 | Play sounds during craft steps |
| Animation Actions | 2 files | ~150 | Trigger animations |
| Side Actions | 2 files | ~100 | Handle directional inventory |
| Validation Actions | 1 file | ~80 | Validate craft conditions |

**Total Estimated**: ~450 lines  
**Dependencies**: Craft base classes (3.3), storage_manager

---

### 3.5 Storage System (10 files) - **Priority: HIGH**
**Purpose**: Storage management for maids and inventories

**Location**: `common/storage/` (all 10 files missing)

#### Estimated Structure:
- **Storage Handlers** (~4 files): Core storage interaction logic
- **Storage Validators** (~2 files): Validation for storage operations
- **Storage Utilities** (~2 files): Helper functions for storage
- **Storage Registration** (~2 files): Register storage types with storage_manager

**Total Estimated**: ~1,000 lines (avg 100 lines/file)  
**Complexity**: HIGH - Complex interactions with storage_manager  
**Dependencies**: `studio.fantasyit.maid_storage_manager.storage.*`

---

### 3.6 Initialization System (2 files) - **Priority: CRITICAL**
**Purpose**: Register all craft types and storage types with storage_manager

| File | Location | Lines (Est.) | Complexity | Dependencies |
|------|----------|--------------|------------|--------------|
| `AddCraftAndStorageTypes.java` | init/ | ~300 | Very High | ALL craft base, custom, storage files |
| `MaidStorageManagerCompat.java` | init/ | ~150 | High | AddCraftAndStorageTypes, storage_manager |

**Total**: ~450 lines  
**Complexity**: VERY HIGH - Depends on ALL infrastructure  
**Dependencies**: **EVERY** file in sections 3.1-3.5

**Critical Note**: These files tie everything together. They MUST be ported last in Phase 3.

---

### 3.7 Auto Craft Guide Base (8 files) - **Priority: MEDIUM**
**Purpose**: Additional base interfaces for guide generators (already have 7, need 8 more)

**Location**: `common/autocraftguide/base/` and `common/autocraftguide/click/`

**Status**: 7 files already ported, ~8 more estimated missing based on mod patterns

**Total Estimated**: ~400 lines  
**Dependencies**: Craft action system

---

## **Phase 3 Summary**

| Category | Files | Lines (Est.) | Complexity | Order |
|----------|-------|--------------|------------|-------|
| Utility Actions | 6 | ~480 | Medium | 1st |
| General Utilities | 3 | ~290 | Medium | 1st |
| Craft Base Classes | 23 | ~2,300 | Very High | 2nd |
| Craft Custom Actions | 7 | ~450 | High | 3rd |
| Storage System | 10 | ~1,000 | High | 4th |
| Auto Craft Guides | 8 | ~400 | Medium | 5th |
| Initialization | 2 | ~450 | Very High | 6th (LAST) |

**Total Phase 3**: 60 files, ~5,370 lines

**Estimated Batches**: 10-12 batches at 3-5 files per batch  
**Estimated Time**: 3-4 weeks at current pace

---

## 🟡 **Phase 4: Storage-Dependent Mods (39 files)**

These mods can ONLY be ported after Phase 3 is complete.

### 4.1 Tea Aroma (6 files) - **Priority: HIGH**
**Status**: 1/7 complete (Lang.java only)

| File | Location | Lines (Est.) | Purpose |
|------|----------|--------------|---------|
| `GeneratorTaBambooTrayGuide.java` | tea_aroma/bambootray/ | ~120 | Bamboo tray drying guide |
| `GeneratorTaBoilingGuide.java` | tea_aroma/boiling/ | ~100 | Tea boiling guide |
| `GeneratorTaBrewingGuide.java` | tea_aroma/brewing/ | ~150 | Tea brewing guide |
| `GeneratorTaFoamGuide.java` | tea_aroma/foam/ | ~80 | Tea foam guide |
| `TeaBrewingFoamHelper.java` | tea_aroma/util/ | ~60 | Helper for tea brewing/foam |

**Total**: 6 files, ~510 lines  
**Dependencies**: Craft actions, Tea Aroma mod

---

### 4.2 Kitchen Karrot (8 files) - **Priority: HIGH**
**All files missing**

| File | Location | Lines (Est.) | Purpose |
|------|----------|--------------|---------|
| `Lang.java` | kitchenkarrot/ | ~30 | Language provider |
| `GeneratorKkAirCompressorGuide.java` | aircompressor/ | ~100 | Air compressor guide |
| `GeneratorKkBrewingGuide.java` | brewing/ | ~120 | Brewing guide |
| `GeneratorKkPlateCuttingGuide.java` | platecutting/ | ~80 | Plate cutting guide |
| `GeneratorPlateFoodGuide.java` | platefood/ | ~100 | Plate food guide |
| `GeneratorKkShakeGuide.java` | shaker/ | ~130 | Shaker guide |
| `PlayShakeSoundAction.java` | shaker/ | ~40 | Custom sound action |
| `ShakeMenuWrap.java` | shaker/ | ~50 | Menu wrapper |

**Total**: 8 files, ~650 lines  
**Dependencies**: Craft actions, custom actions, Kitchen Karrot mod

---

### 4.3 Dungeons Delight (3 files) - **Priority: MEDIUM**

| File | Location | Lines (Est.) | Purpose |
|------|----------|--------------|---------|
| `Lang.java` | dungeonsdelight/ | ~30 | Language provider |
| `DDMonsterPotInvHandler.java` | cooking/ | ~80 | Monster pot inventory handler |
| `GeneratorDdCookingGuide.java` | cooking/ | ~100 | Cooking guide |

**Total**: 3 files, ~210 lines  
**Dependencies**: Craft actions, Dungeons Delight mod

---

### 4.4 Farm & Charm (9 files) - **Priority: MEDIUM**

| File | Location | Lines (Est.) | Purpose |
|------|----------|--------------|---------|
| `FarmAndCharmLang.java` | farm_and_charm/ | ~30 | Language provider |
| `CookingPotBlockEntityContainerInvRegister.java` | cookingpot/ | ~50 | Container register |
| `GeneratorFarmAndCharmCookingPotGuide.java` | cookingpot/ | ~100 | Cooking pot guide |
| `CraftingBowlBlockEntityContainerInvRegister.java` | crafting_bowl/ | ~50 | Container register |
| `GeneratorFarmAndCharmCraftingBowlGuide.java` | crafting_bowl/ | ~80 | Crafting bowl guide |
| `GeneratorFarmAndCharmRoasterGuide.java` | roaster/ | ~90 | Roaster guide |
| `RoasterBlockEntityContainerInvRegister.java` | roaster/ | ~50 | Container register |
| `GeneratorFarmAndCharmSmallCookingPotGuide.java` | stove/ | ~80 | Small cooking pot guide |
| `StoveBlockEntityContainerInvRegister.java` | stove/ | ~50 | Container register |

**Total**: 9 files, ~580 lines  
**Dependencies**: Craft actions, Farm & Charm mod

---

### 4.5 Immortaler's Delight (4 files) - **Priority: LOW**

| File | Location | Lines (Est.) | Purpose |
|------|----------|--------------|---------|
| `Lang.java` | immortalers_delight/ | ~30 | Language provider |
| `EnchantalCoolerSideSpitItemAction.java` | enchantal_cooler/ | ~60 | Custom side action |
| `GeneratorItdEnchantalCoolerGuide.java` | enchantal_cooler/ | ~120 | Enchantal cooler guide |
| `ImdEnchantalCoolerInvHandler.java` | enchantal_cooler/ | ~80 | Inventory handler |

**Total**: 4 files, ~290 lines  
**Dependencies**: Craft actions, custom actions, Immortaler's Delight mod

---

### 4.6 Kaleidoscope Cookery (5 files) - **Priority: LOW**

| File | Location | Lines (Est.) | Purpose |
|------|----------|--------------|---------|
| `Lang.java` | kaleidoscopecookery/ | ~30 | Language provider |
| `GeneratorKcChoppingBoardGuide.java` | choppingboard/ | ~90 | Chopping board guide |
| `CookerAction.java` | cookery/ | ~70 | Custom cooker action |
| `GeneratorKcCookerGuide.java` | cookery/ | ~120 | Cooker guide |
| `GeneratorKcStockPotGuide.java` | stockpot/ | ~100 | Stock pot guide |

**Total**: 5 files, ~410 lines  
**Dependencies**: Craft actions, custom actions, Kaleidoscope Cookery mod

---

### 4.7 Meadow (3 files) - **Priority: LOW**

| File | Location | Lines (Est.) | Purpose |
|------|----------|--------------|---------|
| `MeadowLang.java` | meadow/ | ~30 | Language provider |
| `CheeseFormBlockEntityContainerInvRegister.java` | cheese_form/ | ~50 | Container register |
| `GeneratorMeadowCheeseFormGuide.java` | cheese_form/ | ~100 | Cheese form guide |

**Total**: 3 files, ~180 lines  
**Dependencies**: Craft actions, Meadow mod

---

### 4.8 Youkaishomecoming (10 files) - **Priority: LOW**
**Most complex remaining mod**

| File | Location | Lines (Est.) | Purpose |
|------|----------|--------------|---------|
| `Lang.java` | youkaishomecoming/ | ~30 | Language provider |
| `IYhcRecipe.java` | base/ | ~50 | Recipe interface |
| `GeneratorYhcBasinGuide.java` | basin/ | ~110 | Basin guide |
| `GeneratorYhcCuisineGuide.java` | cusine/ | ~130 | Cuisine guide |
| `GeneratorYhcDryingRackGuide.java` | dryingrack/ | ~100 | Drying rack guide |
| `GeneratorYhcFermentGuide.java` | ferment/ | ~120 | Ferment guide |
| `GeneratorYhcKettleGuide.java` | kettle/ | ~140 | Kettle guide |
| `KettlePotInvFactory.java` | kettle/ | ~60 | Inventory factory |
| `GeneratorYhcMokaPotGuide.java` | mokapot/ | ~120 | Moka pot guide |
| `MokaPotInvFactory.java` | mokapot/ | ~60 | Inventory factory |

**Total**: 10 files, ~920 lines  
**Dependencies**: Craft actions, Youkaishomecoming mod

---

## **Phase 4 Summary**

| Mod | Files | Lines (Est.) | Priority | Batch Est. |
|-----|-------|--------------|----------|------------|
| Tea Aroma | 6 | ~510 | High | 2 batches |
| Kitchen Karrot | 8 | ~650 | High | 2-3 batches |
| Dungeons Delight | 3 | ~210 | Medium | 1 batch |
| Farm & Charm | 9 | ~580 | Medium | 2-3 batches |
| Immortaler's Delight | 4 | ~290 | Low | 1 batch |
| Kaleidoscope Cookery | 5 | ~410 | Low | 1-2 batches |
| Meadow | 3 | ~180 | Low | 1 batch |
| Youkaishomecoming | 10 | ~920 | Low | 3-4 batches |

**Total Phase 4**: 48 files, ~3,750 lines

**Estimated Batches**: 12-18 batches at 3-5 files per batch  
**Estimated Time**: 3-5 weeks at current pace

---

## Overall Migration Summary

| Phase | Files | Lines (Est.) | Status | Batches | Time Est. |
|-------|-------|--------------|--------|---------|----------|
| **Phase 1** | 8 | ~400 | ✅ Complete | 1 | Done |
| **Phase 2** | 68 | ~4,000 | ✅ Complete | 9 | Done |
| **Phase 3** | 60 | ~5,370 | 🔴 Not Started | 10-12 | 3-4 weeks |
| **Phase 4** | 48 | ~3,750 | 🔴 Blocked | 12-18 | 3-5 weeks |
| **TOTAL** | 184 | ~13,520 | 41% Complete | 32-40 | 6-9 weeks |

---

## Critical Dependencies

### Phase 3 Must Complete First
**All Phase 4 mods are blocked until Phase 3 completes**

```
Phase 3 Order:
1. Utility Actions (6 files) + General Utilities (3 files)
2. Craft Base Classes (23 files) - MOST CRITICAL
3. Craft Custom Actions (7 files)
4. Storage System (10 files)
5. Auto Craft Guide Extensions (8 files)
6. Initialization System (2 files) - MUST BE LAST
```

### NeoForge API Migrations Required

All Phase 3 and 4 files will need:
- `studio.fantasyit.maid_storage_manager.*` → NeoForge 1.21.1 version
- `net.minecraftforge.*` → `net.neoforged.*` 
- Recipe API updates (Container → RecipeInput where needed)
- Capabilities API updates

---

## Recommended Porting Strategy

### Phase 3: Infrastructure (Priority 1)

**Batch 10**: Utility Actions Part 1 (3 files)
- EmptyUseStepUtil.java
- IdleStepUtil.java
- ItemPickupUtil.java

**Batch 11**: Utility Actions Part 2 (3 files) + General Utilities (1 file)
- ItemUseStepUtil.java
- TargetUtil.java
- ToolUseStepUtil.java
- RecipeFinderUtil.java

**Batch 12-16**: Craft Base Classes (23 files, 5 batches × ~5 files)
- Split into logical groups (container, item, tool, context, utility actions)

**Batch 17-18**: Craft Custom Actions (7 files, 2 batches)
- Sound + Animation actions (Batch 17)
- Side + Validation actions (Batch 18)

**Batch 19-21**: Storage System (10 files, 3 batches)
- Storage handlers (Batch 19)
- Storage validators + utilities (Batch 20)
- Storage registration (Batch 21)

**Batch 22**: Auto Craft Guide Extensions (8 files)
- Port remaining guide interfaces

**Batch 23**: Initialization System (2 files)
- AddCraftAndStorageTypes.java
- MaidStorageManagerCompat.java
- **CRITICAL**: Verify ALL infrastructure works before proceeding

---

### Phase 4: Storage-Dependent Mods (Priority 2)

**High Priority Mods First**:

**Batch 24-25**: Tea Aroma (6 files, 2 batches)

**Batch 26-28**: Kitchen Karrot (8 files, 3 batches)

**Medium Priority**:

**Batch 29**: Dungeons Delight (3 files)

**Batch 30-32**: Farm & Charm (9 files, 3 batches)

**Low Priority (Can be done last)**:

**Batch 33**: Immortaler's Delight (4 files)

**Batch 34-35**: Kaleidoscope Cookery (5 files, 2 batches)

**Batch 36**: Meadow (3 files)

**Batch 37-40**: Youkaishomecoming (10 files, 4 batches)

---

## Risk Assessment

### High Risks

1. **Craft Base Classes Complexity**: 23 files with complex dependencies
   - **Mitigation**: Port in small increments, test each batch thoroughly
   - **Fallback**: Use Context7 for NeoForge documentation

2. **Storage Manager Integration**: Complex API surface area
   - **Mitigation**: Study maid_storage_manager NeoForge 1.21.1 source first
   - **Fallback**: Contact storage_manager maintainer if needed

3. **Init System Dependencies**: Depends on ALL infrastructure
   - **Mitigation**: Port init files LAST, validate everything before
   - **Fallback**: Incremental testing with dummy registrations

### Medium Risks

1. **Custom Actions**: Unique patterns per mod
   - **Mitigation**: Study upstream patterns carefully
   - **Fallback**: Simplify if needed, document limitations

2. **Recipe API Changes**: 1.21.1 recipe system differences
   - **Mitigation**: Pattern match with working FD integration
   - **Fallback**: Use RecipeInput wrappers

### Low Risks

1. **Language Files**: Simple translations
2. **Guide Generators**: Follow established patterns
3. **Container Registers**: Similar to already-ported files

---

## Success Criteria

### Phase 3 Complete When:
- ✅ All 60 infrastructure files ported and compiling
- ✅ Init system successfully registers craft types
- ✅ No Forge API imports remaining
- ✅ At least one test mod (Tea Aroma Lang.java) works with new infrastructure

### Phase 4 Complete When:
- ✅ All 48 mod integration files ported
- ✅ All guide generators producing correct guides
- ✅ All custom actions working
- ✅ 100% NeoForge 1.21.1 compatibility

### Final Success Criteria:
- ✅ 184/184 files ported (100%)
- ✅ All mods integrated and functional
- ✅ Build compiles without errors
- ✅ No Forge API dependencies remaining
- ✅ Full NeoForge 1.21.1 compliance

---

## Next Steps

1. **Review this analysis** with stakeholders
2. **Confirm maid_storage_manager NeoForge 1.21.1 compatibility** 
3. **Begin Phase 3 Batch 10** with utility actions
4. **Update scratchpad** with Phase 3 progress tracking
5. **Re-evaluate after Phase 3 Batch 15** (midpoint check)

---

## Questions for Clarification

1. Should we prioritize speed (larger batches) or safety (smaller batches)?
2. Are there any specific mods in Phase 4 that are higher priority than listed?
3. Should we validate Phase 3 with test builds before starting Phase 4?
4. Any specific NeoForge API patterns to follow from existing codebase?

---

**Document Version**: 1.0  
**Last Updated**: 2025-12-16  
**Next Review**: After Phase 3 Batch 15 (infrastructure midpoint)
