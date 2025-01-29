import RecipePdfViewer from "../components/RecipePdfViewer";
import { useParams } from "react-router-dom";


const ViewRecipePdf = () => {

    const { id } = useParams<{ id: string }>();
  return (
    <div>
      <RecipePdfViewer recipeId={Number(id)} />
    </div>
  );
};

export default ViewRecipePdf;