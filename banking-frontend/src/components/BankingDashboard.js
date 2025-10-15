// src/components/BankingDashboard.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const BankingDashboard = () => {
    const [customerForm, setCustomerForm] = useState({
        username: '',
        password: '',
        aadharNumber: '',
        permanentAddress: '',
        state: '',
        country: '',
        city: '',
        email: '',
        phoneNumber: '',
        status: 'ACTIVE',
        dob: '',
        age: '',
        gender: '',
        fatherName: '',
        motherName: '',
    });

    const [accountForm, setAccountForm] = useState({
        customerId: '',
        accountType: '',
        bankName: '',
        branch: '',
        balance: 0,
        status: 'ACTIVE',
        accountNumber: '',
        ifscCode: '',
        nameOnAccount: '',
        phoneLinked: '',
        savingAmount: 0,
    });

    const [transactionForm, setTransactionForm] = useState({
        accountNumber: '',
        transactionAmount: '',
        transactionType: 'DEPOSIT',
        description: '',
        modeOfTransaction: ''
    });

    const [transferForm, setTransferForm] = useState({
        senderAccountNumber: '',
        receiverAccountNumber: '',
        transactionAmount: '',
        description: ''
    });

    const [transactions, setTransactions] = useState([]);
    const [message, setMessage] = useState('');
    const [activeTab, setActiveTab] = useState('customer');

    const fetchTransactions = async () => {
        try {
            const res = await axios.get('http://localhost:8081/transactions');
            if (res.data.data) {
                setTransactions(res.data.data);
            }
        } catch (err) {
            console.error('Error fetching transactions:', err);
        }
    };

    const handleDownload = async () => {
        try {
            const res = await axios.get('http://localhost:8081/transactions/export/excel', {
                responseType: 'blob', // Important: tells Axios to expect a binary file
            });

            const fileURL = window.URL.createObjectURL(new Blob([res.data]));
            const fileLink = document.createElement('a');
            fileLink.href = fileURL;
            fileLink.setAttribute('download', 'transaction_history.xlsx');
            document.body.appendChild(fileLink);
            fileLink.click();
            document.body.removeChild(fileLink);
            window.URL.revokeObjectURL(fileURL);
            setMessage(' Download successful!');
        } catch (err) {
            setMessage(' Download failed');
            console.error('Download error:', err);
        }
    };

    useEffect(() => {
        fetchTransactions();
    }, []);

    const handleCustomerChange = (e) => {
        setCustomerForm({ ...customerForm, [e.target.name]: e.target.value });
    };

    const handleAccountChange = (e) => {
        setAccountForm({ ...accountForm, [e.target.name]: e.target.value });
    };

    const handleTransactionChange = (e) => {
        setTransactionForm({ ...transactionForm, [e.target.name]: e.target.value });
    };

    const handleTransferChange = (e) => {
        setTransferForm({ ...transferForm, [e.target.name]: e.target.value });
    };

    const handleCustomerSubmit = async (e) => {
        e.preventDefault();
        try {
            const dobTimestamp = new Date(customerForm.dob).getTime();
            const payload = { ...customerForm, dob: dobTimestamp };

            const res = await axios.post("http://localhost:8081/customers", payload);
            setMessage(` Customer created with ID: ${res.data.id}`);
        } catch (err) {
            setMessage(` Customer creation failed: ${err.response.data.error}`);
            console.error(err);
        }
    };

    const handleAccountSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post("http://localhost:8081/accounts", accountForm);
            setMessage(` Account created with ID: ${res.data.id}`);
            fetchTransactions();
        } catch (err) {
            setMessage(" Account creation failed");
            console.error(err);
        }
    };

    const handleTransactionSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:8081/transactions/depositWithdrawByAccountNumber', transactionForm);
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
            await axios.post('http://localhost:8081/transactions/transferByAccountNumber', transferForm);
            setMessage(' Transfer successful!');
            fetchTransactions();
        } catch (err) {
            setMessage(' Transfer failed');
            console.error(err);
        }
    };

    return (
        <div className="banking-container">
            <h1 className="main-title">🏦 Bank Simulator</h1>
            <div className="tab-navigation">
                <button
                    onClick={() => setActiveTab('customer')}
                    className={activeTab === 'customer' ? 'active' : ''}>
                    1. Customer Creation
                </button>
                <button
                    onClick={() => setActiveTab('account')}
                    className={activeTab === 'account' ? 'active' : ''}>
                    2. Account Creation
                </button>
                <button
                    onClick={() => setActiveTab('transactions')}
                    className={activeTab === 'transactions' ? 'active' : ''}>
                    3. Transactions
                </button>
            </div>

            <div className="tab-content">
                {activeTab === 'customer' && (
                    <div className="form-container">
                        <div className="form-section">
                            <h2>Create New Customer (Full Details)</h2>
                            <form onSubmit={handleCustomerSubmit} className="form-grid">
                                <input type="text" name="username" placeholder="Username" onChange={handleCustomerChange} required />
                                <input type="password" name="password" placeholder="Password" onChange={handleCustomerChange} required />
                                <input type="text" name="aadharNumber" placeholder="Aadhar Number" onChange={handleCustomerChange} required />
                                <input type="text" name="permanentAddress" placeholder="Permanent Address" onChange={handleCustomerChange} />
                                <input type="text" name="state" placeholder="State" onChange={handleCustomerChange} />
                                <input type="text" name="country" placeholder="Country" onChange={handleCustomerChange} />
                                <input type="text" name="city" placeholder="City" onChange={handleCustomerChange} />
                                <input type="email" name="email" placeholder="Email" onChange={handleCustomerChange} required />
                                <input type="text" name="phoneNumber" placeholder="Phone Number" onChange={handleCustomerChange} />
                                <input type="date" name="dob" placeholder="Date of Birth" onChange={handleCustomerChange} />
                                <input type="number" name="age" placeholder="Age" onChange={handleCustomerChange} />
                                <select name="gender" onChange={handleCustomerChange}>
                                    <option value="">Select Gender</option>
                                    <option value="MALE">Male</option>
                                    <option value="FEMALE">Female</option>
                                    <option value="OTHER">Other</option>
                                </select>
                                <input type="text" name="fatherName" placeholder="Father's Name" onChange={handleCustomerChange} />
                                <input type="text" name="motherName" placeholder="Mother's Name" onChange={handleCustomerChange} />
                                <button type="submit">Create Customer Record</button>
                            </form>
                        </div>
                    </div>
                )}

                {activeTab === 'account' && (
                    <div className="form-container">
                        <div className="form-section">
                            <h2>Create New Account</h2>
                            <form onSubmit={handleAccountSubmit} className="form-grid">
                                <input type="number" name="customerId" placeholder="Customer ID" onChange={handleAccountChange} required />
                                <input type="text" name="accountType" placeholder="Account Type" onChange={handleAccountChange} required />
                                <input type="text" name="bankName" placeholder="Bank Name" onChange={handleAccountChange} />
                                <input type="text" name="branch" placeholder="Branch" onChange={handleAccountChange} />
                                <input type="number" name="balance" placeholder="Initial Balance" onChange={handleAccountChange} />
                                <input type="text" name="status" placeholder="Status" onChange={handleAccountChange} />
                                <input type="text" name="accountNumber" placeholder="Account Number" onChange={handleAccountChange} required />
                                <input type="text" name="ifscCode" placeholder="IFSC Code" onChange={handleAccountChange} />
                                <input type="text" name="nameOnAccount" placeholder="Name on Account" onChange={handleAccountChange} />
                                <input type="text" name="phoneLinked" placeholder="Phone Linked" onChange={handleAccountChange} />
                                <input type="number" name="savingAmount" placeholder="Saving Amount" onChange={handleAccountChange} />
                                <button type="submit">Create Account</button>
                            </form>
                        </div>
                    </div>
                )}

                {activeTab === 'transactions' && (
                    <div className="form-container">
                        <div className="form-section">
                            <h2>Process Transactions</h2>
                            <div style={{ display: 'flex', gap: '20px' }}>
                                <form onSubmit={handleTransactionSubmit} style={{ flex: 1 }}>
                                    <h3>Deposit / Withdraw</h3>
                                    <input type="text" name="accountNumber" placeholder="Account Number" onChange={handleTransactionChange} required />
                                    <select name="transactionType" onChange={handleTransactionChange} required>
                                        <option value="DEPOSIT">Deposit</option>
                                        <option value="WITHDRAWAL">Withdrawal</option>
                                    </select>
                                    <input type="number" name="transactionAmount" placeholder="Amount" onChange={handleTransactionChange} required />
                                    <input type="text" name="description" placeholder="Description" onChange={handleTransactionChange} />
                                    <input type="text" name="modeOfTransaction" placeholder="Mode of Transaction" onChange={handleTransactionChange} />
                                    <button type="submit">Submit</button>
                                </form>

                                <form onSubmit={handleTransferSubmit} style={{ flex: 1 }}>
                                    <h3>Transfer Money</h3>
                                    <input type="text" name="senderAccountNumber" placeholder="Sender Account Number" onChange={handleTransferChange} required />
                                    <input type="text" name="receiverAccountNumber" placeholder="Receiver Account Number" onChange={handleTransferChange} required />
                                    <input type="number" name="transactionAmount" placeholder="Amount" onChange={handleTransferChange} required />
                                    <input type="text" name="description" placeholder="Description" onChange={handleTransferChange} />
                                    <button type="submit">Transfer</button>
                                </form>
                            </div>
                        </div>
                        <div style={{ marginTop: '20px', borderTop: '1px solid #ddd', paddingTop: '20px' }}>
                            <h3 style={{ marginBottom: '10px' }}>Transaction History</h3>
                            <button onClick={handleDownload}>Download Excel Sheet</button>
                        </div>
                    </div>
                )}
            </div>
            <p className="status-message">{message}</p>
        </div>
    );
};

export default BankingDashboard;
