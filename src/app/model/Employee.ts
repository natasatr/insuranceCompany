export interface Employee {
    id?: number;
    firstName: string;
    lastName: string;
    username: string;
    password: string;
    role?: 'EMPLOYEE';
    email?: string;
    status?: string;

}