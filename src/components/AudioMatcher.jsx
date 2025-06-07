import React, { useState, useRef } from 'react';
import axios from 'axios';
import './AudioMatcher.css'; 

const AudioMatcher = () => {
  const [recording, setRecording] = useState(false);
  const [result, setResult] = useState('');
  const mediaRecorderRef = useRef(null);
  const audioChunksRef = useRef([]);
  const timeoutRef = useRef(null);

  const startRecording = async () => {
    try{
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaRecorderRef.current = new MediaRecorder(stream, {
        mimeType: 'audio/webm',
      });

      audioChunksRef.current = [];

      mediaRecorderRef.current.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };

      mediaRecorderRef.current.onstop = async () => {
        const blob = new Blob(audioChunksRef.current, { type: 'audio/webm' });
        const file = new File([blob], 'recording.webm', { type: 'audio/webm' });

        const formData = new FormData();
        formData.append('file', file);

        try {
          const res = await axios.post('http://localhost:8000/api/audio/match', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });
          setResult(res.data);
        } catch (err) {
          setResult('Error: ' + err.message);
        }
      };

      mediaRecorderRef.current.start();
      setRecording(true);

      timeoutRef.current = setTimeout(() => {
        stopRecording();
      }, 10000);
    }catch(err){
      setResult('Microphone access denied or not supported: ' + err.message);
    }
  };

  const stopRecording = () => {
    clearTimeout(timeoutRef.current);
    mediaRecorderRef.current.stop();
    setRecording(false);
  };

  return (
    <div className="audio-matcher-container">
      <div className="audio-matcher-form">
        <h2 className="title">🎤 Record Audio and Match</h2>
        <button
          onClick={recording ? stopRecording : startRecording}
          className={`record-btn ${recording ? 'stop' : 'start'}`}
        >
          {recording ? 'Stop Recording' : 'Start Recording'}
        </button>
        {result && <p className="result-text">{result}</p>}
      </div>
    </div>
  );
};

export default AudioMatcher;
