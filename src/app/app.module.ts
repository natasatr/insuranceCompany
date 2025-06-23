import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/shared/header/header.component';
import { FooterComponent } from './components/shared/footer/footer.component';
import { RegisterComponent } from './components/register/register.component';
import { FormsModule } from '@angular/forms';
import { authInterceptor } from './services/auth.interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { LoginComponent } from './components/login/login.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { ClientManagementComponent } from './components/client-management/client-management.component';
import { ClientEditFormComponent } from './components/client-edit-form/client-edit-form.component';
import { PolicyManagementComponent } from './components/policy-management/policy-management.component';
import { PolicyAddFormComponent } from './components/policy-add-form/policy-add-form.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    RegisterComponent,
    LoginComponent,
    AdminDashboardComponent,
    ClientManagementComponent,
    ClientEditFormComponent,
    PolicyManagementComponent,
    PolicyAddFormComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor])
    )
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
