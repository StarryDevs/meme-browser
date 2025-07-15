<template>
    <div class="virtual-list-container">
        <div
            ref="scrollElement"
            class="virtual-scroll-container"
            @scroll="handleScroll"
        >
            <div class="meme-grid">
                <div
                    v-for="item in visibleItems"
                    :key="item.id"
                    class="meme-grid-item"
                >
                    <MemeCard
                        :meme="item"
                        @tag-click="$emit('tag-click', $event)"
                        @height-change="handleCardHeightChange"
                    />
                </div>
            </div>

            <!-- 加载更多触发器 -->
            <div
                v-if="hasMore"
                ref="loadMoreTrigger"
                class="load-more-trigger"
            ></div>
        </div>

        <div v-if="loading" class="loading-container">
            <div class="loading-spinner"></div>
            <p class="loading-text">加载中...</p>
        </div>

        <div v-if="error" class="error-container">
            <p class="error-text">{{ error }}</p>
            <button class="retry-button" @click="$emit('retry')">重试</button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import {computed, nextTick, onMounted, onUnmounted, ref, watch} from "vue";
import MemeCard from "./MemeCard.vue";
import type {Meme} from "../types/meme";

interface Props {
    items: Meme[];
    loading?: boolean;
    error?: string;
    columnsDesktop?: number;
    columnsMobile?: number;
    hasMore: boolean;
}

interface Emits {
    "tag-click": [tag: string];
    "load-more": [];
    "retry": [];
}

const props = withDefaults(defineProps<Props>(), {
    columnsDesktop: 2,
    columnsMobile: 1,
});

const emit = defineEmits<Emits>();

const scrollElement = ref<HTMLElement>();
const loadMoreTrigger = ref<HTMLElement>();
const windowWidth = ref(window.innerWidth);
const visibleCount = ref(20); // 初始显示数量
const loadMoreObserver = ref<IntersectionObserver>();

// 当前可见的 items
const visibleItems = computed(() => {
    return props.items.slice(0, visibleCount.value);
});

const handleScroll = () => {
    // 滚动处理逻辑（如果需要的话）
};

const handleCardHeightChange = () => {
    // 卡片高度变化时的处理
    nextTick(() => {
        // 可以在这里添加任何需要的重新计算逻辑
    });
};

const loadMore = () => {
    if (!props.loading && props.hasMore) {
        visibleCount.value += 20;
        emit("load-more");
    }
};

const setupIntersectionObserver = () => {
    if (loadMoreTrigger.value) {
        loadMoreObserver.value = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting) {
                    loadMore();
                }
            },
            {
                rootMargin: "100px",
            }
        );

        loadMoreObserver.value.observe(loadMoreTrigger.value);
    }
};

const updateWindowWidth = () => {
    windowWidth.value = window.innerWidth;
};

// 监听 items 变化，重置可见数量
watch(() => props.items.length, (newLength) => {
    visibleCount.value = newLength;
});

onMounted(() => {
    window.addEventListener("resize", updateWindowWidth);
    nextTick(() => {
        setupIntersectionObserver();
    });
});

onUnmounted(() => {
    window.removeEventListener("resize", updateWindowWidth);
    if (loadMoreObserver.value) {
        loadMoreObserver.value.disconnect();
    }
});
</script>

<style scoped>
.virtual-list-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
}

.virtual-scroll-container {
    flex: 1;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #666 #2a2a2a;
    padding: 0 20px;
}

.virtual-scroll-container::-webkit-scrollbar {
    width: 8px;
}

.virtual-scroll-container::-webkit-scrollbar-track {
    background: #2a2a2a;
}

.virtual-scroll-container::-webkit-scrollbar-thumb {
    background: #666;
    border-radius: 4px;
}

.virtual-scroll-container::-webkit-scrollbar-thumb:hover {
    background: #888;
}

.meme-grid {
    display: grid;
    /*noinspection CssUnresolvedCustomProperty*/
    grid-template-columns: repeat(var(--columns), 1fr);
    gap: 20px;
    padding-bottom: 20px;
}

.meme-grid-item {
    display: flex;
    flex-direction: column;
    min-height: 0;
}

.load-more-trigger {
    height: 20px;
    width: 100%;
}

.loading-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #333;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 16px;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}

.loading-text {
    color: #cccccc;
    font-size: 16px;
    margin: 0;
}

.error-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
}

.error-text {
    color: #ff6b6b;
    font-size: 16px;
    margin: 0 0 16px 0;
    text-align: center;
}

.retry-button {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 8px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.retry-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
    .virtual-scroll-container {
        padding: 0 16px;
    }

    .meme-grid {
        gap: 16px;
        padding-bottom: 16px;
    }

    .loading-container,
    .error-container {
        padding: 24px;
    }

    .loading-spinner {
        width: 32px;
        height: 32px;
    }
}
</style>
