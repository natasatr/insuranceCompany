export class JwtResponse {
    accessToken: string;
    refreshToken: string;

    constructor(accessToken: string, refreshToken: string) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    getAccessToken(): string {
        return this.accessToken;    
    }
    getRefreshToken(): string {
        return this.refreshToken;    
    }

    setAccessToken(accessToken: string): void {
        this.accessToken = accessToken;  
    }
    setrefreshToken(refreshToken: string) {
    this.refreshToken = refreshToken;
}
}