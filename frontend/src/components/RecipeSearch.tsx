import { useState,useEffect } from "react";
import { Recipe,Tag,searchRecipesWithEmbedding } from "../api/RecipeApi"; 

const RecipeSearch: React.FC = () => {
    const [query, setQuery] = useState<string>('');
    const [recipes, setRecipes] = useState<Recipe[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
  
    const handleSearch = async () => {
      if (!query.trim()) {
        setError('Please enter your keywords');
        return;
      }
  
      setLoading(true);
      setError(null);
  
      try {
        const result = await searchRecipesWithEmbedding(query);
        setRecipes(result);
      } catch (err) {
        setError('Failed to search the reipces:' + err);
      } finally {
        setLoading(false);
      }
    };
  
    return (
      <div>
        <h2>Search For Recipes</h2>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Inpu your keywords"
        />
        <button onClick={handleSearch} disabled={loading}>
          {loading ? 'Search...' : 'Search'}
        </button>
  
        {error && <p style={{ color: 'red' }}>{error}</p>}
  
        <ul>
          {recipes.map((recipe) => (
            <li key={recipe.id}>
              <strong>{recipe.name}</strong>: {recipe.instructions}
            </li>
          ))}
        </ul>
      </div>
    );
  };
  
  export default RecipeSearch;