import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { authGuard } from './services/guard/auth.guard';
import { ClientManagementComponent } from './components/client-management/client-management.component';
import { PolicyManagementComponent } from './components/policy-management/policy-management.component';

const routes: Routes = [
  {path: '', redirectTo:'/login', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent}, 
  {
    path: 'dashboard-admin', 
    component: AdminDashboardComponent,
    canActivate: [authGuard],
  },
  {path: 'dashboard-admin/client-management',
    component: ClientManagementComponent,
    canActivate: [authGuard]
  }, 
  {
    path: 'dashboard-admin/policy-managment',
    component: PolicyManagementComponent,
    canActivate: [authGuard]
  }
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
