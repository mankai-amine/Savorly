import { useEffect, useState } from "react";
import { fetchAllRecipes, Recipe,Tag, PaginatedResponse } from "../api/RecipeApi";

const RecipeList = () => {
  const [recipesData, setRecipesData] = useState<PaginatedResponse<Recipe>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 10,
    number: 0
  });
  
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);

  useEffect(() => {
    const loadRecipes = async () => {
      try {
        const data = await fetchAllRecipes(page, recipesData.size);

        setRecipesData(data);
      } catch (err) {
        setError("Failed to fetch recipess " + err);
      } finally {
        setLoading(false);
      }
    };

    loadRecipes();
  }, [page]);

  if (loading) return <p>Loading recipes...</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;

  const startIndex = page * recipesData.size + 1;
  const endIndex = Math.min(startIndex + recipesData.size - 1, recipesData.totalElements);

  return (
    <div>
      <h2>All Recipess</h2>
      <p>Showing {startIndex} - {endIndex} of {recipesData.totalElements} recipes</p>
      <ul>
        {recipesData.content.map((recipe: Recipe) => ( // specify `recipe` type explicitly
          <li key={recipe.id}>
            <strong>{recipe.name}</strong>: {recipe.instructions}
          </li>
        ))}
      </ul>
      <button disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
      <button disabled={page + 1 >= recipesData.totalPages} onClick={() => setPage(page + 1)}>Next</button>
    </div>
  );
};

export default RecipeList;
