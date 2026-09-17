"use client";

import { useEffect, useState } from "react";

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8081";

export default function Home() {
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    return () => {
      if (preview) URL.revokeObjectURL(preview);
    };
  }, [preview]);

  function handleImage(event) {
    const selected = event.target.files?.[0];
    if (!selected) return;
    setFile(selected);
    setPreview(URL.createObjectURL(selected));
    setResult(null);
    setError("");
  }

  async function identifyItem() {
    if (!file) return;
    setLoading(true);
    setResult(null);
    setError("");

    const formData = new FormData();
    formData.append("image", file);

    try {
      const response = await fetch(`${API_URL}/api/identify`, {
        method: "POST",
        body: formData
      });

      if (!response.ok) throw new Error("The image could not be analyzed.");
      setResult(await response.json());
    } catch (requestError) {
      setError(`${requestError.message} Make sure the Java backend is running.`);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="page">
      <section className="card">
        <h1>♻️ Recycling Scanner</h1>
        <p className="subtitle">Take a picture to learn how to dispose of an item.</p>

        <label className="picker">
          <strong>Take or choose a photo</strong>
          <input type="file" accept="image/*" capture="environment" onChange={handleImage} />
        </label>

        {preview && <img className="preview" src={preview} alt="Item selected for scanning" />}

        {file && (
          <button className="scan" onClick={identifyItem} disabled={loading}>
            {loading ? "Identifying…" : "Identify Item"}
          </button>
        )}

        {error && <p className="error">{error}</p>}

        {result && (
          <div className="result">
            <h2>{result.name}</h2>
            <p><strong>Category:</strong> {result.category}</p>
            <p><strong>Preparation:</strong> {result.instructions}</p>
            <p><small>Demo confidence: {Math.round(result.confidence * 100)}%</small></p>
          </div>
        )}
      </section>
    </main>
  );
}
