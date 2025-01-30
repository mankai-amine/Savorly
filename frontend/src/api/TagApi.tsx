export interface Tag {
  id: number;
  title: string;
  description: string;
  integrients: string;
  embedding: number[]; // 768 dimensions
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/tags`;

export const fetchAllTags = async (page: number, size: number): Promise<PaginatedResponse<Tag>> => {
  const response = await fetch(`${API_BASE_URL}/all?page=${page}&size=${size}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error("Failed to fetch tags: " + response.statusText);
  }

  const data: PaginatedResponse<Tag> = await response.json();
  console.log(data);
  if (!data.content) {
    throw new Error("Invalid response structure: missing content");
  }

  return data;
};

