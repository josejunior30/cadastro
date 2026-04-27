// file: front-end/cadastro/src/app/services/conta.service.ts

import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';

import { environment } from '../../environments/environment';
import { PluggyAccountDTO, ContaTransactionQueryParams, PageResponse, PluggyTransactionDTO } from '../models/conta';

@Injectable({
  providedIn: 'root',
})
export class ContaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/pluggy`;

  findMyAccounts(): Observable<PluggyAccountDTO[]> {
    const url = `${this.apiUrl}/accounts`;

    console.log('[ContaService] GET', url);

    return this.http.get<PluggyAccountDTO[]>(url).pipe(
      tap((response: any[]) => {
        console.log('[ContaService] Resposta bruta /accounts:', response);
        console.log('[ContaService] Quantidade de contas recebidas:', response?.length ?? 0);

        if (Array.isArray(response)) {
          console.table(
            response.map((account) => ({
              id: account.id,
              name: account.name,
              type: account.type,
              subtype: account.subtype,
              balance: account.balance,
              currencyCode: account.currencyCode,
            }))
          );
        }
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('[ContaService] Erro ao buscar /accounts', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url,
          error: error.error,
        });

        return throwError(() => error);
      })
    );
  }

  findMyTransactionsByAccount(
    accountId: number,
    queryParams: ContaTransactionQueryParams = {}
  ): Observable<PageResponse<PluggyTransactionDTO>> {
    let params = new HttpParams();

    if (queryParams.page !== undefined) {
      params = params.set('page', queryParams.page);
    }

    if (queryParams.size !== undefined) {
      params = params.set('size', queryParams.size);
    }

    if (queryParams.sort) {
      const sortValues = Array.isArray(queryParams.sort)
        ? queryParams.sort
        : [queryParams.sort];

      for (const sortValue of sortValues) {
        params = params.append('sort', sortValue);
      }
    }

    const url = `${this.apiUrl}/accounts/${accountId}/transactions`;

    console.log('[ContaService] GET', url, 'params:', params.toString());

    return this.http.get<PageResponse<PluggyTransactionDTO>>(url, { params }).pipe(
      tap((response) => {
        console.log('[ContaService] Resposta bruta /transactions:', response);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('[ContaService] Erro ao buscar /transactions', {
          accountId,
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url,
          error: error.error,
        });

        return throwError(() => error);
      })
    );
  }
}
