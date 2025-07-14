<template>
    <div class="search-container">
        <div class="search-bar">
            <div class="search-input-container">
                <svg class="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <circle cx="11" cy="11" r="8"></circle>
                    <path d="M21 21l-4.35-4.35"></path>
                </svg>
                <input
                    v-model="searchQuery"
                    class="search-input"
                    placeholder="搜索 Meme... (使用 #标签名 创建标签)"
                    type="text"
                    @input="handleInput"
                    @keydown="handleKeydown"
                    @keydown.enter="handleEnter"
                />
                <button
                    v-if="searchQuery"
                    class="clear-button"
                    @click="clearSearch"
                >
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <line x1="18" x2="6" y1="6" y2="18"></line>
                        <line x1="6" x2="18" y1="6" y2="18"></line>
                    </svg>
                </button>
            </div>

            <!-- 搜索标签显示区域 -->
            <div v-if="searchTags.length > 0" class="search-tags-container">
                <div class="search-tags">
                    <div
                        v-for="tag in searchTags"
                        :key="tag"
                        class="search-tag"
                    >
                        <span class="tag-text">{{ tag }}</span>
                        <button class="remove-tag-button" @click="removeSearchTag(tag)">
                            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <line x1="18" x2="6" y1="6" y2="18"></line>
                                <line x1="6" x2="18" y1="6" y2="18"></line>
                            </svg>
                        </button>
                    </div>
                </div>
            </div>

            <!-- 选中的标签显示区域 -->
            <div v-if="selectedTag" class="selected-tag-container">
                <div class="selected-tag">
                    <span class="tag-label">筛选标签: {{ selectedTag }}</span>
                    <button class="clear-tag-button" @click="clearSelectedTag">
                        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <line x1="18" x2="6" y1="6" y2="18"></line>
                            <line x1="6" x2="18" y1="6" y2="18"></line>
                        </svg>
                    </button>
                </div>
            </div>

            <div class="search-info">
                <p class="search-tip">
                    {{ getSearchTip() }}
                </p>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import {ref, watch} from "vue";

interface Props {
    modelValue?: string;
    selectedTag?: string;
}

interface Emits {
    "update:modelValue": [value: string];
    "search": [query: string, tags: string[]];
    "tag-clear": [];
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const searchQuery = ref(props.modelValue || "");
const searchTags = ref<string[]>([]);

let searchTimeout: number | undefined;

// 处理输入变化
const handleInput = () => {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        performSearch();
    }, 300);
};

// 处理回车键
const handleEnter = () => {
    processHashtags();
    clearTimeout(searchTimeout);
    performSearch();
};

// 处理键盘事件
const handleKeydown = (event: KeyboardEvent) => {
    // 处理空格键 - 提取井号标签
    if (event.key === " ") {
        processHashtags();
    }

    // 如果按下退格键且输入框为空，删除最后一个标签
    if (event.key === "Backspace" && searchQuery.value === "" && searchTags.value.length > 0) {
        const lastTag = searchTags.value.pop();
        if (lastTag) {
            searchQuery.value = `#${lastTag}`;
        }
    }
};

// 处理井号标签
const processHashtags = () => {
    const text = searchQuery.value;
    const hashtagRegex = /#(\S+)/g;
    const matches = [...text.matchAll(hashtagRegex)];

    if (matches.length > 0) {
        // 提取新的标签
        const newTags = matches.map(match => match[1]);

        // 添加到标签列表（去重）
        newTags.forEach(tag => {
            if (!searchTags.value.includes(tag)) {
                searchTags.value.push(tag);
            }
        });

        // 从输入框中移除井号标签
        let cleanedText = text;
        matches.forEach(match => {
            cleanedText = cleanedText.replace(match[0], "");
        });

        searchQuery.value = cleanedText.trim();
    }
};

// 执行搜索
const performSearch = () => {
    emit("update:modelValue", searchQuery.value);
    emit("search", searchQuery.value, searchTags.value);
};

// 移除搜索标签
const removeSearchTag = (tag: string) => {
    const index = searchTags.value.indexOf(tag);
    if (index > -1) {
        searchTags.value.splice(index, 1);
        // 将标签重新放入搜索框
        if (searchQuery.value) {
            searchQuery.value += ` #${tag}`;
        } else {
            searchQuery.value = `#${tag}`;
        }
        performSearch();
    }
};

