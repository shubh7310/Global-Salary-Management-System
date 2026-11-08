import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AnalyticsService } from '../../services/analytics.service';
import { AdjustmentSimulationResponse } from '../../models/analytics.model';

@Component({
  selector: 'app-salary-simulator',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-4xl mx-auto space-y-6">
      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
        <span class="px-2.5 py-0.5 rounded text-[11px] font-bold uppercase tracking-wider bg-sky-100 text-sky-800">
          Executive Decision Support
        </span>
        <h1 class="text-xl sm:text-2xl font-black text-slate-900 tracking-tight mt-1.5">
          Salary Revision & What-If Simulator
        </h1>
        <p class="text-xs sm:text-sm text-slate-500 mt-0.5">
          Evaluate exact budget delta and average compensation impact of proposed raises before execution
        </p>
      </div>

      <!-- Controls Card -->
      <div class="bg-white p-5 sm:p-6 rounded-2xl border border-slate-200 shadow-sm space-y-5">
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label class="block text-[11px] font-bold text-slate-500 uppercase tracking-wider mb-1">Target Country</label>
            <select [(ngModel)]="targetCountry"
                    class="w-full px-3.5 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500 bg-white">
              <option value="">Global (All 10 Countries)</option>
              <option value="INDIA">India (INR ₹)</option>
              <option value="UNITED_STATES">United States (USD $)</option>
              <option value="UNITED_KINGDOM">United Kingdom (GBP £)</option>
              <option value="GERMANY">Germany (EUR €)</option>
              <option value="JAPAN">Japan (JPY ¥)</option>
              <option value="CANADA">Canada (CAD CA$)</option>
              <option value="AUSTRALIA">Australia (AUD A$)</option>
              <option value="SINGAPORE">Singapore (SGD S$)</option>
              <option value="SWITZERLAND">Switzerland (CHF)</option>
              <option value="BRAZIL">Brazil (BRL R$)</option>
            </select>
          </div>

          <div>
            <label class="block text-[11px] font-bold text-slate-500 uppercase tracking-wider mb-1">Target Department</label>
            <select [(ngModel)]="targetDepartment"
                    class="w-full px-3.5 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500 bg-white">
              <option value="">All Departments (8)</option>
              <option value="ENGINEERING">Engineering</option>
              <option value="PRODUCT">Product Management</option>
              <option value="SALES">Sales & Revenue</option>
              <option value="MARKETING">Marketing & Growth</option>
              <option value="HUMAN_RESOURCES">Human Resources</option>
              <option value="FINANCE">Finance & Accounting</option>
              <option value="OPERATIONS">Operations & Support</option>
              <option value="LEGAL">Legal & Compliance</option>
            </select>
          </div>

          <div>
            <label class="block text-[11px] font-bold text-slate-500 uppercase tracking-wider mb-1">Percentage Raise (%)</label>
            <div class="flex items-center space-x-2">
              <input type="number" [(ngModel)]="percentageChange" min="-50" max="100" step="0.5"
                     class="w-full px-3.5 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500 bg-white font-mono font-bold">
              <span class="text-xs font-bold text-slate-600">%</span>
            </div>
          </div>
        </div>

        <!-- Quick Adjustment Preset Pills -->
        <div class="flex flex-wrap items-center gap-2 pt-1 border-t border-slate-100 text-xs">
          <span class="text-[11px] font-semibold text-slate-400">Common Models:</span>
          <button (click)="percentageChange = 3.0" class="px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold transition">
            +3% (Standard Inflation)
          </button>
          <button (click)="percentageChange = 5.0" class="px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold transition">
            +5% (Merit Band)
          </button>
          <button (click)="percentageChange = 8.0" class="px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold transition">
            +8% (Market Realignment)
          </button>
        </div>

        <div class="flex justify-end pt-2">
          <button (click)="runSimulation()" [disabled]="simulating"
                  class="w-full sm:w-auto px-6 py-2.5 bg-sky-600 text-white rounded-xl text-xs font-bold hover:bg-sky-700 disabled:opacity-50 transition shadow-sm">
            {{ simulating ? 'Computing Budget Delta...' : 'Run Simulation Model' }}
          </button>
        </div>
      </div>

      <!-- Simulation Result Card -->
      <div *ngIf="result" class="bg-white rounded-2xl border border-sky-300 shadow-md p-5 sm:p-7 space-y-6">
        <div class="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4 border-b border-slate-100 pb-5">
          <div>
            <span class="text-[11px] font-bold uppercase tracking-wider text-sky-800 bg-sky-50 px-2.5 py-1 rounded-md border border-sky-200">
              Projected Output
            </span>
            <h2 class="text-xl font-black text-slate-900 mt-2">
              Model for {{ result.percentageChange }}% Compensation Shift
            </h2>
            <p class="text-xs text-slate-500 mt-0.5">
              Affecting <span class="font-bold text-slate-800">{{ result.affectedEmployees | number }} personnel</span>
              <span *ngIf="result.filterDepartment"> in {{ getDepartmentLabel(result.filterDepartment) }}</span>
              <span *ngIf="result.filterCountry"> ({{ getCountryLabel(result.filterCountry) }})</span>
            </p>
          </div>

          <!-- Total Delta Highlight Card -->
          <div class="sm:text-right bg-slate-50 sm:bg-transparent p-3 sm:p-0 rounded-xl">
            <!-- If local currency is specified (e.g. INR for India), show local currency delta prominently -->
            <div *ngIf="result.payrollDeltaLocal != null && result.currency">
              <div class="text-[11px] font-bold text-sky-700 uppercase tracking-wider">
                Local Payroll Delta ({{ result.currency }})
              </div>
              <div class="text-2xl sm:text-3xl font-black font-mono mt-0.5" [ngClass]="result.payrollDeltaLocal >= 0 ? 'text-rose-600' : 'text-emerald-600'">
                {{ result.payrollDeltaLocal >= 0 ? '+' : '' }}{{ getCurrencySymbol(result.currency) }}{{ result.payrollDeltaLocal | number:'1.0-0' }}
              </div>
              <div class="text-xs text-slate-500 font-medium font-mono mt-0.5">
                ≈ {{ result.payrollDeltaUSD >= 0 ? '+' : '' }}\${{ result.payrollDeltaUSD | number:'1.0-0' }} USD
              </div>
            </div>

            <!-- Global / Multi-country view -->
            <div *ngIf="result.payrollDeltaLocal == null || !result.currency">
              <div class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Payroll Spend Delta (USD)</div>
              <div class="text-2xl sm:text-3xl font-black font-mono mt-1" [ngClass]="result.payrollDeltaUSD >= 0 ? 'text-rose-600' : 'text-emerald-600'">
                {{ result.payrollDeltaUSD >= 0 ? '+' : '' }}\${{ result.payrollDeltaUSD | number:'1.0-0' }}
              </div>
            </div>
          </div>
        </div>

        <!-- Local Currency Breakdown (shown when a country like India is selected) -->
        <div *ngIf="result.currentTotalPayrollLocal != null && result.currency" class="p-4 rounded-xl bg-amber-50/60 border border-amber-200">
          <div class="flex items-center space-x-2 mb-3">
            <span class="inline-block w-2.5 h-2.5 rounded-full bg-amber-500"></span>
            <span class="text-xs font-bold text-amber-900 uppercase tracking-wider">
              Local Currency Impact ({{ result.currency }} &bull; {{ getCurrencySymbol(result.currency) }})
            </span>
          </div>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
            <div class="bg-white p-3 rounded-lg border border-amber-100">
              <span class="text-slate-400 text-[10px] uppercase font-bold">Current Local Payroll</span>
              <p class="text-lg font-black font-mono text-slate-800 mt-0.5">
                {{ getCurrencySymbol(result.currency) }}{{ result.currentTotalPayrollLocal | number:'1.0-0' }}
              </p>
            </div>
            <div class="bg-white p-3 rounded-lg border border-amber-100">
              <span class="text-amber-800 text-[10px] uppercase font-bold">Projected Local Payroll</span>
              <p class="text-lg font-black font-mono text-amber-950 mt-0.5">
                {{ getCurrencySymbol(result.currency) }}{{ result.projectedTotalPayrollLocal | number:'1.0-0' }}
              </p>
            </div>
          </div>
        </div>

        <!-- Global USD Consolidated Wage Bill -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
          <div class="bg-slate-50 p-4 rounded-xl border border-slate-200">
            <div class="font-bold text-slate-400 uppercase tracking-wider text-[10px]">
              {{ result.currency ? 'Current Wage Bill (USD Converted)' : 'Current Total Wage Bill (USD)' }}
            </div>
            <div class="text-xl font-black font-mono text-slate-800 mt-1">
              \${{ result.currentTotalPayrollUSD | number:'1.0-0' }}
            </div>
            <div class="text-slate-500 font-medium mt-1">Current Mean Salary: \${{ result.currentAvgSalaryUSD | number:'1.0-0' }}</div>
          </div>

          <div class="bg-sky-50/70 p-4 rounded-xl border border-sky-100">
            <div class="font-bold text-sky-800 uppercase tracking-wider text-[10px]">
              {{ result.currency ? 'Projected Wage Bill (USD Converted)' : 'Projected Total Wage Bill (USD)' }}
            </div>
            <div class="text-xl font-black font-mono text-sky-950 mt-1">
              \${{ result.projectedTotalPayrollUSD | number:'1.0-0' }}
            </div>
            <div class="text-sky-800 font-semibold mt-1">Projected Mean Salary: \${{ result.projectedAvgSalaryUSD | number:'1.0-0' }}</div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SalarySimulatorComponent {
  targetCountry = '';
  targetDepartment = '';
  percentageChange = 5.0;
  simulating = false;
  result?: AdjustmentSimulationResponse;

  constructor(private analyticsService: AnalyticsService) {}

  runSimulation(): void {
    this.simulating = true;
    this.analyticsService.simulateAdjustment({
      country: this.targetCountry || null,
      department: this.targetDepartment || null,
      percentageChange: this.percentageChange
    }).subscribe({
      next: (res) => {
        this.result = res;
        this.simulating = false;
      },
      error: (err) => {
        console.error('Simulation failed', err);
        this.simulating = false;
      }
    });
  }

  getCurrencySymbol(curr?: string | null): string {
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

  getCountryLabel(country: string): string {
    return country.replace(/_/g, ' ');
  }

  getDepartmentLabel(dept: string): string {
    return dept.replace(/_/g, ' ');
  }
}
