import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeService } from '../../services/employee.service';
import { EmployeeSummary, PagedResponse } from '../../models/employee.model';
import { EmployeeDetailComponent } from '../employee-detail/employee-detail.component';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule, FormsModule, EmployeeDetailComponent],
  template: `
    <div class="space-y-5">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
        <div>
          <h1 class="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">Global Employee Directory</h1>
          <p class="text-xs sm:text-sm text-slate-500 mt-0.5">Filter, search, and audit salary structures for 10,000 personnel</p>
        </div>
        <div>
          <button (click)="exportCsv()"
                  class="w-full sm:w-auto inline-flex items-center justify-center px-4 py-2 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 border border-slate-300 rounded-xl transition shadow-sm">
            Download CSV Export
          </button>
        </div>
      </div>

      <!-- Filters Toolbar -->
      <div class="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
          <!-- Full-Text Search Input -->
          <div class="lg:col-span-2">
            <label class="block text-[11px] font-bold text-slate-500 uppercase tracking-wider mb-1">Search Employee</label>
            <input type="text" [(ngModel)]="searchQuery" (keyup.enter)="applyFilters()"
                   placeholder="Search ID (e.g. EMP-00042), Name, or Title..."
                   class="w-full px-3.5 py-2 text-xs border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-sky-500 bg-slate-50/50">
          </div>

          <!-- Country Filter -->
          <div>
            <label class="block text-[11px] font-bold text-slate-500 uppercase tracking-wider mb-1">Country</label>
            <select [(ngModel)]="selectedCountry" (change)="applyFilters()"
                    class="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-sky-500 bg-white">
              <option value="">All Countries (10)</option>
              <option *ngFor="let c of countries" [value]="c.value">{{ c.label }}</option>
            </select>
          </div>

          <!-- Department Filter -->
          <div>
            <label class="block text-[11px] font-bold text-slate-500 uppercase tracking-wider mb-1">Department</label>
            <select [(ngModel)]="selectedDepartment" (change)="applyFilters()"
                    class="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-sky-500 bg-white">
              <option value="">All Departments (8)</option>
              <option *ngFor="let d of departments" [value]="d.value">{{ d.label }}</option>
            </select>
          </div>

          <!-- Buttons -->
          <div class="flex items-end gap-2">
            <button (click)="applyFilters()"
                    class="flex-1 py-2 px-3 text-xs font-bold text-white bg-sky-600 hover:bg-sky-700 rounded-xl transition shadow-sm">
              Apply Filters
            </button>
            <button (click)="resetFilters()"
                    class="py-2 px-3 text-xs font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl transition">
              Reset
            </button>
          </div>
        </div>
      </div>

      <!-- Data Table Card -->
      <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-left text-xs min-w-[700px]">
            <thead class="bg-slate-50 text-slate-500 uppercase text-[10px] font-bold tracking-wider border-b border-slate-200">
              <tr>
                <th class="py-3 px-4 sm:px-6 cursor-pointer hover:text-slate-900" (click)="setSort('employeeCode')">ID</th>
                <th class="py-3 px-4 cursor-pointer hover:text-slate-900" (click)="setSort('lastName')">Name</th>
                <th class="py-3 px-4">Department & Role</th>
                <th class="py-3 px-4">Country</th>
                <th class="py-3 px-4 text-right">Local Gross</th>
                <th class="py-3 px-4 text-right cursor-pointer hover:text-slate-900" (click)="setSort('annualGrossUSD')">USD Gross</th>
                <th class="py-3 px-4 sm:px-6 text-center">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              <tr *ngIf="loading">
                <td colspan="7" class="py-12 text-center text-slate-400 font-medium">Fetching records from database...</td>
              </tr>
              <tr *ngIf="!loading && employees.length === 0">
                <td colspan="7" class="py-12 text-center text-slate-400 font-medium">No employees match your search criteria.</td>
              </tr>
              <tr *ngFor="let emp of employees" class="hover:bg-slate-50/80 transition">
                <td class="py-3.5 px-4 sm:px-6 font-mono font-bold text-sky-700">{{ emp.employeeCode }}</td>
                <td class="py-3.5 px-4 font-bold text-slate-900">{{ emp.fullName }}</td>
                <td class="py-3.5 px-4">
                  <div class="font-semibold text-slate-800">{{ emp.jobTitle }}</div>
                  <div class="text-[11px] text-slate-400 font-medium">{{ formatDept(emp.department) }}</div>
                </td>
                <td class="py-3.5 px-4 text-slate-600 font-medium">{{ formatCountry(emp.country) }}</td>
                <td class="py-3.5 px-4 text-right font-mono text-slate-600">
                  {{ emp.currency }} {{ emp.annualGrossLocal | number:'1.0-0' }}
                </td>
                <td class="py-3.5 px-4 text-right font-mono font-black text-slate-900">
                  \${{ emp.annualGrossUSD | number:'1.0-0' }}
                </td>
                <td class="py-3.5 px-4 sm:px-6 text-center">
                  <button (click)="openDetail(emp.id)"
                          class="px-3 py-1.5 text-xs font-bold text-sky-700 bg-sky-50 hover:bg-sky-100 rounded-lg border border-sky-200 transition">
                    Inspect & Edit
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination Bar -->
        <div *ngIf="pagedData" class="flex flex-col sm:flex-row items-center justify-between px-4 sm:px-6 py-3.5 bg-slate-50 border-t border-slate-200 text-xs text-slate-600 gap-3">
          <div class="text-center sm:text-left">
            Showing <span class="font-bold text-slate-900">{{ pagedData.pageNumber * pagedData.pageSize + 1 }}</span> to
            <span class="font-bold text-slate-900">{{ Math.min((pagedData.pageNumber + 1) * pagedData.pageSize, pagedData.totalElements) }}</span> of
            <span class="font-bold text-slate-900">{{ pagedData.totalElements | number }}</span> records
          </div>
          <div class="flex items-center space-x-2">
            <button (click)="changePage(pagedData.pageNumber - 1)" [disabled]="pagedData.isFirst"
                    class="px-3 py-1.5 border border-slate-300 rounded-lg bg-white text-slate-700 font-semibold disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-100 transition shadow-sm">
              &larr; Prev
            </button>
            <span class="px-2 font-mono font-bold text-slate-700">Page {{ pagedData.pageNumber + 1 }} of {{ pagedData.totalPages }}</span>
            <button (click)="changePage(pagedData.pageNumber + 1)" [disabled]="pagedData.isLast"
                    class="px-3 py-1.5 border border-slate-300 rounded-lg bg-white text-slate-700 font-semibold disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-100 transition shadow-sm">
              Next &rarr;
            </button>
          </div>
        </div>
      </div>

      <!-- Employee Detail Modal/Drawer -->
      <app-employee-detail *ngIf="selectedEmployeeId"
                           [employeeId]="selectedEmployeeId"
                           (closed)="closeDetail()"
                           (salaryUpdated)="onSalaryUpdated()">
      </app-employee-detail>
    </div>
  `
})
export class EmployeeListComponent implements OnInit {
  employees: EmployeeSummary[] = [];
  pagedData?: PagedResponse<EmployeeSummary>;
  loading = false;
  selectedEmployeeId?: string;
  Math = Math;

