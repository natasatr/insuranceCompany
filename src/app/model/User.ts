export interface User {
    id?: number;
    firstName: string;
    lastName: string;
    username: string; 
    email: string;
    password: string;
    role:'CLIENT';
    status?: 'ACTIVE' | 'BLOCK';
}