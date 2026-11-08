import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeService } from '../../services/employee.service';
import { CreateEmployeeRequest, EmployeeSummary, PagedResponse } from '../../models/employee.model';
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
          <p class="text-xs sm:text-sm text-slate-500 mt-0.5">Filter, search, audit and manage compensation records across all 10 countries</p>
        </div>
        <div class="flex flex-wrap items-center gap-2.5">
          <button (click)="openAddModal()"
                  class="w-full sm:w-auto inline-flex items-center justify-center px-4 py-2.5 text-xs font-bold text-white bg-sky-600 hover:bg-sky-700 rounded-xl transition shadow-sm">
            <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
            Add New Employee
          </button>
          <button (click)="exportCsv()"
                  class="w-full sm:w-auto inline-flex items-center justify-center px-4 py-2.5 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 border border-slate-300 rounded-xl transition shadow-sm">
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

          <!-- Filter Action Buttons -->
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
          <table class="w-full text-left text-xs min-w-[750px]">
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
                <td class="py-3.5 px-4 text-slate-600 font-medium">
                  <span class="inline-flex items-center">
                    {{ formatCountry(emp.country) }}
                  </span>
                </td>
                <td class="py-3.5 px-4 text-right font-mono font-medium text-slate-700">
                  {{ getCurrencySymbol(emp.currency) }}{{ emp.annualGrossLocal | number:'1.0-0' }}
                  <span class="text-[10px] text-slate-400 ml-0.5">{{ emp.currency }}</span>
                </td>
                <td class="py-3.5 px-4 text-right font-mono font-black text-slate-900">
                  \${{ emp.annualGrossUSD | number:'1.0-0' }}
                </td>
                <td class="py-3.5 px-4 sm:px-6 text-center">
                  <div class="inline-flex items-center space-x-1.5">
                    <button (click)="openDetail(emp.id)"
                            class="px-2.5 py-1.5 text-xs font-bold text-sky-700 bg-sky-50 hover:bg-sky-100 rounded-lg border border-sky-200 transition">
                      Inspect & Edit
                    </button>
                    <button (click)="confirmDelete(emp)"
                            class="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                            title="Delete Employee">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                      </svg>
                    </button>
                  </div>
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

      <!-- Add Employee Modal Dialog -->
      <div *ngIf="showAddModal" class="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
        <div class="bg-white w-full max-w-2xl rounded-2xl shadow-2xl p-6 sm:p-7 space-y-5 border border-slate-200 max-h-[90vh] overflow-y-auto">
          <div class="flex justify-between items-start border-b border-slate-100 pb-4">
            <div>
              <span class="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-sky-100 text-sky-800">
                Staff Onboarding
              </span>
              <h2 class="text-xl font-black text-slate-900 mt-1">Add New Employee</h2>
              <p class="text-xs text-slate-500">Create a new employee profile with initial compensation structure</p>
            </div>
            <button (click)="showAddModal = false" class="p-1.5 text-slate-400 hover:text-slate-700 rounded-xl hover:bg-slate-100">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5 text-xs">
            <div>
              <label class="block font-bold text-slate-700 mb-1">First Name <span class="text-rose-500">*</span></label>
              <input type="text" [(ngModel)]="newEmployee.firstName" placeholder="e.g. Ramesh"
                     class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Last Name <span class="text-rose-500">*</span></label>
              <input type="text" [(ngModel)]="newEmployee.lastName" placeholder="e.g. Sharma"
                     class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
            </div>
            <div class="sm:col-span-2">
              <label class="block font-bold text-slate-700 mb-1">Work Email <span class="text-rose-500">*</span></label>
              <input type="email" [(ngModel)]="newEmployee.email" placeholder="ramesh.sharma@acme.corp"
                     class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Department <span class="text-rose-500">*</span></label>
              <select [(ngModel)]="newEmployee.department"
                      class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500 bg-white">
                <option *ngFor="let d of departments" [value]="d.value">{{ d.label }}</option>
              </select>
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Job Title <span class="text-rose-500">*</span></label>
              <input type="text" [(ngModel)]="newEmployee.jobTitle" placeholder="e.g. Senior Software Engineer"
                     class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Country <span class="text-rose-500">*</span></label>
              <select [(ngModel)]="newEmployee.country" (change)="onCountryChange()"
                      class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500 bg-white">
                <option *ngFor="let c of countries" [value]="c.value">{{ c.label }}</option>
              </select>
            </div>
            <div>
              <label class="block font-bold text-slate-700 mb-1">Local Currency</label>
              <input type="text" [value]="newEmployee.currency + ' (' + getCurrencySymbol(newEmployee.currency) + ')'" disabled
                     class="w-full px-3 py-2 border border-slate-200 bg-slate-100 rounded-xl text-slate-600 font-bold font-mono">
            </div>
          </div>

          <!-- Salary Structure Sub-Section -->
          <div class="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                Annual Compensation Structure ({{ newEmployee.currency }})
              </span>
              <span class="text-xs font-mono font-bold text-sky-700">
                Gross: {{ getCurrencySymbol(newEmployee.currency) }}{{ calculateGross() | number:'1.0-0' }}
              </span>
            </div>
            <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 text-xs">
              <div>
                <label class="block text-slate-600 font-medium mb-1">Base Salary</label>
                <input type="number" [(ngModel)]="newEmployee.baseSalary"
                       class="w-full px-3 py-1.5 border border-slate-300 rounded-lg">
              </div>
              <div>
                <label class="block text-slate-600 font-medium mb-1">Housing Allowance</label>
                <input type="number" [(ngModel)]="newEmployee.housingAllowance"
                       class="w-full px-3 py-1.5 border border-slate-300 rounded-lg">
              </div>
              <div>
                <label class="block text-slate-600 font-medium mb-1">Transport Allowance</label>
                <input type="number" [(ngModel)]="newEmployee.transportAllowance"
                       class="w-full px-3 py-1.5 border border-slate-300 rounded-lg">
              </div>
              <div>
                <label class="block text-slate-600 font-medium mb-1">Bonus</label>
                <input type="number" [(ngModel)]="newEmployee.performanceBonus"
                       class="w-full px-3 py-1.5 border border-slate-300 rounded-lg">
              </div>
              <div class="col-span-2">
                <label class="block text-slate-600 font-medium mb-1">Statutory Deduction (Tax)</label>
                <input type="number" [(ngModel)]="newEmployee.statutoryDeduction"
                       class="w-full px-3 py-1.5 border border-slate-300 rounded-lg">
              </div>
            </div>
          </div>

          <div *ngIf="addError" class="p-3 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-700 font-medium">
            {{ addError }}
          </div>

          <div class="flex justify-end space-x-2 pt-2 border-t border-slate-100">
            <button (click)="showAddModal = false" class="px-4 py-2 text-xs font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl">
              Cancel
            </button>
            <button (click)="submitAddEmployee()" [disabled]="adding"
                    class="px-5 py-2 text-xs font-bold text-white bg-sky-600 hover:bg-sky-700 rounded-xl disabled:opacity-50">
              {{ adding ? 'Saving...' : 'Add Employee' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Delete Confirmation Modal -->
      <div *ngIf="employeeToDelete" class="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
        <div class="bg-white w-full max-w-md rounded-2xl shadow-2xl p-6 space-y-4 border border-slate-200">
          <div class="w-12 h-12 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center mx-auto">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
            </svg>
          </div>
          <div class="text-center">
            <h3 class="text-base font-black text-slate-900">Remove Employee Record?</h3>
            <p class="text-xs text-slate-500 mt-1">
              Are you sure you want to remove <span class="font-bold text-slate-800">{{ employeeToDelete.fullName }}</span>
              ({{ employeeToDelete.employeeCode }})? This action will remove the employee from the payroll register.
            </p>
          </div>
          <div class="flex justify-end space-x-2 pt-2">
            <button (click)="employeeToDelete = null" class="px-4 py-2 text-xs font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl">
              Cancel
            </button>
            <button (click)="executeDelete()" [disabled]="deleting"
                    class="px-5 py-2 text-xs font-bold text-white bg-rose-600 hover:bg-rose-700 rounded-xl disabled:opacity-50">
              {{ deleting ? 'Deleting...' : 'Confirm Deletion' }}
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

  showAddModal = false;
  adding = false;
  addError = '';
  newEmployee: CreateEmployeeRequest = this.getInitialEmployeePayload();

  employeeToDelete: EmployeeSummary | null = null;
  deleting = false;

  countries = [
    { value: 'INDIA', label: 'India (INR ₹)' },
    { value: 'UNITED_STATES', label: 'United States (USD $)' },
    { value: 'UNITED_KINGDOM', label: 'United Kingdom (GBP £)' },
    { value: 'GERMANY', label: 'Germany (EUR €)' },
    { value: 'JAPAN', label: 'Japan (JPY ¥)' },
    { value: 'CANADA', label: 'Canada (CAD CA$)' },
    { value: 'AUSTRALIA', label: 'Australia (AUD A$)' },
    { value: 'SINGAPORE', label: 'Singapore (SGD S$)' },
    { value: 'SWITZERLAND', label: 'Switzerland (CHF)' },
    { value: 'BRAZIL', label: 'Brazil (BRL R$)' }
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

  openAddModal(): void {
    this.newEmployee = this.getInitialEmployeePayload();
    this.addError = '';
    this.showAddModal = true;
  }

  onCountryChange(): void {
    this.newEmployee.currency = this.getCurrencyForCountry(this.newEmployee.country);
  }

  calculateGross(): number {
    return (this.newEmployee.baseSalary || 0) +
      (this.newEmployee.housingAllowance || 0) +
      (this.newEmployee.transportAllowance || 0) +
      (this.newEmployee.performanceBonus || 0);
  }

  submitAddEmployee(): void {
    if (!this.newEmployee.firstName || !this.newEmployee.lastName || !this.newEmployee.email || !this.newEmployee.jobTitle) {
      this.addError = 'Please fill in all required fields (First Name, Last Name, Email, Job Title).';
      return;
    }
    this.adding = true;
    this.addError = '';
    this.employeeService.createEmployee(this.newEmployee).subscribe({
      next: () => {
        this.adding = false;
        this.showAddModal = false;
        this.loadEmployees();
      },
      error: (err) => {
        this.adding = false;
        this.addError = err.error?.message || 'Failed to create employee. Please verify details.';
      }
    });
  }

  confirmDelete(emp: EmployeeSummary): void {
    this.employeeToDelete = emp;
  }

  executeDelete(): void {
    if (!this.employeeToDelete) return;
    this.deleting = true;
    this.employeeService.deleteEmployee(this.employeeToDelete.id).subscribe({
      next: () => {
        this.deleting = false;
        this.employeeToDelete = null;
        this.loadEmployees();
      },
      error: (err) => {
        this.deleting = false;
        alert('Failed to delete employee: ' + (err.error?.message || 'Unknown error'));
      }
    });
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

  private getCurrencyForCountry(country: string): string {
    switch (country) {
      case 'INDIA': return 'INR';
      case 'UNITED_KINGDOM': return 'GBP';
      case 'GERMANY': return 'EUR';
      case 'JAPAN': return 'JPY';
      case 'CANADA': return 'CAD';
      case 'AUSTRALIA': return 'AUD';
      case 'SINGAPORE': return 'SGD';
      case 'SWITZERLAND': return 'CHF';
      case 'BRAZIL': return 'BRL';
      default: return 'USD';
    }
  }

  private getInitialEmployeePayload(): CreateEmployeeRequest {
    return {
      firstName: '',
      lastName: '',
      email: '',
      department: 'ENGINEERING',
      jobTitle: '',
      country: 'INDIA',
      currency: 'INR',
      baseSalary: 1200000,
      housingAllowance: 300000,
      transportAllowance: 100000,
      performanceBonus: 150000,
      statutoryDeduction: 250000
    };
  }
}
