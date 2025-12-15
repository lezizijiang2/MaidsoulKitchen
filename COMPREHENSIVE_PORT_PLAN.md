# Comprehensive Port Plan - Complete Migration Strategy

## User Directive

"如果没有对应的代码，那就从1.20.1一致过来；如果需要项目结构重构，那就进行重构"

**Translation**: If code doesn't exist in 1.21.1, copy it from 1.20.1. If project structure needs refactoring, then refactor it.

**Strategy**: Complete comprehensive migration from upstream/1.20.1-1.0-dev (Forge 1.20.1, v0.3.0.9) to current branch with full NeoForge adaptation.

---

## Current Status

### Already Completed ✅
- Priority 1: Critical bug fixes (3 features, 159 lines)
- Priority 3: Client event system refactor (1 feature, 107 lines)
- Total: 4 features, 266 lines, 100% NeoForge migration

### Now Executing: Complete Remaining Port

---

## Analysis Results

### File Count Comparison:
- **Current Branch**: 404 Java files
- **Upstream Main/**: 655 Java files  
- **Gap**: ~251 files potentially missing or differently organized

### Structure Comparison:
- **Current**: Single module in `src/main/java/`
- **Upstream**: Multi-module with `Main/src/` and `Legacy/src/`
- **Decision**: Keep single module, merge both structures

---

## Port Strategy

### Phase 1: File-by-File Comparison ✅ (In Progress)

**Approach**:
1. Extract complete file list from upstream/1.20.1-1.0-dev
2. Normalize paths (remove Main/ prefix)
3. Compare with current branch files
4. Identify:
   - Files missing entirely
   - Files with different content (need update)
   - Files unique to current (keep as-is)

**Tools**:
```bash
# Get upstream file list
git ls-tree -r --name-only upstream/1.20.1-1.0-dev \
  | grep "Main/src/main/java" \
  | sed 's|Main/src/main/java/||' \
  > upstream_files_normalized.txt

# Get current file list  
find src/main/java -type f -name "*.java" \
  | sed 's|src/main/java/||' \
  > current_files_normalized.txt

# Find missing files
comm -23 upstream_files_normalized.txt current_files_normalized.txt \
  > files_to_port.txt
```

### Phase 2: Priority 2 - Compatibility Features

**Features to Port**:

1. **Storage Manager Compatibility** (commit 463c548)
   - Search for storage manager related code in upstream
   - Port missing integration files
   - Adapt to NeoForge APIs

2. **Enhanced Mod Compatibility**
   - Identify mod compatibility additions in upstream
   - Port missing compatibility layers
   - Update existing compatibility code

3. **Cloth Config Enhancements**
   - Compare cloth config integration
   - Port missing features
   - Keep existing working code

**Execution**:
- Extract files from relevant commits
- Copy to appropriate locations
- Migrate APIs to NeoForge
- Test compilation

### Phase 3: Priority 4 - Recipe & Guide System

**Features to Port**:

1. **Recipe System** (commit 8af811d, 917df10)
   - Check if recipe system exists
   - Port recipe loading fixes
   - Port recipe cleanup (water-related removals)
   - Create missing recipe JSON files

2. **Guide Generator**
   - Check if guide system exists  
   - Port guide generator code
   - Port guide generation improvements

**Execution**:
- Identify recipe-related files in upstream
- Copy missing recipe system components
- Adapt to NeoForge recipe APIs
- Copy/remove recipe JSON files as needed

### Phase 4: Priority 5 - Build System (Selective)

**Features to Port**:

1. **Class Analyzer** (commit 9afb084)
   - Port class analyzer code
   - **Decision**: Place in `src/main/java/` as utility package
   - **Skip**: buildSrc/ multi-module structure
   - Reason: Avoid breaking current working build

2. **TaskInfo Dependencies** (commit a9c06ba)
   - Port TaskInfo dependency changes
   - Update TaskInfo management code

3. **Build Configuration**
   - Port relevant gradle configuration improvements
   - Skip multi-module build restructure

**Execution**:
- Extract class analyzer from upstream
- Place in modclazzchecker package (already exists!)
- Port TaskInfo changes
- Update gradle configs selectively

### Phase 5: Additional Features

**From Recent Commits**:
- 0.3.0.9: Latest updates (commit 9360b94)
- 0.3.0.8: Additional features
- 0.3.0.7: More improvements
- Check all commits from merge-base to HEAD

**Execution**:
- Review all commits in range
- Identify additional features
- Port systematically

### Phase 6: NeoForge API Migration

**For All New Code**:
- `net.minecraftforge.*` → `net.neoforged.*`
- `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS`
- `IItemHandler` imports
- Event system imports
- FML imports
- Registry imports

**Validation**:
```bash
# Check for remaining Forge imports
grep -r "net\.minecraftforge" src/main/java/

# Should return: no results (or only comments)
```

### Phase 7: Testing & Validation

**Build Tests**:
```bash
./gradlew clean
./gradlew build
```

**Expected**: Clean compilation with no errors

**Verification**:
- All Priority 1-5 features present
- 100% NeoForge API usage
- No Forge imports remaining
- Build succeeds
- No deprecated APIs

---

## Execution Tracking

### Milestones:

- [ ] **Milestone 1**: Complete file inventory (Phase 1)
- [ ] **Milestone 2**: Port Priority 2 features (Phase 2)
- [ ] **Milestone 3**: Port Priority 4 features (Phase 3)
- [ ] **Milestone 4**: Port Priority 5 features (Phase 4)
- [ ] **Milestone 5**: Port additional features (Phase 5)
- [ ] **Milestone 6**: Complete API migration (Phase 6)
- [ ] **Milestone 7**: Validation & testing (Phase 7)

### Progress Tracking:
See `MIGRATION_TRACKING.md` for detailed per-file progress

---

## Risk Mitigation

### Risks:
1. **Build Breakage**: Incremental commits allow rollback
2. **API Incompatibility**: Systematic NeoForge migration per phase
3. **Merge Conflicts**: Work in isolated branch
4. **Feature Regression**: Comprehensive testing after each phase

### Mitigation:
- Commit after each complete feature port
- Test build after each commit
- Document all changes in detail
- Keep documentation files updated

---

## Success Criteria

### Must Have:
✅ All critical bugs fixed (Priority 1)
✅ Client features working (Priority 3)
⏳ All compatibility features ported (Priority 2)
⏳ Recipe & guide system complete (Priority 4)
⏳ Build utilities ported (Priority 5)
⏳ 100% NeoForge API migration
⏳ Clean build with no errors
⏳ Comprehensive documentation

### Optional/Future:
- Multi-module restructure (explicitly skipped per decision)
- Additional optimization
- Performance improvements

---

## Timeline Estimate

**Phase 1**: ~1 commit (inventory)
**Phase 2**: ~5-10 commits (compatibility features)
**Phase 3**: ~5-10 commits (recipe/guide system)
**Phase 4**: ~3-5 commits (build utilities)
**Phase 5**: ~5-10 commits (additional features)
**Phase 6**: ~2-3 commits (API migration fixes)
**Phase 7**: ~1-2 commits (validation & docs)

**Total**: ~22-41 commits estimated

---

## Current Status

**Phase**: 1 (File Inventory)
**Progress**: Analysis in progress
**Next**: Create FILE_DIFF_ANALYSIS.md with complete file comparison

---

Last Updated: 2025-12-15
Status: 🔄 In Progress
