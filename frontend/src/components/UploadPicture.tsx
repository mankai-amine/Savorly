import { useState } from "react";

interface UploadPictureProps {
    onUploadSuccess: (url: string) => void;
  }

const UploadPicture = ({ onUploadSuccess }: UploadPictureProps) => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [imageUrl, setImageUrl] = useState<string | null>(null);

  // 处理文件选择
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files.length > 0) {
      setFile(event.target.files[0]);
    }
  };

  // 处理文件上传
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
      const response = await fetch("http://localhost:8080/pic-upload", {
        method: "POST",
        body: formData,
        headers: {
            "Authorization": `Bearer ${sessionStorage.getItem("token")}`,
          },
      });

      const data = await response.json();
      if (!response.ok || !data.imgUrl) {
        throw new Error(data.msg || "File upload failed");
      }

      setImageUrl(data.imgUrl); // 服务器返回的 S3 文件名
      onUploadSuccess(data.imgUrl);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div>
      <h2>Upload a Picture</h2>
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
