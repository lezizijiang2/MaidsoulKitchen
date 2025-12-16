# Instructions

During your interaction with the user, if you find anything reusable in this project (e.g. version of a library, model
name), especially about a fix to a mistake you made or a correction you received, you should take note in the `Lessons`
section in the `.github/copilot-instructions.md` file so you will not make the same mistake again.

You should also use the `.github/copilot-instructions.md` file's "scratchpad" section as a Scratchpad to organize your
thoughts. Especially when you receive a new task, you should first review the content of the Scratchpad, clear old
different task if necessary, first explain the task, and plan the steps you need to take to complete the task. You can
use todo markers to indicate the progress, e.g.
[X] Task 1
[ ] Task 2

Also update the progress of the task in the Scratchpad when you finish a subtask.
Especially when you finished a milestone, it will help to improve your depth of task accomplishment to use the
Scratchpad to reflect and plan.
The goal is to help you maintain a big picture as well as the progress of the task. Always refer to the Scratchpad when
you plan the next step.

# Tools

Note all the tools are in python. So in the case you need to do batch processing, you can always consult the python
files and write your own script.

[NOTE TO CURSOR: Since no API key is configured, please ignore both the Screenshot Verification and LLM sections below.]
[NOTE TO USER: If you have configured or plan to configure an API key in the future, simply delete these two notice lines to enable these features.]

## Screenshot Verification

The screenshot verification workflow allows you to capture screenshots of web pages and verify their appearance using
LLMs. The following tools are available:

1. Screenshot Capture:

```bash
venv/bin/python tools/screenshot_utils.py URL [--output OUTPUT] [--width WIDTH] [--height HEIGHT]
```

2. LLM Verification with Images:

```bash
venv/bin/python tools/llm_api.py --prompt "Your verification question" --provider {openai|anthropic} --image path/to/screenshot.png
```

Example workflow:

```python
from screenshot_utils import take_screenshot_sync
from llm_api import query_llm

# Take a screenshot
screenshot_path = take_screenshot_sync('https://example.com', 'screenshot.png')

# Verify with LLM
response = query_llm(
    "What is the background color and title of this webpage?",
    provider="openai",  # or "anthropic"
    image_path=screenshot_path
)
print(response)
```

## LLM

You always have an LLM at your side to help you with the task. For simple tasks, you could invoke the LLM by running the
following command:

```
venv/bin/python ./tools/llm_api.py --prompt "What is the capital of France?" --provider "anthropic"
```

The LLM API supports multiple providers:

- OpenAI (default, model: gpt-4o)
- Azure OpenAI (model: configured via AZURE_OPENAI_MODEL_DEPLOYMENT in .env file, defaults to gpt-4o-ms)
- DeepSeek (model: deepseek-chat)
- Anthropic (model: claude-3-sonnet-20240229)
- Gemini (model: gemini-pro)
- Local LLM (model: Qwen/Qwen2.5-32B-Instruct-AWQ)

But usually it's a better idea to check the content of the file and use the APIs in the `tools/llm_api.py` file to
invoke the LLM if needed.

## Web browser

You could use the `tools/web_scraper.py` file to scrape the web.

```
venv/bin/python ./tools/web_scraper.py --max-concurrent 3 URL1 URL2 URL3
```

This will output the content of the web pages.

## Search engine

You could use the `tools/search_engine.py` file to search the web.

```
venv/bin/python ./tools/search_engine.py "your search keywords"
```

This will output the search results in the following format:

```
URL: https://example.com
Title: This is the title of the search result
Snippet: This is a snippet of the search result
```

If needed, you can further use the `web_scraper.py` file to scrape the web page content.

# Lessons

## User Specified Lessons

- You have a python venv in ./venv. Use it.
- Include info useful for debugging in the program output.
- Read the file before you try to edit it.
- Due to Cursor's limit, when you use `git` and `gh` and need to submit a multiline commit message, first write the
  message in a file, and then use `git commit -F <filename>` or similar command to commit. And then remove the file.
  Include "[Cursor] " in the commit message and PR title.

## Cursor learned

- For search results, ensure proper handling of different character encodings (UTF-8) for international queries
- Add debug information to stderr while keeping the main output clean in stdout for better pipeline integration
- When using seaborn styles in matplotlib, use 'seaborn-v0_8' instead of 'seaborn' as the style name due to recent
  seaborn version changes
- Use 'gpt-4o' as the model name for OpenAI's GPT-4 with vision capabilities
- NeoForge packet format requires implementing CustomPacketPayload interface with TYPE and STREAM_CODEC static fields
- Use IPayloadContext instead of NetworkEvent.Context for packet handling in NeoForge
- StreamCodec.composite() requires proper parameter count matching - use separate codec creation methods for complex
  types

# Scratchpad

## Current Task: Phase 3 Infrastructure Porting

### Overall Progress
- ✅ Phase 1: 8 files (Critical bugs)
- ✅ Phase 2: 68 files (12 mods without storage dependencies)
- ✅ Phase 3 Planning: REMAINING_FILES_ANALYSIS.md created
- ✅ Phase 3 Batch 10: Foundation utilities (2 files) - TargetUtil, FailCraftGuideStepData
- 🔴 Phase 3 Batch 11: IN PROGRESS - Craft base actions (3 files)
- ⏳ Phase 3 Batches 12-22: 55 infrastructure files remaining
- ⏳ Phase 4 Batches 23-40: 39 mod integration files

**Current**: 78/184 files (42% complete) → 81/184 (44% after Batch 11)

### Phase 3 Batch 11 - Craft Base Actions (Simple Ones)

**Ported Files**:
1. ✅ IFailGuideUseActionContext.java (~78 lines) - Interface for failure handling
   - Depends on: TargetUtil ✅, FailCraftGuideStepData ✅
   - Forward references: EnchantCommonPlaceItemAction, EnchantCommonSplitItemAction, FailAction, FailTakeAction
   - These forward references will be resolved in Batches 12-13

2. ✅ EnchantCommonIdleAction.java (~47 lines) - Idle/waiting action
   - Extends CommonIdleAction from maid_storage_manager
   - Implements IFailGuideUseActionContext
   - No Forge APIs, pure NeoForge compatible

3. ✅ EnchantCommonPickupItemAction.java (~55 lines) - Item pickup action
   - Extends CommonPickupItemAction from maid_storage_manager
   - Implements IFailGuideUseActionContext
   - Uses TargetUtil for step creation
   - No Forge APIs, pure NeoForge compatible

**NeoForge Migration Notes**:
- All files use standard Minecraft/Mojang APIs
- No Forge-specific code to migrate
- Dependencies on maid_storage_manager NeoForge 1.21.1

### Next Steps for Batch 12

**Remaining Craft Base Classes** (5 files):
1. EnchantCommonPlaceItemAction.java (~120 lines) - Place items in containers
2. EnchantCommonSplitItemAction.java (~80 lines) - Split item stacks
3. EnchantCommonTakeItemAction.java (~100 lines) - Take items from containers
4. EnchantCommonThrowItemAction.java (~60 lines) - Throw items
5. EnchantCommonAttackAction.java (~90 lines) - Attack actions

**Target**: Port 3-4 files in Batch 12
