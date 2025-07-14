export interface Meme {
  id: string;
  title: string;
  imageUrl: string;
  originalText: string;
  recognizedText?: string; // 图片中识别到的文本
  content: string;
  tags: string[];
  createdAt: string;
}

export interface MemeResponse {
  data: Meme[];
  total: number;
  page: number;
  limit: number;
  hasMore: boolean;
}

export interface SearchParams {
  query?: string;
  tags?: string[];
  tag?: string;
  page?: number;
  limit?: number;
}
