import { useState } from "react";
import { createRecipe, Recipe } from "../../api/RecipeApi";

const CreateRecipe = () => {
  const [recipe, setRecipe] = useState<Recipe>({
    name: "",
    ingredients: "",
    instructions: "",
    picture: "",
    authorId: 1, // 默认值，可更改
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setRecipe({
      ...recipe,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const response = await createRecipe(recipe);
      console.log(response);
      if (response.message !== "success") { 
        throw new Error(`Failed to create recipe, HTTP status: ${response.message}`);
      }
    
      const data = await response.recipes;
      console.log(data[0]);    
      
    
      setSuccessMessage("Recipe created successfully!");
      setRecipe({ name: "", ingredients: "", instructions: "", picture: "", authorId: 1 });
    
    } catch (err) {
      setError("Failed to create recipe: " + err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Create a New Recipe</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {successMessage && <p style={{ color: "green" }}>{successMessage}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Name:</label>
          <input type="text" name="name" value={recipe.name} onChange={handleChange} required />
        </div>
        <div>
          <label>Ingredients:</label>
          <textarea name="ingredients" value={recipe.ingredients} onChange={handleChange}/>
        </div>
        <div>
          <label>Instructions:</label>
          <textarea name="instructions" value={recipe.instructions} onChange={handleChange}/>
        </div>
        <div>
          <label>Picture URL:</label>
          <input type="text" name="picture" value={recipe.picture} onChange={handleChange} />
        </div>
        <button type="submit" disabled={loading}>
          {loading ? "Submitting..." : "Create Recipe"}
        </button>
      </form>
    </div>
  );
};

export default CreateRecipe;

