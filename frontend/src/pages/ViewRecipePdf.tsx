import RecipePdfViewer from "../components/RecipePdfViewer";
import { useParams } from "react-router-dom";


const ViewRecipePdf = () => {

    const { id } = useParams<{ id: string }>();
  return (
    <div>
      <h1>Recipe PDF Viewer</h1>     
      <RecipePdfViewer recipeId={Number(id)} />
    </div>
  );
};

export default ViewRecipePdf;