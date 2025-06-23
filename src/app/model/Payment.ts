export interface Payment {
    userId: number;
    policyId: number;
    amount: number;
    policyName: string;
    paymentMethodId: string;
    cardNumber: string;
}