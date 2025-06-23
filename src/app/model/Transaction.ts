import { PaymentType } from "./enums/PaymentType";
import { TransactionStatus } from "./enums/TransactionStatus";
import { User } from "./User";

export interface Transaction {
    id?: number;
    user: User;
    amount: number;
    createdAt: string;
    paymentType: PaymentType;
    transactionStatus: TransactionStatus;
}