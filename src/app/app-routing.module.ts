import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { VerifyComponent } from './components/verify/verify.component';
import { authGuard } from './guard/auth.guard';
import { ClientDashboardComponent } from './components/client-dashboard/client-dashboard.component';
import { MyPoliciesComponent } from './components/my-policies/my-policies.component';
import { ViewAllPoliciesComponent } from './components/view-all-policies/view-all-policies.component';

const routes: Routes = [
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'verify-2fa', component: VerifyComponent},
  {path: 'dashboard-client', component: ClientDashboardComponent, canActivate: [authGuard]},
  {path: 'dashboard-client/my-policies', component: MyPoliciesComponent, canActivate: [authGuard]},
  {path: 'dashboard-client/view-all-policies', component: ViewAllPoliciesComponent, canActivate: [authGuard]},
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  {path: '**', redirectTo: 'login'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
