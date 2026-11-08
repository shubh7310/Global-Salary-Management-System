export interface AnalyticsOverview {
  totalHeadcount: number;
  totalPayrollUSD: number;
  averageSalaryUSD: number;
  medianSalaryUSD: number;
  minSalaryUSD: number;
  maxSalaryUSD: number;
}

export interface DepartmentAnalytics {
  department: string;
  departmentName: string;
  headcount: number;
  totalPayrollUSD: number;
  avgSalaryUSD: number;
  minSalaryUSD: number;
  maxSalaryUSD: number;
  payrollPercentage: number;
}

export interface CountryAnalytics {
  country: string;
  countryName: string;
  currency: string;
  headcount: number;
  totalPayrollLocal: number;
  totalPayrollUSD: number;
  avgSalaryUSD: number;
  minSalaryUSD: number;
  maxSalaryUSD: number;
  payrollPercentage: number;
}

export interface AdjustmentSimulationRequest {
  country?: string | null;
  department?: string | null;
  percentageChange: number;
}

export interface AdjustmentSimulationResponse {
  filterCountry?: string | null;
  filterDepartment?: string | null;
  percentageChange: number;
  affectedEmployees: number;
  currency?: string | null;
  currentTotalPayrollLocal?: number | null;
  projectedTotalPayrollLocal?: number | null;
  payrollDeltaLocal?: number | null;
  currentTotalPayrollUSD: number;
  projectedTotalPayrollUSD: number;
  payrollDeltaUSD: number;
  currentAvgSalaryUSD: number;
  projectedAvgSalaryUSD: number;
}
