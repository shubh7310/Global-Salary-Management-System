import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AdjustmentSimulationRequest,
  AdjustmentSimulationResponse,
  AnalyticsOverview,
  CountryAnalytics,
  DepartmentAnalytics
} from '../models/analytics.model';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly baseUrl = typeof window !== 'undefined' && window.location.port === '4200'
    ? 'http://localhost:8080/api/v1/analytics'
    : '/api/v1/analytics';

  constructor(private http: HttpClient) {}

  getOverview(country?: string, department?: string): Observable<AnalyticsOverview> {
    let params = new HttpParams();
    if (country) params = params.set('country', country);
    if (department) params = params.set('department', department);
    return this.http.get<AnalyticsOverview>(`${this.baseUrl}/overview`, { params });
  }

  getDepartmentAnalytics(): Observable<DepartmentAnalytics[]> {
    return this.http.get<DepartmentAnalytics[]>(`${this.baseUrl}/by-department`);
  }

  getCountryAnalytics(): Observable<CountryAnalytics[]> {
    return this.http.get<CountryAnalytics[]>(`${this.baseUrl}/by-country`);
  }

  simulateAdjustment(request: AdjustmentSimulationRequest): Observable<AdjustmentSimulationResponse> {
    return this.http.post<AdjustmentSimulationResponse>(`${this.baseUrl}/simulate-adjustment`, request);
  }
}

