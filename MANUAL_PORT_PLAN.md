# Manual Feature Port Plan

## User Request
"Manual feature port, based on branch 1.21.1, not upstream/1.21.1"

## Approach
- **Base Branch**: `origin/1.21.1` (commit c7ec4ed, NeoForge-based)
- **Source**: `upstream/1.20.1-1.0-dev` (commit 9360b94, Forge-based)
- **Method**: Manual feature porting with NeoForge API adaptation

## Analysis of upstream/1.20.1-1.0-dev

### Latest Version: 0.3.0.9
**Commit**: 9360b94 (2025-12-14)
**Message**: 同步最新进度：0.3.0.9

### Version History (Reverse Chronological):
1. **0.3.0.9** - Latest sync (9360b94)
2. **0.3.0.4** - 类检测器修正, TaskInfo依赖, 多模块构建 (9afb084, a9c06ba, 3b31efc)
3. **0.3.0.3** - 错误的配方加载 (8af811d)
4. **0.3.0.2** - 移除胡萝卜厨房摇酒壶支持 (917df10)
5. **0.3.0.1** - Multiple updates (00a773d, 6b8fd9e)
6. **0.3.1/0.3.0** - entity backpack item insert mixin修正, 仓管兼容 (6a4d46a, db7d317, 2e64218)
7. **0.2.5.1** - sophisticated chests bug fixes (73532dd)
8. **0.2.5** - Various fixes (add85e9)
9. **0.2.3** - Server crash fixes (a87cce3)
10. **0.2.2** - Furnace support, cooking pot, air compressor (24440d9)
11. **0.2.0** - 类分析器增强 (4c56edc, 5858c0f)

### Key Changes in 0.3.0.9 (Latest):

Based on git show output:
- **Build System**: Updates to Legacy/build.gradle and Main/build.gradle
- **Language Files**: Updates to en_us.json and zh_cn.json
- **Recipe Removal**: Removed several recipes (water_cup, wooden_water_bucket, raw_egg_yolk, whole_egg, kettle, water)
- **Event System**: 
  - Removed: RenderSlotHighEvent.java
  - Added: RenderSlotHighEventLegacy.java, RenderSlotHighEventModern.java, SlotRenderAndTipsHandler.java
- **Compat Layer**: Updates to cloth MenuIntegration.java
- **Guide Generators**: Added methods to multiple guide generator classes
- **Common Craft**: Updates to EnchantCommonUseAction.java

### Feature Categories to Port:

#### 1. Core Functionality
- [ ] Entity backpack item insert mixin fixes
- [ ] Storage manager (仓管) compatibility
- [ ] Sophisticated chests bug fixes
- [ ] Server crash fixes

#### 2. Compatibility Updates
- [ ] Cloth config menu integration improvements
- [ ] Furnace support restoration
- [ ] Cooking pot support
- [ ] Various mod compatibility (bakery, cooking, tea, etc.)

#### 3. Client-Side Features
- [ ] Slot rendering and highlight system updates
- [ ] Event system refactoring (Legacy/Modern split)
- [ ] Client setup improvements

#### 4. Recipe System
- [ ] Recipe removal/cleanup (water-related recipes)
- [ ] Recipe loading fixes
- [ ] Cooking guide generator updates

#### 5. Build System
- [ ] Multi-module build fixes
- [ ] Class analyzer improvements
- [ ] TaskInfo dependency updates

## Port Strategy

### Phase 1: Analysis & Preparation (Current)
- [x] Identify source and target branches
- [x] Analyze version history and features
- [ ] Create detailed feature list
- [ ] Prioritize features for porting

### Phase 2: Core Feature Port
Start with critical bug fixes and compatibility:

**Priority 1: Bug Fixes**
1. Server crash fixes (0.2.3)
2. Sophisticated chests fixes (0.2.5.1)
3. Entity backpack mixin fixes (0.3.1)

**Priority 2: Compatibility**
1. Storage manager compatibility (0.3.0)
2. Cloth config improvements
3. Various mod compatibility updates

**Priority 3: Client Features**
1. Slot rendering system
2. Event system updates
3. Client setup

**Priority 4: Recipe & Guide System**
1. Recipe loading fixes
2. Guide generator updates
3. Recipe cleanup

**Priority 5: Build System** (if needed)
1. Class analyzer improvements
2. Build configuration updates

### Phase 3: Testing & Validation
- [ ] Build with NeoForge
- [ ] Test each ported feature
- [ ] Verify no Forge-specific code
- [ ] Check for API compatibility issues

### Phase 4: Documentation
- [ ] Document ported features
- [ ] List any skipped features
- [ ] Update version information
- [ ] Create changelog

## Technical Considerations

### API Migration Patterns

**Network Packets (Forge → NeoForge)**:
- Forge: `NetworkEvent.Context`, manual encode/decode
- NeoForge: `IPayloadContext`, `CustomPacketPayload`, `StreamCodec`

**Event System**:
- Forge: Event bus registration
- NeoForge: Similar but may have API changes

**Registry**:
- Check for registry API differences
- Update deferred registers if needed

**Mixin**:
- Should be mostly compatible
- Check target method signatures for version changes

### Files to Watch

Based on the 0.3.0.9 changes, key areas:
- `Main/src/main/java/com/github/wallev/maidsoulkitchen/client/event/` - Event system
- `Main/src/main/java/com/github/wallev/maidsoulkitchen/compat/` - Mod compatibility
- `Main/src/main/resources/assets/maidsoulkitchen/` - Resources and recipes
- Build system files

## Next Steps

1. Create feature inventory from upstream/1.20.1-1.0-dev
2. Start with highest priority bug fixes
3. Port features incrementally
4. Test after each port
5. Document changes

## Branch Structure

- **Working Branch**: `1.21.1-manual-port` (created from origin/1.21.1)
- **Will merge to**: `copilot/cherry-pick-forge-to-neoforge` for PR

---

**Status**: Planning complete, ready to begin feature porting
**Last Updated**: 2025-12-15
