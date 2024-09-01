import React, { useState } from 'react';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [tbd, setTbd] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();
        if (username === 'csca5028' && password === 'password') {
            setTbd('To be Implemented');
        } else {
            setTbd('Invalid credentials');
        }
    };

    return (<>
        {!tbd &&
            <div style={{ maxWidth: '300px', margin: '0 auto', padding: '1rem', border: '1px solid #ccc', borderRadius: '4px' }}>
                <h2>Login</h2>
                <form onSubmit={handleSubmit}>
                    <div style={{ marginBottom: '1rem' }}>
                        <label htmlFor="username">Username:</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }}
                            required
                        />
                    </div>
                    <div style={{ marginBottom: '1rem' }}>
                        <label htmlFor="password">Password:</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            style={{ width: '100%', padding: '0.5rem', marginTop: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }}
                            required
                        />
                    </div>
                    <button type="submit" style={{ padding: '0.5rem 1rem', borderRadius: '4px', backgroundColor: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}>
                        Login
                    </button>
                </form>
            </div>}
        <div>
            {tbd}
        </div>
    </>
    );
};

export default Login;
