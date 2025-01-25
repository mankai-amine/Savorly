import { useEffect, useState } from "react";

const API_BASE_URL = "http://localhost:8080/api/pdf"; // 你的后端 API 地址

const RecipePdfViewer = ({ recipeId }: { recipeId: number }) => {
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchPdf = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/${recipeId}`, {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
            "Accept": "application/pdf",
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
      <h2>Recipe PDF Viewer</h2>
      {pdfUrl && (
        <iframe
          src={pdfUrl}
          width="125%"
          height="900px"
          style={{ border: "none" }}
        />
      )}
    </div>
  );
};

export default RecipePdfViewer;
