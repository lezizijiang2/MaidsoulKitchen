# Feature Porting Progress

## Started: 2025-12-15

### Base Information
- **Source**: `upstream/1.20.1-1.0-dev` (Forge 1.20.1, v0.3.0.9)
- **Target**: `origin/1.21.1` (NeoForge 1.21.1)
- **Working Branch**: `feature-port-work` → merge to `copilot/cherry-pick-forge-to-neoforge`

---

## Priority 1: Critical Bug Fixes

### ✅ In Progress: Sophisticated Chests Bug Fix (0.2.5.1)
**Commit**: 73532dd
**Issue**: Items not correctly deleted in sophisticated chests, causing work/cook failures

**Changes Required**:
1. **ChestInventory.java** - Complete refactoring
   - Remove: `ItemInventory`, `WrapperItemHandler`, `BlockPos` tracking
   - Add: `Map<ItemDefinition, ChestItemDef>` for better item tracking
   - Add: Inner class `ChestItemDef` to track items by handler
   - Refactor: `tickScan()` method to iterate per-handler instead of globally
   - Refactor: `reset()`, `clear()`, `initData()` methods
   - Change: `getAvailable()` return type and logic
   
2. **Related Files** (need to check compatibility):
   - `ItemDefinition.java` - Verify exists and has `of()` method
   - `ChestInvsData.java` - Update data structure
   - `MaidCookManager.java` - Update usage of ChestInventory
   - `GatherResult.java` - New class added

**API Adaptations Needed**:
- Change `net.minecraftforge.items.IItemHandler` → `net.neoforged.neoforge.items.IItemHandler` ✅ (already done in current branch)

**Status**: Analyzing dependencies

---

### [ ] Todo: Server Crash Fix (0.2.3)
**Commit**: a87cce3
**Issue**: Server crash fixes

**Status**: Pending - will analyze after sophisticated chests fix

---

### [ ] Todo: Entity Backpack Item Insert Mixin Fix (0.3.1)
**Commits**: 6a4d46a, db7d317
**Issue**: Incorrect entity backpack item insert mixin

**Status**: Pending

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
