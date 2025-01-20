export interface Recipe {
  id?: number;
  name: string;
  ingredients?: string;
  instructions?: string;
  picture?: string;
  authorId: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}


const API_BASE_URL = "http://localhost:8080/api/recipes";

export const fetchAllRecipes = async (page: number, size: number): Promise<PaginatedResponse<Recipe>> => {
  const response = await fetch(`${API_BASE_URL}/all?page=${page}&size=${size}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error("Failed to fetch recipes: " + response.statusText);
  }

  const data: PaginatedResponse<Recipe> = await response.json();
  console.log(data);
  if (!data.content) {
    throw new Error("Invalid response structure: missing content");
  }

  return data;
};


//create using the recipe, not recipe view model
export const createRecipe = async (recipe: Recipe) => {
  const response = await fetch(`${API_BASE_URL}/new`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(recipe),
  });

  if (!response.ok) {
    throw new Error("Failed to create recipe: " + response.statusText);
  }

  return response.json();
};
//create in transactional way
export const createRecipeTransactional = async (recipe: Recipe) => {
  const response = await fetch(`${API_BASE_URL}/create`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(recipe),
  });

  if (!response.ok) {
    throw new Error("Failed to create recipe: " + response.statusText);
  }

  return response.json();
};


export const getRecipeById = async (id: number): Promise<Recipe> => {
  const response = await fetch(`${API_BASE_URL}/${id}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error("Failed to fetch recipe: " + response.statusText);
  }

  return response.json();
};

//update using the recipe directly
export const updateRecipe = async (recipe: Recipe) => {
  const response = await fetch(`${API_BASE_URL}/update`, {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(recipe),
  });

  if (!response.ok) {
    throw new Error("Failed to update recipe: " + response.statusText);
  }

  return response.json();
};


//update in transactional way
export const updateRecipeTransactional = async (recipe: Recipe) => {
  const response = await fetch(`${API_BASE_URL}/edit`, {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(recipe),
  });

  if (!response.ok) {
    throw new Error("Failed to update recipe: " + response.statusText);
  }

  return response.json();
};