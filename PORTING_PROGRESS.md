# Feature Porting Progress

## Started: 2025-12-15

### Base Information
- **Source**: `upstream/1.20.1-1.0-dev` (Forge 1.20.1, v0.3.0.9)
- **Target**: `origin/1.21.1` (NeoForge 1.21.1)
- **Working Branch**: `feature-port-work` → merge to `copilot/cherry-pick-forge-to-neoforge`

---

## Priority 1: Critical Bug Fixes

### ✅ COMPLETED: Sophisticated Chests Bug Fix (0.2.5.1)
**Commit**: 73532dd
**Issue**: Items not correctly deleted in sophisticated chests, causing work/cook failures

**Changes Applied**:
1. **ChestInventory.java** - Complete refactoring ✅
   - Removed: `ItemInventory`, `WrapperItemHandler`, `BlockPos` tracking
   - Added: `Map<ItemDefinition, ChestItemDef>` for better item tracking
   - Added: Inner class `ChestItemDef` to track items by handler
   - Refactored: `tickScan()` method to iterate per-handler instead of globally
   - Refactored: `reset()`, `clear()`, `initData()` methods
   - Changed: `getAvailable()` return type and logic
   - Converted to NeoForge: `net.minecraftforge.items.IItemHandler` → `net.neoforged.neoforge.items.IItemHandler`

**Status**: ✅ Complete

---

### ✅ COMPLETED: Entity Backpack Item Insert Mixin Fix (0.3.1)
**Commits**: 6a4d46a, db7d317
**Issue**: Incorrect entity backpack item insert mixin - shaker couldn't be inserted into maid backpack

**Changes Applied**:
1. **EntityMaidInsertItemMixin.java** - New mixin class ✅
   - Created new mixin for EntityMaid.canInsertItem method
   - Allows SHAKER item to be inserted into maid backpack
   - Uses @TaskMixin for conditional loading
   
2. **maidsoulkitchen-compat.mixins.json** - Updated ✅
   - Added EntityMaidInsertItemMixin to mixins list

**Status**: ✅ Complete

---

### ✅ COMPLETED: Server Crash Fix (0.2.3)
**Commit**: d4bcf7b
**Issue**: Server crash related to BD (Barbeques Delight) mod loading

**Changes Applied**:
1. **Mods.java** - Already present ✅
   - BD enum entry already exists in current branch
   - No changes needed

**Status**: ✅ Complete (already in base branch)

---

## Priority 2: Compatibility Updates

### [ ] Todo: Storage Manager Compatibility (0.3.0)
**Commit**: 2e64218
**Feature**: 仓管初步兼容

**Status**: Pending

---

### [ ] Todo: Cloth Config Menu Improvements
**Commit**: Multiple
**Feature**: Menu integration improvements

**Status**: Pending

---

## Priority 3: Client Features

### [ ] Todo: Event System Refactor (0.3.0.9)
**Commit**: 9360b94
**Changes**:
- Remove: `RenderSlotHighEvent.java`
- Add: `RenderSlotHighEventLegacy.java`
- Add: `RenderSlotHighEventModern.java`
- Add: `SlotRenderAndTipsHandler.java`

**Status**: Pending

---

## Priority 4: Recipe & Guide System

### [ ] Todo: Recipe Loading Fix (0.3.0.3)
**Commit**: 8af811d
**Issue**: 错误的配方加载

**Status**: Pending

---

### [ ] Todo: Recipe Cleanup (0.3.0.9)
**Commit**: 9360b94
**Changes**: Remove water-related recipes

**Status**: Pending

---

## Priority 5: Build System

### [ ] Todo: Class Analyzer Improvements (0.2.0)
**Commits**: 4c56edc, 5858c0f
**Feature**: Enhanced class analyzer for Mixin files

**Status**: Pending

---

## Notes

### API Migration Checklist
- [x] `IItemHandler` imports updated to NeoForge
- [ ] Event system compatibility verified
- [ ] Registry system compatibility verified
- [ ] Mixin compatibility verified

### Testing Checklist
- [ ] Build succeeds
- [ ] Sophisticated chests fix tested
- [ ] No Forge-specific code remaining
- [ ] All ported features functional

---

**Last Updated**: 2025-12-15 01:00 UTC