  searchQuery = '';
  selectedCountry = '';
  selectedDepartment = '';
  minSalary?: number;
  maxSalary?: number;
  currentPage = 0;
  pageSize = 20;
  sortBy = 'employeeCode';
  sortDir = 'ASC';

  countries = [
    { value: 'UNITED_STATES', label: 'United States (USD)' },
    { value: 'UNITED_KINGDOM', label: 'United Kingdom (GBP)' },
    { value: 'GERMANY', label: 'Germany (EUR)' },
    { value: 'INDIA', label: 'India (INR)' },
    { value: 'JAPAN', label: 'Japan (JPY)' },
    { value: 'CANADA', label: 'Canada (CAD)' },
    { value: 'AUSTRALIA', label: 'Australia (AUD)' },
    { value: 'SINGAPORE', label: 'Singapore (SGD)' },
    { value: 'SWITZERLAND', label: 'Switzerland (CHF)' },
    { value: 'BRAZIL', label: 'Brazil (BRL)' }
  ];

  departments = [
    { value: 'ENGINEERING', label: 'Engineering' },
    { value: 'PRODUCT', label: 'Product Management' },
    { value: 'SALES', label: 'Sales & Revenue' },
    { value: 'MARKETING', label: 'Marketing & Growth' },
    { value: 'HUMAN_RESOURCES', label: 'Human Resources' },
    { value: 'FINANCE', label: 'Finance & Accounting' },
    { value: 'OPERATIONS', label: 'Operations & Support' },
    { value: 'LEGAL', label: 'Legal & Compliance' }
  ];

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.loading = true;
    this.employeeService.getEmployees({
      search: this.searchQuery,
      country: this.selectedCountry || undefined,
      department: this.selectedDepartment || undefined,
      minSalary: this.minSalary,
      maxSalary: this.maxSalary,
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDir: this.sortDir
    }).subscribe({
      next: (res) => {
        this.pagedData = res;
        this.employees = res.content;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load employees', err);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.currentPage = 0;
    this.loadEmployees();
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.selectedCountry = '';
    this.selectedDepartment = '';
    this.minSalary = undefined;
    this.maxSalary = undefined;
    this.currentPage = 0;
    this.loadEmployees();
  }

  changePage(newPage: number): void {
    this.currentPage = newPage;
    this.loadEmployees();
  }

  setSort(column: string): void {
    if (this.sortBy === column) {
      this.sortDir = this.sortDir === 'ASC' ? 'DESC' : 'ASC';
    } else {
      this.sortBy = column;
      this.sortDir = 'ASC';
    }
    this.loadEmployees();
  }

  openDetail(id: string): void {
    this.selectedEmployeeId = id;
  }

  closeDetail(): void {
    this.selectedEmployeeId = undefined;
  }

  onSalaryUpdated(): void {
    this.closeDetail();
    this.loadEmployees();
  }

  exportCsv(): void {
    const url = this.employeeService.getExportCsvUrl({
      search: this.searchQuery,
      country: this.selectedCountry || undefined,
      department: this.selectedDepartment || undefined
    });
    window.open(url, '_blank');
  }

  formatDept(d: string): string {
    return d ? d.replace(/_/g, ' ') : '';
  }

  formatCountry(c: string): string {
    return c ? c.replace(/_/g, ' ') : '';
  }
}
