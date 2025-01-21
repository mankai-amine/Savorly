import React from "react";
import RecipeList from "../components/RecipeList";

const Home: React.FC = () => {
  return (
    <div>
      <h1>Recipe Management</h1>
      <RecipeList />
    </div>
  );
};

export default Home;