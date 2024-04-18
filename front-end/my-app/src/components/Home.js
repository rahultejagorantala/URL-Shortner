import 'bootstrap/dist/css/bootstrap.css';
import React, { useCallback, useEffect, useState } from 'react';
import validateUrl from '../utils/isValidUrl'; 
import './home.css';
import { initializeFloatingStars } from './stars'; 

const Home = () => {
  // State variables
  const [longUrl, setLongUrl] = useState('');
  const [shortUrl, setShortUrl] = useState(''); 
  const [error, setError] = useState(''); 
  const [showNotification, setShowNotification] = useState(false); 
  const [loading, setLoading] = useState(false);
  const SERVER_URL = "http://localhost:8080"; 

  // Function to redirect to original URL
  const redirectToOriginalUrl = useCallback(async (shortUrl) => {
    try {
      const response = await fetch(`${SERVER_URL}/${shortUrl}`, {
        method: 'GET',
      });

      if (response.status === 404) {
        console.error('Short URL not found');
        return;
      }

      if (!response.ok) {
        throw new Error('Failed to retrieve the original URL');
      }

      const originalUrl = await response.text();
      console.log('---> ', originalUrl);

      if (!originalUrl.startsWith('http://') && !originalUrl.startsWith('https://')) {
        const fullUrl = 'http://' + originalUrl;
        window.location.replace(fullUrl);
      } else {
        window.location.replace(originalUrl);
      }

    } catch (error) {
      console.error('Error:', error);
      // Handle the error as needed
    }
  }, [SERVER_URL]);

  // Effect to handle redirection and initialize stars animation
  useEffect(() => {
    const pathname = window.location.pathname;
    if (pathname.length > 1) {
      const shortUrl = pathname.substring(1);
      redirectToOriginalUrl(shortUrl);
    }
    initializeFloatingStars();
  }, [redirectToOriginalUrl]);

  // Function to handle form submission
  const handleSubmit = async () => {
    if (!validateUrl(longUrl)) {
      setError('Invalid URL');
      setShortUrl('');
      return;
    }

    setLoading(true);

    const cleanedUrl = removeProtocolAndTrailingSlash(longUrl);

    try {
      const generatedShortUrl = await generateShortUrl(cleanedUrl);
      setShortUrl(generatedShortUrl);
      setShowNotification(false);
      setError('');
    } catch (error) {
      console.error('Error generating short URL:', error);
      setError('Failed to generate short URL');
    } finally {
      setLoading(false);
    }
  };

  // Function to generate short URL
  const generateShortUrl = async (cleanedUrl) => {
    try {
      const response = await fetch(`${SERVER_URL}/gen`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ cleanedUrl }),
      });

      if (!response.ok) {
        throw new Error('Failed to generate short URL');
      }

      const data = await response.text();
      return data;
    } catch (error) {
      console.error('Error:', error);
      return '';
    }
  };

  // Function to remove protocol and trailing slash from URL
  const removeProtocolAndTrailingSlash = (url) => {
    let cleanedUrl = url.trim();

    if (cleanedUrl.startsWith('http://')) {
      cleanedUrl = cleanedUrl.slice(7);
    } else if (cleanedUrl.startsWith('https://')) {
      cleanedUrl = cleanedUrl.slice(8);
    }

    if (cleanedUrl.endsWith('/')) {
      cleanedUrl = cleanedUrl.slice(0, -1);
    }

    return cleanedUrl;
  };

  // Function to handle copy button click
  const handleCopyClick = () => {
    navigator.clipboard.writeText(shortUrl).then(() => {
      setShowNotification(true);
      setTimeout(() => {
        setShowNotification(false);
      }, 2000);
    }).catch((error) => {
      console.error('Error copying to clipboard:', error);
    });
  };

  // JSX for rendering the component
  return (
    <div className="container d-flex flex-column justify-content-center align-items-center vh-100">

      <div className="floating-stars"></div> {/* Div for floating stars animation */}

      <h2 className="text_color">URL Shortener</h2> {/* Heading */}

      {/* Form for entering long URL */}
      <div className="input-group">
        <input
          type="text"
          className="form-control"
          placeholder="Enter long URL"
          value={longUrl}
          onChange={(e) => setLongUrl(e.target.value)}
        />
        <button className="btn btn-primary btn-submit" onClick={handleSubmit}>
          Submit
        </button>
      </div>

      {/* Error message display */}
      {error && <p className="text-danger">{error}</p>}

      {/* Loading indicator */}
      {loading ? (
        <div className='d-flex flex-column align-items-center'>
          <p className='text_color'>Loading...</p>
        </div>
      ) : (
        // Short URL display and copy button
        shortUrl && (
          <div className='d-flex flex-column align-items-center'>
            <p className='text_color'>Short URL: {shortUrl}</p>
            <button className='btn btn-success' onClick={handleCopyClick}>
              Copy
            </button>
          </div>
        )
      )}

      {/* Copy notification */}
      {showNotification && (
        <div className="notification">
          <div className="alert alert-success" role="alert">
            Copied to clipboard!
          </div>
        </div>
      )}

    </div>
  );
};

export default Home;
