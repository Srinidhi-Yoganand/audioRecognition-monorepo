import React, { useState } from 'react';
import axios from 'axios';
import './UploadSong.css'; 

const UploadSong = () => {
  const [file, setFile] = useState(null);
  const [songName, setSongName] = useState('');
  const [artistName, setArtistName] = useState('');
  const [uploading, setUploading] = useState(false);
  const [result, setResult] = useState('');

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleSongNameChange = (event) => {
    setSongName(event.target.value);
  };

  const handleArtistNameChange = (event) => {
    setArtistName(event.target.value);
  };

  const handleUpload = async () => {
    if (!file || !songName || !artistName) {
      setResult('Please fill all fields and select a file.');
      return;
    }

    setUploading(true);
    const formData = new FormData();
    formData.append('file', file);
    formData.append('songName', songName);
    formData.append('artistName', artistName);

    try {
      const response = await axios.post('http://localhost:8000/api/audio/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      setResult(response.data);
    } catch (err) {
      setResult('Error: ' + err.message);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="upload-container">
      <div className="upload-form">
        <h2 className="title">🎶 Upload Song</h2>

        <div className="input-group">
          <label className="label">Song Name:</label>
          <input
            type="text"
            value={songName}
            onChange={handleSongNameChange}
            placeholder="Enter song name"
            className="input"
          />
        </div>

        <div className="input-group">
          <label className="label">Artist Name:</label>
          <input
            type="text"
            value={artistName}
            onChange={handleArtistNameChange}
            placeholder="Enter artist name"
            className="input"
          />
        </div>

        <div className="input-group">
          <label className="label">Select a song to upload:</label>
          <input
            type="file"
            onChange={handleFileChange}
            accept="audio/*"
            className="input"
          />
        </div>

        <button
          onClick={handleUpload}
          disabled={uploading}
          className="upload-btn"
        >
          {uploading ? 'Uploading...' : 'Upload Song'}
        </button>

        {result && <p className="result">{result}</p>}
      </div>
    </div>
  );
};

export default UploadSong;
