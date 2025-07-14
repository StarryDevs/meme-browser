import axios from "axios";
import type {MemeResponse, SearchParams} from "../types/meme";

const API_BASE_URL = location.origin; // API 地址

const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
});

export const memeApi = {
    // 获取meme列表
    async getMemes(params: SearchParams = {}): Promise<MemeResponse> {
        const response = await api.get("/memes", {params});
        return response.data;
    },

    // 搜索memes（支持关键词和标签同时搜索）
    async searchMemes(params: SearchParams = {}): Promise<MemeResponse> {
        const response = await api.get("/memes/search", {params});
        return response.data;
    },

};
