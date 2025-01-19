import { useEffect, useState } from "react";
import { fetchAllTags, Tag, PaginatedResponse } from "../api/TagApi";

const TagList = () => {
  const [tagsData, setTagsData] = useState<PaginatedResponse<Tag>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 10,
    number: 0
  });
  
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);

  useEffect(() => {
    const loadTags = async () => {
      try {
        const data = await fetchAllTags(page, tagsData.size);

        setTagsData(data);
      } catch (err) {
        setError("Failed to fetch tags " + err);
      } finally {
        setLoading(false);
      }
    };

    loadTags();
  }, [page]);

  if (loading) return <p>Loading tags...</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;

  const startIndex = page * tagsData.size + 1;
  const endIndex = Math.min(startIndex + tagsData.size - 1, tagsData.totalElements);

  return (
    <div>
      <h2>All Tags</h2>
      <p>Showing {startIndex} - {endIndex} of {tagsData.totalElements} tags</p>
      <ul>
        {tagsData.content.map((tag: Tag) => ( // specify `tag` type explicitly
          <li key={tag.id}>
            <strong>{tag.title}</strong>: {tag.description}
          </li>
        ))}
      </ul>
      <button disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
      <button disabled={page + 1 >= tagsData.totalPages} onClick={() => setPage(page + 1)}>Next</button>
    </div>
  );
};

export default TagList;
