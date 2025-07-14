<template>
    <div class="meme-card">
        <div class="meme-image-container">
            <img
                :alt="meme.title"
                :src="meme.imageUrl"
                class="meme-image"
            />
            <div class="image-overlay">
                <div class="original-text">{{ meme.originalText }}</div>
            </div>
        </div>

        <div class="meme-content">
            <h3 class="meme-title">
                <a @click="idTag ? $emit('tag-click', idTag) : () => {}">{{ meme.title }}</a>
            </h3>

            <!-- 图片文本识别区域 -->
            <div v-if="meme.recognizedText" class="recognized-text-section">
                <button
                    :class="{ 'expanded': showRecognizedText }"
                    class="recognized-text-toggle"
                    @click="toggleRecognizedText"
                >
                    <svg class="toggle-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <polyline points="6,9 12,15 18,9"></polyline>
                    </svg>
                    <span class="toggle-label">原始文本</span>
                    <span class="toggle-hint">{{ showRecognizedText ? '收起' : '展开' }}</span>
                </button>

                <div
                    v-show="showRecognizedText"
                    class="recognized-text-content"
                >
                    <div class="recognized-text-box">
                        <div class="recognized-text-header">
                            <svg class="text-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                <polyline points="14,2 14,8 20,8"></polyline>
                                <line x1="16" x2="8" y1="13" y2="13"></line>
                                <line x1="16" x2="8" y1="17" y2="17"></line>
                                <polyline points="10,9 9,9 8,9"></polyline>
                            </svg>
                            <span>图片原始文本</span>
                        </div>
                        <pre class="recognized-text">{{ meme.recognizedText }}</pre>
                    </div>
                </div>
            </div>

            <p class="meme-description">{{ meme.content }}</p>

            <div class="meme-tags">
        <span
            v-for="tag in meme.tags.filter(it => !it.startsWith('$'))"
            :key="tag"
            class="tag"
            @click="$emit('tag-click', tag)"
        >
            {{ tag }}
        </span>
            </div>

            <div class="meme-meta">
                <span class="created-date">{{ formatDate(meme.createdAt) }}</span>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import {computed, ref} from "vue";
import type {Meme} from "../types/meme";

interface Props {
    meme: Meme;
}

const props = defineProps<Props>();

const idTag = computed(() => {
    return props.meme.tags.find(it => it.startsWith("$"));
});

const emit = defineEmits<{
    "tag-click": [tag: string];
    "height-change": [];
}>();

const showRecognizedText = ref(false);

const toggleRecognizedText = () => {
    showRecognizedText.value = !showRecognizedText.value;
    // 通知父组件高度发生变化
    emit("height-change");
};

const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "short",
        day: "numeric"
    });
};
</script>

<style lang="scss" scoped>
.meme-card {
    background: #1a1a1a;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.meme-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 15px rgba(0, 0, 0, 0.4);
}

.meme-image-container {
    position: relative;
    width: 100%;
    height: 300px;
    overflow: hidden;
}

.meme-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.meme-card:hover .meme-image {
    transform: scale(1.05);
}

.image-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
    padding: 20px;
    transform: translateY(100%);
    transition: transform 0.3s ease;
}

.meme-card:hover .image-overlay {
    transform: translateY(0);
}

.original-text {
    color: #ffffff;
    font-size: 14px;
    line-height: 1.4;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.meme-content {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

.meme-title {
    color: #ffffff;
    font-size: 18px;
    font-weight: 600;
    margin: 0 0 12px 0;
    line-height: 1.3;
    transition: color 0.2s ease;

    &:hover {
        color: #667eea;
        cursor: pointer;
    }

}

.recognized-text-section {
    margin-bottom: 16px;
}

.recognized-text-toggle {
    display: flex;
    align-items: center;
    width: 100%;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 10px 12px;
    color: #cccccc;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-bottom: 8px;
}

.recognized-text-toggle:hover {
    background: rgba(255, 255, 255, 0.08);
    border-color: rgba(255, 255, 255, 0.2);
}

.toggle-icon {
    width: 16px;
    height: 16px;
    margin-right: 8px;
    transition: transform 0.3s ease;
    flex-shrink: 0;
}

.recognized-text-toggle.expanded .toggle-icon {
    transform: rotate(180deg);
}

.toggle-label {
    flex: 1;
    text-align: left;
    font-weight: 500;
}

.toggle-hint {
    font-size: 11px;
    color: #888;
    font-weight: 400;
}

.recognized-text-content {
    animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.recognized-text-box {
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 12px;
    margin-bottom: 8px;
}

.recognized-text-header {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    color: #888;
    font-size: 12px;
    font-weight: 500;
}

.text-icon {
    width: 14px;
    height: 14px;
    margin-right: 6px;
    flex-shrink: 0;
}

.recognized-text {
    color: #e0e0e0;
    font-size: 13px;
    line-height: 1.4;
    margin: 0;
    font-family: 'Courier New', monospace;
    white-space: pre-wrap;
    word-wrap: break-word;
    background: rgba(255, 255, 255, 0.02);
    padding: 8px;
    border-radius: 4px;
    border-left: 3px solid #667eea;
}

.meme-description {
    color: #cccccc;
    font-size: 14px;
    line-height: 1.5;
    margin: 0 0 16px 0;
    flex: 1;
}

.meme-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 16px;
}

.tag {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
}

.tag:hover {
    transform: scale(1.1);
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.tag:nth-child(2n) {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.tag:nth-child(3n) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.meme-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #333;
}

.created-date {
    color: #888;
    font-size: 12px;
}

@media (max-width: 768px) {
    .meme-card {
        margin-bottom: 16px;
    }

    .meme-image-container {
        height: 250px;
    }

    .meme-content {
        padding: 16px;
    }

    .meme-title {
        font-size: 16px;
    }

    .meme-description {
        font-size: 13px;
    }

    .recognized-text-toggle {
        padding: 8px 10px;
        font-size: 12px;
    }

    .recognized-text-box {
        padding: 10px;
    }

    .recognized-text {
        font-size: 12px;
        padding: 6px;
    }
}
</style>
