# Corrected Plan - Based on User Feedback

## User Clarification

User specified: "上游分支是1.20.1-dev-1.0" (The upstream branch is 1.20.1-1.0-dev)

## Updated Understanding

The correct upstream branch is `upstream/1.20.1-1.0-dev`, not `upstream/1.20.1-main` or direct `upstream/1.21.1`.

## Current Situation

### Branches Available:
- `upstream/1.20.1-1.0-dev` - Forge-based 1.20.1 development branch (latest: e13a79b)
- `upstream/1.21.1` - NeoForge-based 1.21.1 branch (latest: 6faa84a)

### Key Finding:
`upstream/1.20.1-1.0-dev` has **20+ commits** that are NOT in `upstream/1.21.1`:

```
e13a79b 更新readme
9afb084 0.3.0.4 修正类检测器的检测，修正两平台的发布参数
a9c06ba 0.3.0.4 给TaskInfo添加依赖，修正农夫乐事的错误应用
3b31efc 0.3.0.4 完成多模块构建的修正
726f497 更新readme
8af811d 0.3.0.3 错误的配方加载
917df10 0.3.0.2 移除对胡萝卜厨房摇酒壶的支持，等待日后重新支持
... (and more)
```

## Corrected Task Interpretation

1. **拉取upstream代码，合并1.20.1分支**
   - Create/update a 1.20.1 branch based on `upstream/1.20.1-1.0-dev`
   - This branch uses Forge

2. **cherry-pick上游分支中对1.20.1版本的修改到1.21.1分支中**
   - Cherry-pick relevant commits from `upstream/1.20.1-1.0-dev` to a 1.21.1 branch
   - The 1.21.1 branch should be based on `upstream/1.21.1` (which already uses NeoForge)

3. **迁移forge到neoforge，使用context7**
   - Verify NeoForge migration is complete in the 1.21.1 branch
   - Ensure cherry-picked code works with NeoForge

## Execution Plan

### Step 1: Set up 1.20.1 branch
- [x] Create local branch from `upstream/1.20.1-1.0-dev`
- [x] Verify it uses Forge
- [ ] Document its state

### Step 2: Set up 1.21.1 branch
- [x] Create local branch from `upstream/1.21.1`
- [x] Verify it uses NeoForge
- [ ] Identify commits to cherry-pick

### Step 3: Cherry-pick process
- [ ] Analyze which commits can be cherry-picked
- [ ] Cherry-pick commits one by one
- [ ] Resolve any conflicts with NeoForge migration
- [ ] Test each cherry-pick

### Step 4: Verification
- [ ] Verify NeoForge patterns in cherry-picked code
- [ ] Ensure no Forge-specific code remains
- [ ] Document the changes

## Branches Created

- `1.20.1-work` - Local branch tracking `upstream/1.20.1-1.0-dev` (Forge)
- `1.21.1-work` - Local branch tracking `upstream/1.21.1` (NeoForge)

## Next Actions

1. Document the commits in 1.20.1-1.0-dev
2. Create a cherry-pick strategy
3. Execute cherry-picks with conflict resolution
4. Update documentation
