import React from 'react'
import AudioMatcher from './components/AudioMatcher.jsx'
import MP3AudioMatcher from './components/MP3AudioMatcher'; 
import UploadSong from './components/UploadSong.jsx'
import Navbar from './components/Navbar.jsx'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import './App.css'

function App() {
  return (
    <Router>
      <Navbar />
      <div className='main-content'>
        <Routes>
          <Route path="/" element={<AudioMatcher />} />
          <Route path="/audio-matcher" element={<AudioMatcher />} />
          <Route path="/upload-song" element={<UploadSong />} />
          <Route path="/mp3-upload-matcher" element={<MP3AudioMatcher />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
