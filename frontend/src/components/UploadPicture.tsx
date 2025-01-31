import { useState } from "react";

const API_UPLOAD_URL = new URL('pic-upload', import.meta.env.VITE_UPLOAD_URL).toString();

interface UploadPictureProps {
    onUploadSuccess: (url: string) => void;
  }

const UploadPicture = ({ onUploadSuccess }: UploadPictureProps) => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [imageUrl, setImageUrl] = useState<string | null>(null);

  // Handle file selection
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files.length > 0) {
      setFile(event.target.files[0]);
    }
  };

  // Handle file uploads
  const handleUpload = async () => {
    if (!file) {
      setError("Please select a file first.");
      return;
    }

    setUploading(true);
    setError(null);

    const formData = new FormData();
    formData.append("imgUrl", file);

    try {
      const response = await fetch(`${API_UPLOAD_URL}`, {
        method: "POST",
        body: formData,
        headers: {
            "Authorization": `Bearer ${sessionStorage.getItem("accessToken")}`,
          },
      });

      const data = await response.json();
      if (!response.ok || !data.imgUrl) {
        throw new Error(data.msg || "File upload failed");
      }

      setImageUrl(data.imgUrl); // S3 file name returned by the server
      onUploadSuccess(data.imgUrl);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div>
      <p>Upload a Picture</p>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {imageUrl && (
        <div>
          <p>File uploaded successfully!</p>
          <p>File Name: <strong>{imageUrl}</strong></p>
        </div>
      )}
      <input type="file" accept="image/*" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={uploading}>
        {uploading ? "Uploading..." : "Upload"}
      </button>
    </div>
  );
};

export default UploadPicture;
