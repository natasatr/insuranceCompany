Project Overview
This project represents an information system for an insurance company consisting of two web applications: an Administrator Application and a Client Application.
The system allows clients to browse, purchase, and manage various insurance policies such as life, travel, and property insurance. Administrators can manage user accounts and the services offered to clients.

Security Features
New clients can register and log in using two-factor authentication (2FA). After entering their username and password, a verification code is sent to their email, which must be entered to complete the login process.
The system also implements a Single Sign-On (SSO) mechanism, allowing administrators and employees with access to both applications to authenticate once and access the entire system.

Payment Integration
Clients can purchase insurance policies through integration with a payment service such as Stripe. All payments are executed in test mode only, without real charges.
All transaction data must be securely stored in the database. Upon successful purchase, the client receives the purchased policy in PDF format via email.

System Architecture
The system architecture includes several key components.
The Access Controller manages authentication, authorization, session tracking, and detection of potentially malicious requests, such as entering unusually large payment amounts. It can automatically terminate a suspicious session.
The SIEM component is responsible for monitoring and logging all security-sensitive actions.
Nagios is configured to monitor all critical system services.
