import React from 'react';
import { Link } from 'react-router-dom'; // Import Link component from react-router-dom
import './Navbar.css'; // Add styling for the navbar if needed

const Navbar = () => {
  return (
    <nav className="navbar">
      <div className="navbar-left">
        <h1 className="app-name">No More Earworms</h1>
      </div>
      <div className="navbar-right">
        <ul>
          <li>
            <Link to="/audio-matcher" className="navbar-link">
              Audio Matcher
            </Link>
          </li>
          <li>
            <Link to="/upload-song" className="navbar-link">
              Upload Song
            </Link>
          </li>
          <li>
            <Link to="/mp3-upload-matcher" className="navbar-link">
              MP3 Upload Matcher
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
