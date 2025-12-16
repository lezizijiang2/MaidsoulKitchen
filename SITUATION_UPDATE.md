# Situation Update - Cherry-Pick Analysis

## User Clarification Received

**用户反馈**: "上游分支是1.20.1-dev-1.0" (The upstream branch is 1.20.1-1.0-dev)

## Current Situation

### Branches Analysis

**Source Branch**: `upstream/1.20.1-1.0-dev`
- Platform: **Forge** (ForgeGradle)
- Minecraft Version: 1.20.1
- Latest Commit: `e13a79b` (更新readme)
- Mod Version: 0.3.0.4+

**Target Branch**: `upstream/1.21.1`
- Platform: **NeoForge** 21.1.125
- Minecraft Version: 1.21.1
- Latest Commit: `6faa84a` (Merge pull request #20)
- Already migrated to NeoForge

**Merge Base**: `ee90f6e` (upload log)

### Commits to Integrate

**Total**: 70 commits from merge base to HEAD of `upstream/1.20.1-1.0-dev`

**Version Progression in 1.20.1-1.0-dev**:
- 0.3.0.4 - Latest (类检测器修正, TaskInfo依赖, 多模块构建)
- 0.3.0.3 - 配方加载错误修正
- 0.3.0.2 - 胡萝卜厨房支持移除
- 0.3.0.1 - Multiple updates
- 0.3.0 - 仓管初步兼容
- 0.2.5.1 - sophisticated chests bug fixes
- 0.2.5 - Various fixes
- 0.2.3 - Server crash fixes
- 0.2.2 - Furnace support restoration

### Merge Test Results

Attempted merge of `upstream/1.20.1-1.0-dev` into `upstream/1.21.1`:

**Result**: **50+ conflict files**

**Types of Conflicts**:
1. **API Changes (Forge → NeoForge)**:
   - Network packet format changes
   - Event system changes
   - Registry changes

2. **File Structure Changes**:
   - `src/main/java/` → `Main/src/main/java/`
   - File renames and moves
   - Package reorganization

3. **Content Conflicts**:
   - GUI widgets and buttons
   - Client initialization
   - JEI compatibility
   - Data structures
   - Items and inventories

**Example Conflicts**:
```
CONFLICT: Main/src/main/java/com/github/wallev/maidsoulkitchen/client/gui/widget/button/CookBagGuiSideTabButton.java
CONFLICT: Main/src/main/java/com/github/wallev/maidsoulkitchen/event/ItemTabEvent.java
CONFLICT: Main/src/main/java/com/github/wallev/maidsoulkitchen/init/ModContainers.java
CONFLICT (rename/delete): Network packet files renamed/moved
CONFLICT (modify/delete): TaskRegister.java reorganization
```

## Challenges

### 1. Platform Migration
- Forge uses different APIs than NeoForge
- Network packet system completely different
- Event handling changes

### 2. Minecraft Version
- 1.20.1 → 1.21.1 API differences
- Some methods changed signatures
- Registry system updates

### 3. Code Reorganization
- 1.21.1 has different package structure
- Files moved to `Main/` subdirectory
- File naming conventions changed

### 4. Scale
- 70 commits is a large volume
- Each commit may have conflicts
- Testing each change is time-consuming

## Recommendations

### Option 1: Assisted Manual Merge (Recommended)
Given the complexity, recommend a hybrid approach:

1. **List Critical Features Missing in 1.21.1**
   - From version history, identify major features
   - Focus on user-facing functionality
   - Skip internal refactoring commits

2. **Manual Port by Feature**
   - Port each feature individually
   - Adapt to NeoForge from the start
   - Test each feature before next

3. **Document Skipped Items**
   - Build system changes (already different)
   - Version-specific fixes (may not apply)
   - Refactoring commits (already done differently)

**Time Estimate**: 4-8 hours of focused work

### Option 2: Full Automated Merge
Resolve all 50+ conflicts manually:

**Pros**:
- Preserves all commits
- Complete history

**Cons**:
- Very time-consuming (8-16 hours)
- High risk of introducing bugs
- Many conflicts may not be meaningful

### Option 3: Selective Cherry-Pick
Pick only the most important commits:

**Criteria**:
- Bug fixes that affect gameplay
- New features visible to users
- Critical stability fixes

**Skip**:
- Build system changes
- Code organization commits
- Already-present features

**Time Estimate**: 2-4 hours

## Proposed Action Plan

### Phase 1: Feature Inventory (Current)
- [x] Identify source and target branches
- [x] Count commits (70 total)
- [x] Test merge (50+ conflicts)
- [ ] List missing features in 1.21.1

### Phase 2: Priority Assessment
Need user input on:
1. Which features from 0.3.0.x are most important?
2. Are there specific bugs that need fixing?
3. Is complete version parity required, or selective features OK?

### Phase 3: Implementation
Based on chosen approach:
- **Manual Port**: Port features one by one
- **Selective Cherry-Pick**: Pick critical commits
- **Full Merge**: Resolve all conflicts (not recommended)

### Phase 4: Testing & Validation
- Build with NeoForge
- Test each ported feature
- Verify no Forge code remains
- Update documentation

## Immediate Next Steps

Recommend pausing automated cherry-pick and getting user guidance on:

1. **Priority Features**: Which features from 1.20.1-1.0-dev are critical?
2. **Approach**: Manual port, selective pick, or full merge?
3. **Testing**: What level of testing is expected?

This will prevent wasted effort on low-priority items and ensure focus on what matters most.

## Files Created

- `CORRECTED_PLAN.md` - Initial understanding after user feedback
- `CHERRY_PICK_STRATEGY.md` - Detailed strategy options
- `SITUATION_UPDATE.md` - This file (current situation analysis)

## Branches Created

- `1.20.1-work` - Local tracking `upstream/1.20.1-1.0-dev`
- `1.21.1-work` - Local tracking `upstream/1.21.1`

---

**Status**: Awaiting user guidance on approach and priorities
**Last Updated**: December 12, 2025
