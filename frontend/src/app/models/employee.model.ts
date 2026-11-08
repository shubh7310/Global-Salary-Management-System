export interface SalaryStructure {
  currency: string;
  baseSalary: number;
  housingAllowance: number;
  transportAllowance: number;
  performanceBonus: number;
  statutoryDeduction: number;
  grossAnnualLocal: number;
  netAnnualLocal: number;
  monthlyGrossLocal: number;
  monthlyNetLocal: number;
  grossAnnualUSD: number;
  netAnnualUSD: number;
}

export interface EmployeeSummary {
  id: string;
  employeeCode: string;
  fullName: string;
  department: string;
  jobTitle: string;
  country: string;
  currency: string;
  annualGrossLocal: number;
  annualNetLocal: number;
  annualGrossUSD: number;
  annualNetUSD: number;
}

export interface EmployeeDetail {
  id: string;
  employeeCode: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  department: string;
  departmentName: string;
  jobTitle: string;
  country: string;
  countryName: string;
  hireDate: string;
  salaryStructure: SalaryStructure;
}

export interface CreateEmployeeRequest {
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  country: string;
  hireDate?: string;
  currency: string;
  baseSalary: number;
  housingAllowance: number;
  transportAllowance: number;
  performanceBonus: number;
  statutoryDeduction: number;
}

export interface UpdateEmployeeProfileRequest {
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  country: string;
}

export interface SalaryUpdateRequest {
  currency: string;
  baseSalary: number;
  housingAllowance: number;
  transportAllowance: number;
  performanceBonus: number;
  statutoryDeduction: number;
  reason: string;
  modifiedBy?: string;
}

export interface PagedResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
}

export interface AuditLog {
  id: string;
  employeeId: string;
  employeeCode: string;
  employeeName: string;
  currency: string;
  previousGrossLocal: number;
  newGrossLocal: number;
  previousGrossUSD: number;
  newGrossUSD: number;
  reason: string;
  modifiedBy: string;
  modifiedAt: string;
}
