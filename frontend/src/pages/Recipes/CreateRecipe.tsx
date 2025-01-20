import { useState } from "react";
import { createRecipeTransactional, Recipe } from "../../api/RecipeApi";
import UploadPicture from "../../components/UploadPicture";

const CreateRecipe = () => {
  const [recipe, setRecipe] = useState<Recipe>({
    name: "",
    ingredients: "",
    instructions: "",
    picture: "",
    authorId: 1, // default
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // handle input change
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setRecipe((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  // handle picture upload success, update recipe picture field
  const handleUploadSuccess = (imgUrl: string) => {
    setRecipe((prev) => ({
      ...prev,
      picture: imgUrl, // update picture field
    }));
  };

  // handle form submit
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const response = await createRecipeTransactional(recipe);
      console.log(response);

      if (response.message !== "success") { 
        throw new Error(`Failed to create recipe, HTTP status: ${response.message}`);
      }

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
          <textarea name="ingredients" value={recipe.ingredients} onChange={handleChange} />
        </div>
        <div>
          <label>Instructions:</label>
          <textarea name="instructions" value={recipe.instructions} onChange={handleChange} />
        </div>
        <div>
          <label>Picture URL:</label>
          <input type="text" name="picture" value={recipe.picture} onChange={handleChange} />
        </div>
        <div>
          {/* pass as call back*/}
          <UploadPicture onUploadSuccess={handleUploadSuccess} />
        </div>
        <button type="submit" disabled={loading}>
          {loading ? "Submitting..." : "Create Recipe"}
        </button>
      </form>
    </div>
  );
};

export default CreateRecipe;
