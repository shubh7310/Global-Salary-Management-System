import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AuditLog,
  CreateEmployeeRequest,
  EmployeeDetail,
  EmployeeSummary,
  PagedResponse,
  SalaryUpdateRequest,
  UpdateEmployeeProfileRequest
} from '../models/employee.model';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private readonly baseUrl = typeof window !== 'undefined' && window.location.port === '4200'
    ? 'http://localhost:8080/api/v1/employees'
    : '/api/v1/employees';

  constructor(private http: HttpClient) {}

  getEmployees(params: {
    search?: string;
    country?: string;
    department?: string;
    minSalary?: number;
    maxSalary?: number;
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: string;
  }): Observable<PagedResponse<EmployeeSummary>> {
    let httpParams = new HttpParams();
    if (params.search) httpParams = httpParams.set('search', params.search);
    if (params.country) httpParams = httpParams.set('country', params.country);
    if (params.department) httpParams = httpParams.set('department', params.department);
    if (params.minSalary != null) httpParams = httpParams.set('minSalary', params.minSalary.toString());
    if (params.maxSalary != null) httpParams = httpParams.set('maxSalary', params.maxSalary.toString());
    if (params.page != null) httpParams = httpParams.set('page', params.page.toString());
    if (params.size != null) httpParams = httpParams.set('size', params.size.toString());
    if (params.sortBy) httpParams = httpParams.set('sortBy', params.sortBy);
    if (params.sortDir) httpParams = httpParams.set('sortDir', params.sortDir);

    return this.http.get<PagedResponse<EmployeeSummary>>(this.baseUrl, { params: httpParams });
  }

  getEmployeeById(id: string): Observable<EmployeeDetail> {
    return this.http.get<EmployeeDetail>(`${this.baseUrl}/${id}`);
  }

  createEmployee(payload: CreateEmployeeRequest): Observable<EmployeeDetail> {
    return this.http.post<EmployeeDetail>(this.baseUrl, payload);
  }

  updateEmployeeProfile(id: string, payload: UpdateEmployeeProfileRequest): Observable<EmployeeDetail> {
    return this.http.put<EmployeeDetail>(`${this.baseUrl}/${id}`, payload);
  }

  deleteEmployee(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updateSalary(id: string, payload: SalaryUpdateRequest): Observable<EmployeeDetail> {
    return this.http.put<EmployeeDetail>(`${this.baseUrl}/${id}/salary`, payload);
  }

  getAuditLogs(id: string): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.baseUrl}/${id}/audit-logs`);
  }

  getRecentAuditLogs(): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.baseUrl}/recent-audit-logs`);
  }

  getExportCsvUrl(params: {
    search?: string;
    country?: string;
    department?: string;
    minSalary?: number;
    maxSalary?: number;
  }): string {
    const query = new URLSearchParams();
    if (params.search) query.set('search', params.search);
    if (params.country) query.set('country', params.country);
    if (params.department) query.set('department', params.department);
    if (params.minSalary != null) query.set('minSalary', params.minSalary.toString());
    if (params.maxSalary != null) query.set('maxSalary', params.maxSalary.toString());
    return `${this.baseUrl}/export/csv?${query.toString()}`;
  }
}
