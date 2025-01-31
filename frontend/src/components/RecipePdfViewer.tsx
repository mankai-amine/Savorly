import { useEffect, useState } from "react";

const API_BASE_URL = new URL('/api/pdf', import.meta.env.VITE_API_URL).toString();

const RecipePdfViewer = ({ recipeId }: { recipeId: number }) => {
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const accessToken = sessionStorage.getItem("accessToken"); 
    if (!accessToken) {
        alert("Authorization token is missing. Please log in again.");
        return;
    }

  useEffect(() => {
    const fetchPdf = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/${recipeId}`, {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${sessionStorage.getItem("accessToken")}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch PDF");
        }

        const pdfBlob = await response.blob();
        const pdfObjectURL = URL.createObjectURL(pdfBlob);
        setPdfUrl(pdfObjectURL);
      } catch (err) {
        setError("Failed to load PDF");
      } finally {
        setLoading(false);
      }
    };

    fetchPdf();
  }, [recipeId]);

  if (loading) return <p>Loading PDF...</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;

  return (
    <div>
      {pdfUrl && (
        <iframe
          src={pdfUrl}
          width="100%"
          height="700px"
          style={{ border: "none" }}
        />
      )}
    </div>
  );
};

export default RecipePdfViewer;