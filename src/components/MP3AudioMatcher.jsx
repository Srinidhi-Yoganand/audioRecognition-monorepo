import React, { useState } from 'react';
import axios from 'axios';
import './AudioMatcher.css'; 

const MP3AudioMatcher = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [result, setResult] = useState('');

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleFileSubmit = async () => {
    if (!selectedFile) {
      setResult('Please select an MP3 file to upload.');
      return;
    }

    const formData = new FormData();
    formData.append('file', selectedFile);

    try {
      const res = await axios.post('http://localhost:8000/api/audio/match/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      const ignore= await axios.post('http://localhost:8000/api/audio/match', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      setResult(res.data);
    } catch (err) {
      setResult('Error: ' + err.message);
    }
  };

  return (
    <div className="audio-matcher-container">
      <div className="audio-matcher-form">
        <h2 className="title">🎵 Upload MP3 and Match</h2>
        
        <input
          type="file"
          accept="audio/mp3"
          onChange={handleFileChange}
          className="file-input"
        />
        
        <button
          onClick={handleFileSubmit}
          className="submit-btn"
        >
          Submit MP3
        </button>
        
        {result && <p className="result-text">{result}</p>}
      </div>
    </div>
  );
};

export default MP3AudioMatcher;
