import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  template: `
    <div class="min-h-screen flex flex-col bg-slate-100/70 text-slate-900">
      <app-navbar></app-navbar>
      <main class="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8">
        <router-outlet></router-outlet>
      </main>
      <footer class="bg-white border-t border-slate-200 py-5 text-center text-xs text-slate-500">
        <div class="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <div>
            <span class="font-bold text-slate-700">ACME Corporation HRMS</span> &bull; 10,000 Global Employees
          </div>
          <div class="text-[11px] text-slate-400">
            Engineered with Spring Boot 3, Java 21, PostgreSQL & Angular &bull; Craftsperson: Shubhasheesh Shukla
          </div>
        </div>
      </footer>
    </div>
  `
})
export class AppComponent {}