// 清空搜索
const clearSearch = () => {
    searchQuery.value = "";
    searchTags.value = [];
    emit("update:modelValue", "");
    emit("search", "", []);
};

// 清空选中的标签
const clearSelectedTag = () => {
    emit("tag-clear");
};

// 获取搜索提示
const getSearchTip = () => {
    const hasQuery = searchQuery.value.trim() !== "";
    const hasTags = searchTags.value.length > 0;
    const hasSelectedTag = props.selectedTag;

    if (hasQuery && hasTags && hasSelectedTag) {
        return `搜索 "${searchQuery.value}"，标签 [${searchTags.value.join(", ")}]，筛选 "${props.selectedTag}"`;
    } else if (hasQuery && hasTags) {
        return `搜索 "${searchQuery.value}" 并包含标签 [${searchTags.value.join(", ")}]`;
    } else if (hasQuery && hasSelectedTag) {
        return `搜索 "${searchQuery.value}" 并筛选标签 "${props.selectedTag}"`;
    } else if (hasTags && hasSelectedTag) {
        return `标签 [${searchTags.value.join(", ")}] 并筛选 "${props.selectedTag}"`;
    } else if (hasQuery) {
        return `搜索 "${searchQuery.value}"`;
    } else if (hasTags) {
        return `搜索标签 [${searchTags.value.join(", ")}]`;
    } else if (hasSelectedTag) {
        return `筛选标签 "${props.selectedTag}"`;
    }
    return "输入关键词搜索，使用 [#标签名] 添加标签";
};

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
    if (newValue !== searchQuery.value) {
        searchQuery.value = newValue || "";
    }
});
</script>

<style scoped>
.search-container {
    position: sticky;
    top: 0;
    z-index: 100;
    background: rgba(26, 26, 26, 0.95);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid #333;
    padding: 20px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.search-bar {
    max-width: 600px;
    margin: 0 auto;
}

.search-input-container {
    position: relative;
    display: flex;
    align-items: center;
    background: #2a2a2a;
    border-radius: 25px;
    padding: 12px 20px;
    border: 2px solid transparent;
    transition: all 0.3s ease;
}

.search-input-container:focus-within {
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
    width: 20px;
    height: 20px;
    color: #888;
    margin-right: 12px;
    flex-shrink: 0;
}

.search-input {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    color: #ffffff;
    font-size: 16px;
}

.search-input::placeholder {
    color: #666;
}

.clear-button {
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background-color 0.3s ease;
}

.clear-button:hover {
    background: rgba(255, 255, 255, 0.1);
}

.clear-button svg {
    width: 16px;
    height: 16px;
    color: #888;
}

/* 搜索标签样式 */
.search-tags-container {
    margin-top: 12px;
}

.search-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.search-tag {
    display: flex;
    align-items: center;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    padding: 6px 12px;
    border-radius: 16px;
    font-size: 13px;
    font-weight: 500;
    animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
    from {
        opacity: 0;
        transform: translateY(-10px) scale(0.8);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.tag-text {
    margin-right: 6px;
}

.remove-tag-button {
    background: none;
    border: none;
    cursor: pointer;
    padding: 2px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background-color 0.3s ease;
}

.remove-tag-button:hover {
    background: rgba(255, 255, 255, 0.2);
}

.remove-tag-button svg {
    width: 12px;
    height: 12px;
    color: white;
}

/* 选中标签样式 */
.selected-tag-container {
    margin-top: 12px;
}

.selected-tag {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 8px 16px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20px;
    color: white;
    font-size: 14px;
    width: fit-content;
    margin: 0 auto;
}

.tag-label {
    margin-right: 8px;
}

.clear-tag-button {
    background: none;
    border: none;
    cursor: pointer;
    padding: 2px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background-color 0.3s ease;
}

.clear-tag-button:hover {
    background: rgba(255, 255, 255, 0.2);
}

.clear-tag-button svg {
    width: 14px;
    height: 14px;
    color: white;
}

.search-info {
    margin-top: 8px;
    text-align: center;
}

.search-tip {
    color: #888;
    font-size: 12px;
    margin: 0;
    font-style: italic;
}

@media (max-width: 768px) {
    .search-container {
        padding: 16px;
    }

    .search-input-container {
        padding: 10px 16px;
    }

    .search-input {
        font-size: 14px;
    }

    .search-tags {
        gap: 6px;
    }

    .search-tag {
        padding: 4px 10px;
        font-size: 12px;
    }
}
</style>
