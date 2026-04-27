export interface PluggyAccountDTO {
  id: number;
  pluggyAccountId: string;
  name: string;
  type: string;
  subtype: string | null;
  currencyCode: string;
  balance: number;
  updatedAt: string;
}

export interface PluggyTransactionDTO {
  id: number;
  pluggyTransactionId: string;
  date: string;
  description: string;
  amount: number;
  currencyCode: string;
  category: string | null;
  status: string;
  type: string;
  importedAt: string;
  accountId: number;
  accountName: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ContaTransactionQueryParams {
  page?: number;
  size?: number;
  sort?: string | string[];
}
