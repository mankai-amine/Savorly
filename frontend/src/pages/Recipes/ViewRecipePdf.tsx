import RecipePdfViewer from "../../components/RecipePdfViewer";
import { useParams } from "react-router-dom";


const VieRecipePdf = () => {

    const { recipeId } = useParams<{ recipeId: string }>();
  return (
    <div>
      <h1>Recipe Details</h1>     
      <RecipePdfViewer recipeId={Number(recipeId)} />
    </div>
  );
};

export default VieRecipePdf;
