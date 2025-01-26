import { useState,useEffect } from "react";
import { Recipe, Tag } from "../api/RecipeApi"; 

interface RecipeFormProps {
  initialRecipe:  Recipe; 
  onSubmit: (recipe: Recipe) => void;
}

const RecipeForm = ({ initialRecipe, onSubmit }: RecipeFormProps) => {
  // handle `Recipe` and `RecipeViewModel` types
  const getInitialRecipe = (recipe: Recipe): Recipe => {
    if (!recipe.id) {
      return { id:0 ,name: "", ingredients: "", instructions: "", picture: "", authorId: 1 };
    }
    return {
        id: recipe.id,
      name: recipe.name,
      ingredients: recipe.ingredients,
      instructions: recipe.instructions,
      picture: "picture" in recipe ? recipe.picture : "", // `Recipe` may not picture
      authorId: "authorId" in recipe ? recipe.authorId : 1, // `Recipe` may not authorId
    };
  };

  const [recipe, setRecipe] = useState<Recipe>(getInitialRecipe(initialRecipe));

  // listen for changes in `initialRecipe`
  useEffect(() => {
    setRecipe(initialRecipe);
  }, [initialRecipe]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setRecipe({ ...recipe, [e.target.name]: e.target.value });
  };

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        onSubmit(recipe);
      }}
    >
      <div>
        <label>Name:</label>
        <input type="text" name="name" value={recipe.name} onChange={handleChange} required />
      </div>
      <div>
        <label>Ingredients:</label>
        <textarea name="ingredients" value={recipe.ingredients} onChange={handleChange} required />
      </div>
      <div>
        <label>Instructions:</label>
        <textarea name="instructions" value={recipe.instructions} onChange={handleChange} required />
      </div>
      <div>
        <label>Picture URL:</label>
        <input type="text" name="picture" value={recipe.picture} onChange={handleChange} />
      </div>
      <button type="submit">Save</button>
    </form>
  );
};

export default RecipeForm;
