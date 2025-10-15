import React, { useState, useEffect } from 'react';
import axios from 'axios';

const TransactionDashboard = () => {
    const [transactions, setTransactions] = useState([]);
    const [message, setMessage] = useState('');
    const [depositForm, setDepositForm] = useState({
        customerId: '',
        transactionAmount: '',
        transactionType: 'DEPOSIT',
    });
    const [transferForm, setTransferForm] = useState({
        senderId: '',
        receiverId: '',
        transactionAmount: '',
        description: ''
    });

    const fetchTransactions = async () => {
        try {
            const res = await axios.get('http://localhost:8081/transactions');
            setTransactions(res.data.data);
        } catch (err) {
            console.error('Error fetching transactions:', err);
        }
    };

    useEffect(() => {
        fetchTransactions();
    }, []);

    const handleDepositChange = (e) => {
        setDepositForm({ ...depositForm, [e.target.name]: e.target.value });
    };

    const handleTransferChange = (e) => {
        setTransferForm({ ...transferForm, [e.target.name]: e.target.value });
    };

    const handleDepositSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:8081/transactions/depositWithdrawByCustomerId', depositForm);
            setMessage(' Transaction successful!');
            fetchTransactions();
        } catch (err) {
            setMessage(' Transaction failed');
            console.error(err);
        }
    };

    const handleTransferSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:8081/transactions/transferByCustomerId', transferForm);
            setMessage(' Transfer successful!');
            fetchTransactions();
        } catch (err) {
            setMessage(' Transfer failed');
            console.error(err);
        }
    };

    return (
        <div>
            <h1>🏦 Banking Dashboard</h1>
            <div style={{ display: 'flex', gap: '20px', marginBottom: '20px' }}>
                <div style={{ padding: '20px', border: '1px solid #ccc' }}>
                    <h2>Deposit/Withdraw</h2>
                    <form onSubmit={handleDepositSubmit}>
                        <input type="number" name="customerId" placeholder="Customer ID" onChange={handleDepositChange} required />
                        <select name="transactionType" onChange={handleDepositChange} required>
                            <option value="DEPOSIT">Deposit</option>
                            <option value="WITHDRAWAL">Withdrawal</option>
                        </select>
                        <input type="number" name="transactionAmount" placeholder="Amount" onChange={handleDepositChange} required />
                        <button type="submit">Submit</button>
                    </form>
                </div>

                <div style={{ padding: '20px', border: '1px solid #ccc' }}>
                    <h2>Transfer Money</h2>
                    <form onSubmit={handleTransferSubmit}>
                        <input type="number" name="senderId" placeholder="Sender ID" onChange={handleTransferChange} required />
                        <input type="number" name="receiverId" placeholder="Receiver ID" onChange={handleTransferChange} required />
                        <input type="number" name="transactionAmount" placeholder="Amount" onChange={handleTransferChange} required />
                        <input type="text" name="description" placeholder="Description" onChange={handleTransferChange} />
                        <button type="submit">Transfer</button>
                    </form>
                </div>
            </div>
            <p>{message}</p>
            <h3>All Transactions</h3>
            <table border="1" style={{ width: '100%', textAlign: 'left' }}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Account ID</th>
                        <th>Type</th>
                        <th>Amount</th>
                        <th>Balance After</th>
                        <th>Description</th>
                    </tr>
                </thead>
                <tbody>
                    {transactions.map(txn => (
                        <tr key={txn.transactionId}>
                            <td>{txn.transactionId}</td>
                            <td>{txn.accountId}</td>
                            <td>{txn.transactionType}</td>
                            <td>{txn.transactionAmount}</td>
                            <td>{txn.balanceAfterTxn}</td>
                            <td>{txn.description}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default TransactionDashboard;