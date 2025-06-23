import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/shared/header/header.component';
import { FooterComponent } from './components/shared/footer/footer.component';
import { VerifyComponent } from './components/verify/verify.component';
import { DashboardComponent } from './components/main/dashboard/dashboard.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { FormsModule, NgForm } from '@angular/forms';
import { ClientDashboardComponent } from './components/client-dashboard/client-dashboard.component';
import { MyPoliciesComponent } from './components/my-policies/my-policies.component';
import { ViewAllPoliciesComponent } from './components/view-all-policies/view-all-policies.component';
import { PolicyManagmentComponent } from './component/policy-managment/policy-managment.component';
import { PolicyPurchaseComponent } from './components/policy-purchase/policy-purchase.component';
import {  provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptor/auth.interceptor';
@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    RegisterComponent,
    LoginComponent,
    VerifyComponent,
    DashboardComponent,
    ClientDashboardComponent,
    MyPoliciesComponent,
    ViewAllPoliciesComponent,
    PolicyManagmentComponent,
    PolicyPurchaseComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule, 
    FormsModule
  ],
  providers:[ provideHttpClient(
      withInterceptors([authInterceptor])
    )],
  bootstrap: [AppComponent]
})
export class AppModule { }
