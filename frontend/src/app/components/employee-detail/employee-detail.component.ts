import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeService } from '../../services/employee.service';
import { AuditLog, EmployeeDetail, SalaryUpdateRequest, UpdateEmployeeProfileRequest } from '../../models/employee.model';

@Component({
  selector: 'app-employee-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex justify-end transition-opacity">
      <div class="bg-white w-full sm:max-w-2xl min-h-full sm:h-full shadow-2xl p-5 sm:p-7 overflow-y-auto flex flex-col justify-between">
        <div class="space-y-6">
          <!-- Top bar -->
          <div class="flex items-start justify-between pb-4 border-b border-slate-100">
            <div>
              <div class="flex items-center space-x-2">
                <span class="inline-block text-[11px] font-mono font-bold text-sky-800 bg-sky-50 px-2.5 py-0.5 rounded-md border border-sky-200">
                  {{ employee?.employeeCode }}
                </span>
                <span class="inline-block text-[11px] font-bold text-emerald-800 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200">
                  Active Member
                </span>
              </div>
              <h2 class="text-xl sm:text-2xl font-black text-slate-900 mt-1.5">{{ employee?.fullName }}</h2>
              <p class="text-xs text-slate-500 font-medium">
                {{ employee?.jobTitle }} &bull; {{ employee?.departmentName }} ({{ employee?.countryName }})
              </p>
            </div>
            <button (click)="close()" class="p-2 text-slate-400 hover:text-slate-700 rounded-xl hover:bg-slate-100 transition" aria-label="Close modal">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>

          <!-- Tab Navigation -->
          <div class="flex border-b border-slate-200 text-xs font-bold">
            <button (click)="activeTab = 'COMPENSATION'"
                    class="py-2 px-4 border-b-2 transition"
                    [ngClass]="activeTab === 'COMPENSATION' ? 'border-sky-600 text-sky-600' : 'border-transparent text-slate-400 hover:text-slate-700'">
              Compensation Structure
            </button>
            <button (click)="activeTab = 'EDIT_PROFILE'"
                    class="py-2 px-4 border-b-2 transition"
                    [ngClass]="activeTab === 'EDIT_PROFILE' ? 'border-sky-600 text-sky-600' : 'border-transparent text-slate-400 hover:text-slate-700'">
              Edit Profile
            </button>
            <button (click)="activeTab = 'AUDIT'"
                    class="py-2 px-4 border-b-2 transition"
                    [ngClass]="activeTab === 'AUDIT' ? 'border-sky-600 text-sky-600' : 'border-transparent text-slate-400 hover:text-slate-700'">
              Audit Trail ({{ auditLogs.length }})
            </button>
          </div>

          <!-- Loading state -->
          <div *ngIf="loading" class="py-16 text-center text-slate-400 font-medium">Fetching employee compensation details...</div>

          <div *ngIf="!loading && employee" class="space-y-6">

            <!-- TAB 1: COMPENSATION STRUCTURE & REVISION -->
            <div *ngIf="activeTab === 'COMPENSATION'" class="space-y-6">
              <!-- Current Compensation Card -->
              <div class="bg-slate-50 rounded-2xl p-4 sm:p-5 border border-slate-200">
                <div class="flex flex-wrap justify-between items-center gap-2 mb-3">
                  <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Current Compensation</span>
                  <span class="text-xs font-bold px-2.5 py-1 rounded-md bg-white border border-slate-200 text-slate-700">
                    Currency: {{ employee.salaryStructure.currency }} ({{ getCurrencySymbol(employee.salaryStructure.currency) }})
                  </span>
                </div>

                <div class="grid grid-cols-2 gap-2.5 text-xs mb-3">
                  <div class="bg-white p-3 rounded-xl border border-slate-200/80">
                    <div class="text-slate-500 text-[11px] font-medium">Base Salary (Annual)</div>
                    <div class="text-sm font-mono font-bold text-slate-900 mt-0.5">
                      {{ getCurrencySymbol(employee.salaryStructure.currency) }}{{ employee.salaryStructure.baseSalary | number:'1.0-0' }}
                    </div>
                  </div>
                  <div class="bg-white p-3 rounded-xl border border-slate-200/80">
                    <div class="text-slate-500 text-[11px] font-medium">Performance Bonus</div>
                    <div class="text-sm font-mono font-bold text-slate-900 mt-0.5">
                      {{ getCurrencySymbol(employee.salaryStructure.currency) }}{{ employee.salaryStructure.performanceBonus | number:'1.0-0' }}
                    </div>
                  </div>
                  <div class="bg-white p-3 rounded-xl border border-slate-200/80">
                    <div class="text-slate-500 text-[11px] font-medium">Housing Allowance</div>
                    <div class="text-sm font-mono font-bold text-slate-900 mt-0.5">
                      {{ getCurrencySymbol(employee.salaryStructure.currency) }}{{ employee.salaryStructure.housingAllowance | number:'1.0-0' }}
                    </div>
                  </div>
                  <div class="bg-white p-3 rounded-xl border border-slate-200/80">
                    <div class="text-slate-500 text-[11px] font-medium">Transport Allowance</div>
                    <div class="text-sm font-mono font-bold text-slate-900 mt-0.5">
                      {{ getCurrencySymbol(employee.salaryStructure.currency) }}{{ employee.salaryStructure.transportAllowance | number:'1.0-0' }}
                    </div>
                  </div>
                </div>

                <!-- Summary Totals -->
                <div class="bg-sky-50/70 p-3.5 rounded-xl border border-sky-100 flex flex-wrap justify-between items-center gap-3 text-xs">
                  <div>
                    <div class="text-sky-950 font-bold">Total Gross (USD Equivalent)</div>
                    <div class="text-xl font-black text-sky-800 font-mono">
                      \${{ employee.salaryStructure.grossAnnualUSD | number:'1.0-0' }}
                    </div>
                  </div>
                  <div class="text-left sm:text-right">
                    <div class="text-slate-500 font-medium">Monthly Take-Home (Net)</div>
                    <div class="text-sm font-bold text-slate-800 font-mono">
                      {{ getCurrencySymbol(employee.salaryStructure.currency) }}{{ employee.salaryStructure.monthlyNetLocal | number:'1.0-0' }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- Salary Adjustment Form -->
              <div class="bg-white rounded-2xl border border-slate-200 p-4 sm:p-5 space-y-4">
                <div>
                  <h3 class="text-sm font-bold text-slate-900">Revise Salary Structure</h3>
                  <p class="text-xs text-slate-500 mt-0.5">Modify component values. A business justification is required for the audit trail.</p>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">
                      Base Salary ({{ employee.salaryStructure.currency }})
                    </label>
                    <input type="number" [(ngModel)]="editForm.baseSalary"
                           class="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                  </div>
                  <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">Performance Bonus</label>
                    <input type="number" [(ngModel)]="editForm.performanceBonus"
                           class="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                  </div>
                  <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">Housing Allowance</label>
                    <input type="number" [(ngModel)]="editForm.housingAllowance"
                           class="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                  </div>
                  <div>
                    <label class="block text-xs font-semibold text-slate-600 mb-1">Transport Allowance</label>
                    <input type="number" [(ngModel)]="editForm.transportAllowance"
                           class="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                  </div>
                </div>

                <div>
                  <label class="block text-xs font-semibold text-slate-600 mb-1">Statutory Deductions (Taxes)</label>
                  <input type="number" [(ngModel)]="editForm.statutoryDeduction"
                         class="w-full px-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                </div>

                <div>
                  <label class="block text-xs font-bold text-slate-800 mb-1">
                    Reason for Salary Revision <span class="text-rose-500">*</span>
                  </label>
                  <input type="text" [(ngModel)]="editForm.reason" placeholder="e.g. Annual Merit Increase, Senior Promotion, Inflation Adjustment"
                         class="w-full px-3.5 py-2 text-xs border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-sky-500">
                </div>

                <div *ngIf="errorMessage" class="text-xs text-rose-700 font-semibold bg-rose-50 border border-rose-200 p-3 rounded-xl">
                  {{ errorMessage }}
                </div>

                <button (click)="submitRevision()" [disabled]="saving || !editForm.reason"
                        class="w-full py-2.5 bg-sky-600 text-white rounded-xl text-xs font-bold hover:bg-sky-700 disabled:opacity-50 transition shadow-sm">
                  {{ saving ? 'Recording Revision...' : 'Save & Append to Audit Trail' }}
                </button>
              </div>
            </div>

            <!-- TAB 2: EDIT EMPLOYEE PROFILE -->
            <div *ngIf="activeTab === 'EDIT_PROFILE'" class="bg-white rounded-2xl border border-slate-200 p-5 space-y-4">
              <div>
                <h3 class="text-sm font-bold text-slate-900">Update Profile Details</h3>
                <p class="text-xs text-slate-500 mt-0.5">Update personal metadata, assigned department, and country location.</p>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                <div>
                  <label class="block font-semibold text-slate-700 mb-1">First Name</label>
                  <input type="text" [(ngModel)]="profileForm.firstName"
                         class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                </div>
                <div>
                  <label class="block font-semibold text-slate-700 mb-1">Last Name</label>
                  <input type="text" [(ngModel)]="profileForm.lastName"
                         class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                </div>
                <div class="sm:col-span-2">
                  <label class="block font-semibold text-slate-700 mb-1">Email</label>
                  <input type="email" [(ngModel)]="profileForm.email"
                         class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                </div>
                <div>
                  <label class="block font-semibold text-slate-700 mb-1">Department</label>
                  <select [(ngModel)]="profileForm.department"
                          class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500 bg-white">
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
                  <label class="block font-semibold text-slate-700 mb-1">Job Title</label>
                  <input type="text" [(ngModel)]="profileForm.jobTitle"
                         class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500">
                </div>
                <div class="sm:col-span-2">
                  <label class="block font-semibold text-slate-700 mb-1">Country</label>
                  <select [(ngModel)]="profileForm.country"
                          class="w-full px-3 py-2 border border-slate-300 rounded-xl focus:ring-2 focus:ring-sky-500 bg-white">
                    <option value="INDIA">India</option>
                    <option value="UNITED_STATES">United States</option>
                    <option value="UNITED_KINGDOM">United Kingdom</option>
                    <option value="GERMANY">Germany</option>
                    <option value="JAPAN">Japan</option>
                    <option value="CANADA">Canada</option>
                    <option value="AUSTRALIA">Australia</option>
                    <option value="SINGAPORE">Singapore</option>
                    <option value="SWITZERLAND">Switzerland</option>
                    <option value="BRAZIL">Brazil</option>
                  </select>
                </div>
              </div>

              <div *ngIf="profileError" class="text-xs text-rose-700 font-semibold bg-rose-50 border border-rose-200 p-3 rounded-xl">
                {{ profileError }}
              </div>

              <button (click)="submitProfileUpdate()" [disabled]="savingProfile"
                      class="w-full py-2.5 bg-sky-600 text-white rounded-xl text-xs font-bold hover:bg-sky-700 disabled:opacity-50 transition shadow-sm">
                {{ savingProfile ? 'Updating Profile...' : 'Save Profile Changes' }}
              </button>
            </div>

            <!-- TAB 3: AUDIT TRAIL -->
            <div *ngIf="activeTab === 'AUDIT'" class="space-y-3">
              <h3 class="text-xs font-bold text-slate-400 uppercase tracking-wider">Revision History for Employee</h3>
              <div *ngIf="auditLogs.length === 0" class="p-6 text-center text-slate-400 bg-slate-50 rounded-xl border border-slate-200 text-xs">
                No salary revisions recorded yet for this employee.
              </div>
              <div class="space-y-2.5">
                <div *ngFor="let log of auditLogs" class="bg-slate-50 p-3.5 rounded-xl border border-slate-200 text-xs">
                  <div class="flex justify-between font-bold text-slate-900">
                    <span>{{ log.reason }}</span>
                    <span class="text-slate-400 font-normal">{{ log.modifiedAt | date:'mediumDate' }}</span>
                  </div>
                  <div class="text-slate-600 mt-1">
                    Gross Shift:
                    <span class="font-mono line-through text-slate-400">\${{ log.previousGrossUSD | number:'1.0-0' }}</span>
                    &rarr;
                    <span class="font-mono font-bold text-emerald-600">\${{ log.newGrossUSD | number:'1.0-0' }}</span>
                    <span class="text-[11px] text-slate-400 ml-1">({{ log.currency }} {{ log.previousGrossLocal | number:'1.0-0' }} &rarr; {{ log.newGrossLocal | number:'1.0-0' }})</span>
                  </div>
                  <div class="text-[11px] text-slate-400 mt-1">Authorized by: {{ log.modifiedBy }}</div>
                </div>
              </div>
            </div>

          </div>
        </div>

        <div class="pt-5 border-t border-slate-100 flex justify-between items-center">
          <button (click)="deleteThisEmployee()"
                  class="px-3 py-2 text-xs font-bold text-rose-600 hover:text-rose-700 hover:bg-rose-50 rounded-xl transition">
            Delete Employee
          </button>
          <button (click)="close()" class="px-5 py-2 text-xs font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl transition">
            Close Panel
          </button>
        </div>
      </div>
    </div>
  `
})
export class EmployeeDetailComponent implements OnInit {
  @Input() employeeId!: string;
  @Output() closed = new EventEmitter<void>();
  @Output() salaryUpdated = new EventEmitter<void>();

  employee?: EmployeeDetail;
  auditLogs: AuditLog[] = [];
  loading = true;
  saving = false;
  errorMessage = '';
  activeTab: 'COMPENSATION' | 'EDIT_PROFILE' | 'AUDIT' = 'COMPENSATION';

  savingProfile = false;
  profileError = '';
  profileForm: UpdateEmployeeProfileRequest = {
    firstName: '',
    lastName: '',
    email: '',
    department: 'ENGINEERING',
    jobTitle: '',
    country: 'INDIA'
  };

  editForm: SalaryUpdateRequest = {
    currency: 'USD',
    baseSalary: 0,
    housingAllowance: 0,
    transportAllowance: 0,
    performanceBonus: 0,
    statutoryDeduction: 0,
    reason: '',
    modifiedBy: 'Elena Rostova (HR VP)'
  };

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadDetail();
  }

  loadDetail(): void {
    this.loading = true;
    this.employeeService.getEmployeeById(this.employeeId).subscribe({
      next: (res) => {
        this.employee = res;
        this.editForm = {
          currency: res.salaryStructure.currency,
          baseSalary: res.salaryStructure.baseSalary,
          housingAllowance: res.salaryStructure.housingAllowance,
          transportAllowance: res.salaryStructure.transportAllowance,
          performanceBonus: res.salaryStructure.performanceBonus,
          statutoryDeduction: res.salaryStructure.statutoryDeduction,
          reason: '',
          modifiedBy: 'Elena Rostova (HR VP)'
        };
        this.profileForm = {
          firstName: res.firstName,
          lastName: res.lastName,
          email: res.email,
          department: res.department,
          jobTitle: res.jobTitle,
          country: res.country
        };
        this.loading = false;
        this.loadAuditLogs();
      },
      error: (err) => {
        console.error('Failed to load employee detail', err);
        this.loading = false;
      }
    });
  }

  loadAuditLogs(): void {
    this.employeeService.getAuditLogs(this.employeeId).subscribe({
      next: (logs) => this.auditLogs = logs,
      error: (err) => console.error('Failed to load logs', err)
    });
  }

  submitRevision(): void {
    if (!this.editForm.reason.trim()) {
      this.errorMessage = 'Please provide a valid business reason for this salary revision.';
      return;
    }
    this.saving = true;
    this.errorMessage = '';

    this.employeeService.updateSalary(this.employeeId, this.editForm).subscribe({
      next: () => {
        this.saving = false;
        this.salaryUpdated.emit();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to update salary. Check deduction values.';
      }
    });
  }

  submitProfileUpdate(): void {
    if (!this.profileForm.firstName || !this.profileForm.lastName || !this.profileForm.email || !this.profileForm.jobTitle) {
      this.profileError = 'Please fill out all required profile fields.';
      return;
    }
    this.savingProfile = true;
    this.profileError = '';
    this.employeeService.updateEmployeeProfile(this.employeeId, this.profileForm).subscribe({
      next: (updated) => {
        this.savingProfile = false;
        this.employee = updated;
        this.salaryUpdated.emit();
      },
      error: (err) => {
        this.savingProfile = false;
        this.profileError = err.error?.message || 'Failed to update profile.';
      }
    });
  }

  deleteThisEmployee(): void {
    if (!this.employee) return;
    if (confirm(`Are you sure you want to delete employee ${this.employee.fullName} (${this.employee.employeeCode})?`)) {
      this.employeeService.deleteEmployee(this.employeeId).subscribe({
        next: () => {
          this.salaryUpdated.emit();
        },
        error: (err) => {
          alert('Failed to delete employee: ' + (err.error?.message || 'Unknown error'));
        }
      });
    }
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

  close(): void {
    this.closed.emit();
  }
}
