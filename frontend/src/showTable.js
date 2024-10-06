import React, { useState, useEffect } from 'react';

function ShowTable() {
    const [data, setData] = useState([]);
    const dataUrl = 'http://git2-env.eba-njzpava2.us-east-1.elasticbeanstalk.com/repos/all'; // Replace with your actual URL

    useEffect(() => {
        fetch(dataUrl)
            .then((response) => response.json())
            .then((jsonData) => setData(jsonData))
            .catch((error) => console.error('Error fetching data:', error));
    }, []);

    if (data.length === 0) {
        return <div>Loading...</div>;
    }

    // Assuming data is an array of objects
    const tableHeaders = Object.keys(data[0]);

    return (
        <table border="1" cellPadding="5" cellSpacing="0">
            <thead>
                <tr>
                    {tableHeaders.map((key) => (
                        <th key={key}>{key.replace(/_/g, ' ').toUpperCase()}</th>
                    ))}
                </tr>
            </thead>
            <tbody>
                {data.map((item, rowIndex) => (
                    <tr key={rowIndex}>
                        {tableHeaders.map((key) => (
                            <td key={`${rowIndex}-${key}`}>{item[key]}</td>
                        ))}
                    </tr>
                ))}
            </tbody>
        </table>
    );
}

export default ShowTable;