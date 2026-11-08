import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AnalyticsService } from '../../services/analytics.service';
import { AnalyticsOverview, CountryAnalytics, DepartmentAnalytics } from '../../models/analytics.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="space-y-6">
      <!-- Top Header -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
        <div>
          <div class="flex items-center gap-2">
            <span class="px-2.5 py-0.5 rounded text-[11px] font-bold uppercase tracking-wider bg-sky-100 text-sky-800">
              HR Executive Suite
            </span>
            <span class="text-xs text-slate-400 font-medium">Global ACME Org</span>
          </div>
          <h1 class="text-xl sm:text-2xl font-black text-slate-900 tracking-tight mt-1">
            Compensation Intelligence Dashboard
          </h1>
          <p class="text-xs sm:text-sm text-slate-500 mt-0.5">
            Real-time aggregate analytics across 10,000 personnel in 10 countries
          </p>
        </div>
        <div class="flex flex-wrap items-center gap-2.5">
          <a routerLink="/simulator" class="flex-1 sm:flex-none text-center px-4 py-2 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 border border-slate-300 rounded-xl transition">
            Salary Simulator
          </a>
          <a routerLink="/employees" class="flex-1 sm:flex-none text-center px-4 py-2 text-xs font-bold text-white bg-sky-600 hover:bg-sky-700 rounded-xl transition shadow-sm">
            Employee Directory
          </a>
        </div>
      </div>

      <!-- Loading State -->
      <div *ngIf="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 animate-pulse">
        <div *ngFor="let _ of [1,2,3,4]" class="h-28 bg-white rounded-2xl border border-slate-200"></div>
      </div>

      <!-- KPI Metrics Cards -->
      <div *ngIf="!loading && overview" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- Card 1: Headcount -->
        <div class="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm hover:shadow transition">
          <div class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Headcount</div>
          <div class="text-3xl font-black text-slate-900 mt-2 font-mono">
            {{ overview.totalHeadcount | number }}
          </div>
          <div class="text-xs font-semibold text-slate-500 mt-2 flex items-center gap-1.5">
            <span class="w-2 h-2 rounded-full bg-emerald-500"></span>
            100% Active Workforce
          </div>
        </div>

        <!-- Card 2: Total Annual Wage Bill -->
        <div class="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm hover:shadow transition">
          <div class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Annual Payroll (USD)</div>
          <div class="text-3xl font-black text-sky-700 mt-2 font-mono">
            \${{ (overview.totalPayrollUSD / 1000000) | number:'1.2-2' }}M
          </div>
          <div class="text-xs text-slate-500 font-medium mt-2">
            Exact: \${{ overview.totalPayrollUSD | number:'1.0-0' }}
          </div>
        </div>

        <!-- Card 3: Average Annual Salary -->
        <div class="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm hover:shadow transition">
          <div class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Average Salary (USD)</div>
          <div class="text-3xl font-black text-slate-900 mt-2 font-mono">
            \${{ overview.averageSalaryUSD | number:'1.0-0' }}
          </div>
          <div class="text-xs text-slate-500 font-medium mt-2">
            Company-wide mean benchmark
          </div>
        </div>

        <!-- Card 4: Median Annual Salary -->
        <div class="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm hover:shadow transition">
          <div class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Median Salary (USD)</div>
          <div class="text-3xl font-black text-emerald-700 mt-2 font-mono">
            \${{ overview.medianSalaryUSD | number:'1.0-0' }}
          </div>
          <div class="text-xs text-slate-500 font-medium mt-2">
            Min: \${{ overview.minSalaryUSD | number:'1.0-0' }} &bull; Max: \${{ overview.maxSalaryUSD | number:'1.0-0' }}
          </div>
        </div>
      </div>

      <!-- Main Visual Analytics Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Department Distribution -->
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6">
          <div class="flex items-center justify-between mb-5">
            <div>
              <h2 class="text-base font-bold text-slate-900">Payroll Spend by Department</h2>
              <p class="text-xs text-slate-400">Headcount vs total compensation budget</p>
            </div>
            <span class="text-[11px] font-bold px-2.5 py-1 bg-slate-100 rounded-lg text-slate-600">8 Units</span>
          </div>

          <div class="space-y-4">
            <div *ngFor="let dept of departments" class="space-y-1.5">
              <div class="flex justify-between items-center text-xs">
                <span class="font-bold text-slate-800">{{ dept.departmentName }}</span>
                <span class="font-mono text-slate-600 font-medium">
                  {{ dept.headcount }} staff &bull; \${{ dept.totalPayrollUSD | number:'1.0-0' }} ({{ dept.payrollPercentage }}%)
                </span>
              </div>
              <div class="w-full bg-slate-100 rounded-full h-3 overflow-hidden p-0.5">
                <div class="bg-sky-600 h-2 rounded-full transition-all duration-500" [style.width.%]="dept.payrollPercentage"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Country Compensation Footprint -->
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 sm:p-6 flex flex-col">
          <div class="flex items-center justify-between mb-5">
            <div>
              <h2 class="text-base font-bold text-slate-900">Regional Multi-Currency Footprint</h2>
              <p class="text-xs text-slate-400">Local contracts converted to USD reporting</p>
            </div>
            <span class="text-[11px] font-bold px-2.5 py-1 bg-slate-100 rounded-lg text-slate-600">10 Jurisdictions</span>
          </div>

          <div class="overflow-x-auto -mx-5 sm:-mx-6 flex-1">
            <table class="w-full text-left text-xs min-w-[500px]">
              <thead class="bg-slate-50 text-slate-500 uppercase font-bold text-[10px] tracking-wider border-y border-slate-200">
                <tr>
                  <th class="py-2.5 px-4 sm:px-6">Country</th>
                  <th class="py-2.5 px-3">Headcount</th>
                  <th class="py-2.5 px-3">Local Payroll Total</th>
                  <th class="py-2.5 px-4 sm:px-6 text-right">Normalized (USD)</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                <tr *ngFor="let c of countries" class="hover:bg-slate-50/80 transition">
                  <td class="py-3 px-4 sm:px-6 font-semibold text-slate-900">
                    {{ c.countryName }}
                    <span class="text-[10px] text-slate-400 font-normal ml-1">({{ c.currency }})</span>
                  </td>
                  <td class="py-3 px-3 text-slate-600 font-mono">{{ c.headcount }}</td>
                  <td class="py-3 px-3 text-slate-700 font-mono font-medium">
                    {{ getCurrencySymbol(c.currency) }}{{ c.totalPayrollLocal | number:'1.0-0' }}
                  </td>
                  <td class="py-3 px-4 sm:px-6 text-right font-mono font-bold text-slate-900">
                    \${{ c.totalPayrollUSD | number:'1.0-0' }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  overview?: AnalyticsOverview;
  departments: DepartmentAnalytics[] = [];
  countries: CountryAnalytics[] = [];
  loading = true;

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.analyticsService.getOverview().subscribe({
      next: (data) => {
        this.overview = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load overview', err);
        this.loading = false;
      }
    });

    this.analyticsService.getDepartmentAnalytics().subscribe({
      next: (data) => this.departments = data,
      error: (err) => console.error('Failed to load departments', err)
    });

    this.analyticsService.getCountryAnalytics().subscribe({
      next: (data) => this.countries = data,
      error: (err) => console.error('Failed to load countries', err)
    });
  }

  getCurrencySymbol(curr?: string): string {
    if (!curr) return '$';
    switch (curr) {
      case 'INR': return '₹';
      case 'GBP': return '£';
      case 'EUR': return '€';
      case 'JPY': return '¥';
      case 'CAD': return 'CA$';
      case 'AUD': return 'A$';
      case 'SGD': return 'S$';
      case 'CHF': return 'CHF ';
      case 'BRL': return 'R$';
      default: return '$';
    }
  }
}
