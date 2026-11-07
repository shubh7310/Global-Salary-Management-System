import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EmployeeListComponent } from './components/employee-list/employee-list.component';
import { SalarySimulatorComponent } from './components/salary-simulator/salary-simulator.component';
import { AuditTrailComponent } from './components/audit-trail/audit-trail.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'employees', component: EmployeeListComponent },
  { path: 'simulator', component: SalarySimulatorComponent },
  { path: 'audit-trail', component: AuditTrailComponent },
  { path: '**', redirectTo: 'dashboard' }
];

