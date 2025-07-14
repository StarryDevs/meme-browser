<template>
    <div id="app">
        <div class="app-content">
            <header class="app-header">
                <h1 class="app-title">
                    <span class="title-icon">🎭</span>
                    Meme 浏览器
                </h1>
                <p class="app-subtitle">发现最有趣的内容</p>
            </header>

            <SearchBar
                v-model="searchQuery"
                :selected-tag="selectedTag"
                @search="handleSearch"
                @tag-clear="handleTagClear"
            />

            <main class="main-content">
                <div v-if="!loading && memes.length === 0" class="empty-state">
                    <div class="empty-icon">🔍</div>
                    <h3 class="empty-title">没有找到相关内容</h3>
                    <p class="empty-description">
                        {{ searchQuery || selectedTag ? '试试其他关键词或标签' : '暂无 Meme 内容' }}
                    </p>
                </div>

                <VirtualMemeList
                    v-else
                    :columns-desktop="2"
                    :columns-mobile="1"
                    :error="error"
                    :has-more="hasMore"
                    :items="memes"
                    :loading="loading"
                    :row-height="420"
                    @retry="retry"
                    @tag-click="handleTagClick"
                    @load-more="loadMore"
                />
            </main>
        </div>
    </div>
</template>

<script lang="ts" setup>
import {onMounted, ref, watch} from "vue";
import SearchBar from "./components/SearchBar.vue";
import VirtualMemeList from "./components/VirtualMemeList.vue";
import {memeApi} from "./scripts/api";
import type {Meme} from "./types/meme";

const memes = ref<Meme[]>([]);
const loading = ref(false);
const error = ref("");
const searchQuery = ref("");
const searchTags = ref<string[]>([]);
const selectedTag = ref("");
const currentPage = ref(1);
const hasMore = ref(true);
const loadMemes = async (reset = false) => {
    if (loading.value || (!hasMore.value && !reset)) return;
    loading.value = true;
    error.value = "";

    try {
        const page = reset ? 1 : currentPage.value;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        const searchParams: any = {page, limit: 20};

        // 添加搜索参数
        if (searchQuery.value) {
            searchParams.query = searchQuery.value;
        }
        if (searchTags.value.length > 0) {
            searchParams.tags = searchTags.value;
        }
        if (selectedTag.value) {
            searchParams.tag = selectedTag.value;
        }

        let response;
        if (searchQuery.value || searchTags.value.length > 0 || selectedTag.value) {
            response = await memeApi.searchMemes(searchParams);
        } else {
            response = await memeApi.getMemes(searchParams);
        }

        if (reset) {
            memes.value = response.data;
            currentPage.value = 1;
        } else {
            memes.value = [...memes.value, ...response.data];
        }

        hasMore.value = response.hasMore;
        currentPage.value = page + 1;

    } catch (err) {
        error.value = "加载失败，请稍后重试";
        console.error("加载memes失败:", err);
    } finally {
        loading.value = false;
    }
};

const handleSearch = (query: string, tags: string[] = []) => {
    searchQuery.value = query;
    searchTags.value = tags;
    loadMemes(true);
};

const handleTagClick = (tag: string) => {
    selectedTag.value = tag;
    loadMemes(true);
};

const handleTagClear = () => {
    selectedTag.value = "";
    loadMemes(true);
};

const loadMore = () => {
    if (!loading.value && hasMore.value) {
        loadMemes(false);
    }
};

const retry = () => {
    error.value = "";
    loadMemes(true);
};

// 监听搜索查询变化
watch(searchQuery, (newQuery) => {
    if (!newQuery && searchTags.value.length === 0 && !selectedTag.value) {
        loadMemes(true);
    }
});

onMounted(() => {
    loadMemes(true);
});
</script>

<style scoped>
#app {
    height: 100vh;
    background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
    color: #ffffff;
    overflow: hidden;
}

.app-content {
    height: 100%;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #666 #2a2a2a;
}

.app-content::-webkit-scrollbar {
    width: 8px;
}

.app-content::-webkit-scrollbar-track {
    background: #2a2a2a;
}

.app-content::-webkit-scrollbar-thumb {
    background: #666;
    border-radius: 4px;
}

.app-content::-webkit-scrollbar-thumb:hover {
    background: #888;
}

.app-header {
    text-align: center;
    padding: 40px 20px 20px;
    background: rgba(26, 26, 26, 0.8);
}

.app-title {
    font-size: 36px;
    font-weight: 700;
    margin: 0 0 8px 0;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
}

.title-icon {
    font-size: 32px;
    animation: bounce 2s infinite;
}

@keyframes bounce {
    0%, 20%, 50%, 80%, 100% {
        transform: translateY(0);
    }
    40% {
        transform: translateY(-10px);
    }
    60% {
        transform: translateY(-5px);
    }
}

.app-subtitle {
    color: #cccccc;
    font-size: 16px;
    margin: 0;
    font-weight: 300;
}

.main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    flex: 1;
    padding: 80px 20px;
    text-align: center;
}

.empty-icon {
    font-size: 64px;
    margin-bottom: 24px;
    opacity: 0.6;
}

.empty-title {
    font-size: 24px;
    font-weight: 600;
    margin: 0 0 12px 0;
    color: #ffffff;
}

.empty-description {
    font-size: 16px;
    color: #cccccc;
    margin: 0;
    max-width: 400px;
    line-height: 1.5;
}

@media (max-width: 768px) {
    .app-header {
        padding: 24px 16px 16px;
    }

    .app-title {
        font-size: 28px;
    }

    .app-subtitle {
        font-size: 14px;
    }

    .empty-state {
        padding: 60px 16px;
    }

    .empty-icon {
        font-size: 48px;
    }

    .empty-title {
        font-size: 20px;
    }

    .empty-description {
        font-size: 14px;
    }
}
</style>
