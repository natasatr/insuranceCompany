import { Policy } from "./Policy";
import { User } from "./User";

export interface UserPolicy {
    id?: number;
    user: User;
    policy: Policy;
    purchaseDate: string;
}