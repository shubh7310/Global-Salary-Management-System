import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="bg-slate-900 border-b border-slate-800 sticky top-0 z-40 shadow-md">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center space-x-6">
            <!-- Brand Logo -->
            <div class="flex items-center space-x-3 cursor-pointer" routerLink="/">
              <div class="w-9 h-9 rounded-lg bg-sky-500 flex items-center justify-center text-slate-950 font-black text-lg shadow-sm">
                A
              </div>
              <div>
                <span class="text-lg font-black tracking-tight text-white">ACME <span class="text-sky-400">HRMS</span></span>
                <span class="hidden sm:block text-[10px] font-semibold text-slate-400 -mt-1 tracking-wider uppercase">Global Compensation Governance</span>
              </div>
            </div>

            <!-- Desktop Navigation -->
            <nav class="hidden md:flex items-center space-x-1">
              <a routerLink="/dashboard" routerLinkActive="bg-slate-800 text-sky-400 font-semibold"
                 class="px-3 py-1.5 rounded-md text-xs font-medium text-slate-300 hover:text-white hover:bg-slate-800/60 transition">
                Dashboard
              </a>
              <a routerLink="/employees" routerLinkActive="bg-slate-800 text-sky-400 font-semibold"
                 class="px-3 py-1.5 rounded-md text-xs font-medium text-slate-300 hover:text-white hover:bg-slate-800/60 transition">
                Employees Directory
              </a>
              <a routerLink="/simulator" routerLinkActive="bg-slate-800 text-sky-400 font-semibold"
                 class="px-3 py-1.5 rounded-md text-xs font-medium text-slate-300 hover:text-white hover:bg-slate-800/60 transition">
                Salary Simulator
              </a>
              <a routerLink="/audit-trail" routerLinkActive="bg-slate-800 text-sky-400 font-semibold"
                 class="px-3 py-1.5 rounded-md text-xs font-medium text-slate-300 hover:text-white hover:bg-slate-800/60 transition">
                Audit Trail
              </a>
            </nav>
          </div>

          <div class="flex items-center space-x-3">
            <!-- 10k Active Badge -->
            <div class="hidden sm:inline-flex items-center px-2.5 py-1 rounded-full text-[11px] font-medium bg-emerald-950/80 text-emerald-400 border border-emerald-800/60">
              <span class="w-1.5 h-1.5 mr-1.5 bg-emerald-400 rounded-full animate-pulse"></span>
              10,000 Employees Active
            </div>

            <!-- User Pill -->
            <div class="flex items-center space-x-2.5 pl-2 sm:border-l sm:border-slate-800">
              <div class="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 text-sky-400 flex items-center justify-center text-xs font-bold">
                ER
              </div>
              <div class="hidden sm:block text-left">
                <div class="text-xs font-bold text-white leading-none">Elena Rostova</div>
                <div class="text-[10px] text-slate-400 font-medium leading-none mt-1">VP, HR Operations</div>
              </div>
            </div>

            <!-- Mobile Menu Toggle Button -->
            <button (click)="mobileMenuOpen = !mobileMenuOpen"
                    class="md:hidden p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 focus:outline-none"
                    aria-label="Toggle menu">
              <svg *ngIf="!mobileMenuOpen" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
              </svg>
              <svg *ngIf="mobileMenuOpen" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
        </div>

        <!-- Mobile Menu Dropdown -->
        <div *ngIf="mobileMenuOpen" class="md:hidden py-3 border-t border-slate-800 space-y-1">
          <a routerLink="/dashboard" (click)="mobileMenuOpen = false" routerLinkActive="bg-slate-800 text-sky-400 font-semibold"
             class="block px-3 py-2 rounded-md text-sm font-medium text-slate-300 hover:bg-slate-800">
            Dashboard
          </a>
          <a routerLink="/employees" (click)="mobileMenuOpen = false" routerLinkActive="bg-slate-800 text-sky-400 font-semibold"
             class="block px-3 py-2 rounded-md text-sm font-medium text-slate-300 hover:bg-slate-800">
            Employees Directory
          </a>
          <a routerLink="/simulator" (click)="mobileMenuOpen = false" routerLinkActive="bg-slate-800 text-sky-400 font-semibold"
             class="block px-3 py-2 rounded-md text-sm font-medium text-slate-300 hover:bg-slate-800">
            Salary Simulator
          </a>
          <a routerLink="/audit-trail" (click)="mobileMenuOpen = false" routerLinkActive="bg-slate-800 text-sky-400 font-semibold"
             class="block px-3 py-2 rounded-md text-sm font-medium text-slate-300 hover:bg-slate-800">
            Audit Trail
          </a>
          <div class="pt-2 px-3 sm:hidden">
            <span class="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium bg-emerald-950 text-emerald-400">
              10,000 Employees Active
            </span>
          </div>
        </div>
      </div>
    </header>
  `
})
export class NavbarComponent {
  mobileMenuOpen = false;
}
