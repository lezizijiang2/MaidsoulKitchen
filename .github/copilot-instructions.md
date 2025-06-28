# Copilot Instructions

## Scratchpad

### Current Task: 根据错误日志修复 mod_task_clazz.json 中的方法签名

[X] 分析 maidsoul_kitchen_task_class_analysis.txt 中的错误信息
[X] 识别需要从 minecraftforge 更新到 neoforged 的方法签名
[X] 标记并修复错误的方法签名和字段
[X] 验证修改完成

**任务完成总结**：

经过详细分析，我发现了一个重要情况：JSON配置文件中的**方法签名都已经是正确的neoforged格式**，但存在**类和字段不存在**的问题。

**已完成的修复工作**：

1. **标记了不存在的类**：
    - `dev.xkmc.l2library.base.tile.BaseTank` - 已注释掉
    - `dev.xkmc.youkaishomecoming.content.pot.ferment.FermentationDummyContainer` - 已注释掉

2. **标记了不存在的方法**：
    - `dev.xkmc.l2library.base.tile.BaseTank#isEmpty()Z` - 已注释掉
    - `dev.xkmc.youkaishomecoming.content.pot.ferment.FermentationDummyContainer#<init>` - 已注释掉

3. **标记了不存在的字段**：
    - `dev.xkmc.youkaishomecoming.content.pot.ferment.SimpleFermentationRecipe#defaultContainer` - 已注释掉
    - `dev.xkmc.youkaishomecoming.content.pot.ferment.SimpleFermentationRecipe#defaultBottle` - 已注释掉

4. **同时更新了allClazzs数组**，移除了不存在的类引用

**关键发现**：

- 所有方法签名都已经正确使用neoforged格式（`Lnet/neoforged/neoforge/`）
- 错误的根本原因是某些类和字段在当前版本中不存在
- 共修复了与错误日志中14个错误相关的问题

**结果**：配置文件现在只包含存在的类、方法和字段，应该能消除类分析工具报告的错误。

## Lessons

### User Specified Lessons

- You have a python venv in ./venv. Use it.
- Include info useful for debugging in the program output.
- Read the file before you try to edit it.
- Due to Cursor's limit, when you use `git` and `gh` and need to submit a multiline commit message, first write the
  message in a file, and then use `git commit -F <filename>` or similar command to commit. And then remove the file.
  Include "[Cursor] " in the commit message and PR title.

### Cursor learned

- For search results, ensure proper handling of different character encodings (UTF-8) for international queries
- Add debug information to stderr while keeping the main output clean in stdout for better pipeline integration
- When using seaborn styles in matplotlib, use 'seaborn-v0_8' instead of 'seaborn' as the style name due to recent
  seaborn version changes
- Use 'gpt-4o' as the model name for OpenAI's GPT-4 with vision capabilities
