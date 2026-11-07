import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeeService } from '../../services/employee.service';
import { AuditLog } from '../../models/employee.model';

@Component({
  selector: 'app-audit-trail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <div class="bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm">
        <span class="px-2.5 py-0.5 rounded text-[11px] font-bold uppercase tracking-wider bg-slate-100 text-slate-700">
          Governance & Compliance
        </span>
        <h1 class="text-xl sm:text-2xl font-black text-slate-900 tracking-tight mt-1.5">
          Compensation Audit Trail
        </h1>
        <p class="text-xs sm:text-sm text-slate-500 mt-0.5">
          Immutable history of all salary revisions, reasons, and authorized users
        </p>
      </div>

      <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-left text-xs min-w-[700px]">
            <thead class="bg-slate-50 text-slate-500 uppercase text-[10px] font-bold tracking-wider border-b border-slate-200">
              <tr>
                <th class="py-3 px-4 sm:px-6">Timestamp</th>
                <th class="py-3 px-4">Employee</th>
                <th class="py-3 px-4">Previous Gross (USD)</th>
                <th class="py-3 px-4">New Gross (USD)</th>
                <th class="py-3 px-4">Business Reason</th>
                <th class="py-3 px-4 sm:px-6">Authorized By</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-100">
              <tr *ngIf="loading">
                <td colspan="6" class="py-12 text-center text-slate-400 font-medium">Loading audit history...</td>
              </tr>
              <tr *ngIf="!loading && logs.length === 0">
                <td colspan="6" class="py-12 text-center text-slate-400 font-medium">
                  No salary adjustments recorded yet. Inspect any employee in the directory to execute a revision.
                </td>
              </tr>
              <tr *ngFor="let log of logs" class="hover:bg-slate-50/80 transition">
                <td class="py-3.5 px-4 sm:px-6 font-mono text-slate-500">
                  {{ log.modifiedAt | date:'medium' }}
                </td>
                <td class="py-3.5 px-4">
                  <div class="font-bold text-slate-900">{{ log.employeeName }}</div>
                  <div class="text-[11px] font-mono text-sky-700 font-semibold">{{ log.employeeCode }}</div>
                </td>
                <td class="py-3.5 px-4 font-mono text-slate-400 line-through">
                  \${{ log.previousGrossUSD | number:'1.0-0' }}
                </td>
                <td class="py-3.5 px-4 font-mono font-bold text-emerald-600">
                  \${{ log.newGrossUSD | number:'1.0-0' }}
                </td>
                <td class="py-3.5 px-4 text-slate-800 font-medium">
                  {{ log.reason }}
                </td>
                <td class="py-3.5 px-4 sm:px-6 text-slate-500">
                  {{ log.modifiedBy }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class AuditTrailComponent implements OnInit {
  logs: AuditLog[] = [];
  loading = true;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.employeeService.getRecentAuditLogs().subscribe({
      next: (data) => {
        this.logs = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load audit trail', err);
        this.loading = false;
      }
    });
  }
}
