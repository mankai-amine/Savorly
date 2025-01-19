import { useEffect, useState } from "react";
import RecipeForm from "../../components/RecipeForm";
import { updateRecipe, getRecipeById, Recipe } from "../../api/RecipeApi";
import { useParams } from "react-router-dom"; // 假设是基于 URL 传递 ID

const EditRecipe = () => {
  const { id } = useParams<{ id: string }>();
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchRecipe = async () => {
      const data = await getRecipeById(Number(id));
      setRecipe(data);
    };
    fetchRecipe();
  }, [id]);

  const handleSubmit = async (recipeViewModel: Recipe) => {
    setSuccessMessage(null);
    setError(null);
    setLoading(true);
    try {      
      const response = await updateRecipe(recipeViewModel);
      if(response.message !== "success") {
        throw new Error(`Failed to update recipe, HTTP status: ${response.message}`);
      }
      console.log(response);
      setRecipe(response.recipes[0]);
      setSuccessMessage("Recipe updated successfully!");
    } catch (err) {
      setError("Failed to create recipe: " + err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Edit Recipe</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {successMessage && <p style={{ color: "green" }}>{successMessage}</p>}
      {recipe ? <RecipeForm initialRecipe={recipe} onSubmit={handleSubmit} /> : <p>Loading...</p>}
      {loading ? "Update..." : ""}
    </div>
  );
};

export default EditRecipe;
