import { TransactionStatus } from './enums/TransactionStatus';
import { Policy } from './Policy';
import { User } from './User';
export interface Purchase {
    id?: number;
    user: User;
    policy: Policy;
    transactionId: string;
    amount: number;
    status: TransactionStatus;
    createdAt: string;
}