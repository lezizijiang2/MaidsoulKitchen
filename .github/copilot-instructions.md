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
- 🔴 Phase 3 Batch 10: BEGIN - Infrastructure foundation files
- ⏳ Phase 3 Batches 11-23: 60 infrastructure files total
- ⏳ Phase 4 Batches 24-40: 39 mod integration files

**Current**: 76/184 files (41% complete)

### Phase 3 Batch 10 - Dependency Analysis Complete

**Problem Discovered**: Circular dependencies in initial plan
- Utility Actions → Craft Base Classes → IFailGuideUseActionContext → Craft Actions (circular!)

**Revised Strategy**: Port in dependency order, breaking cycles where needed

#### Foundation Layer (No internal dependencies)
1. ✅ maid_storage_manager NeoForge 1.21.1 available (external dep)
2. TargetUtil - only depends on storage_manager.storage.Target
3. CraftGuideOperator2 - check dependencies
4. FailCraftGuideStepData - uses Codec, CompoundTag

#### Craft Layer (Depends on foundation + maid_storage_manager)
5. Craft Base Classes (8 files) - extend maid_storage_manager classes
6. Craft Custom Classes (11 files) - extend maid_storage_manager classes  
7. IFailGuideUseActionContext - depends on craft classes

#### Utility Layer (Depends on craft layer)
8. Utility Actions (6 files) - use craft classes
9. General Utilities (3 files)

### Next Steps for Batch 10

**Option A - Foundation First** (RECOMMENDED):
1. Port TargetUtil (simple, no internal deps)
2. Port CraftGuideOperator2 (if simple)
3. Port FailCraftGuideStepData utility

**Option B - Craft Classes First** (breaks cycles):
1. Port 3 simplest Craft Base Classes
2. Port stub IFailGuideUseActionContext (minimal impl)
3. Complete interface in next batch

**Decision**: Will port foundation utilities first (Option A)

## Batch 10 File List (Pending)
1. TargetUtil.java (~30 lines, LOW complexity)
2. CraftGuideOperator2.java (TBD - check dependencies)
3. FailCraftGuideStepData.java (~80 lines, MEDIUM complexity)
