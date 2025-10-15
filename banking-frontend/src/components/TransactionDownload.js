// src/components/TransactionDownload.js
import React, { useState } from 'react';
import axios from 'axios';

const TransactionDownload = () => {
    const [message, setMessage] = useState('');

    const handleDownload = async () => {
        try {
            const res = await axios.get('http://localhost:8081/transactions/export/excel', {
                responseType: 'blob', // Important: tells Axios to expect a binary file
            });

            // Create a URL for the file to be downloaded
            const fileURL = window.URL.createObjectURL(new Blob([res.data]));
            const fileLink = document.createElement('a');

            fileLink.href = fileURL;
            fileLink.setAttribute('download', 'transaction_history.xlsx');
            document.body.appendChild(fileLink);
            fileLink.click();

            // Clean up
            document.body.removeChild(fileLink);
            window.URL.revokeObjectURL(fileURL);

            setMessage(' Download successful!');
        } catch (err) {
            setMessage(' Download failed');
            console.error('Download error:', err);
        }
    };

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', marginBottom: '20px' }}>
            <h2>Download Transaction History</h2>
            <button onClick={handleDownload}>
                Download Excel Sheet
            </button>
            <p>{message}</p>
        </div>
    );
};

export default TransactionDownload;