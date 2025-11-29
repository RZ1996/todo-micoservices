import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../environments/environment';

export interface User {
  id: number;
  name: string;
  surname: string;
  email?: string;         // pro případ, že FE někde používá "email"
  emailAddress?: string;  // backend používá emailAddress
  password?: string;      // heslo backend většinou nevrací
}

export type UserDTO = Omit<User, 'password'>;

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly base = environment.userServiceUrl;
  private currentUserSubject = new BehaviorSubject<UserDTO | null>(this.readUser());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  /** Registrace */
  create(user: User): Observable<UserDTO> {
    return this.http.post<UserDTO>(`${this.base}/users`, user).pipe(
      tap((u) => this.storeUser(u)) // pokud nechceš autologin po registraci, tap smaž
    );
  }

  /** Login – backend očekává emailAddress */
  login(payload: { email?: string; emailAddress?: string; password: string }): Observable<UserDTO> {
    const body = {
      emailAddress: payload.emailAddress ?? payload.email, // převedeme případné "email" na "emailAddress"
      password: payload.password
    };
    return this.http.post<UserDTO>(`${this.base}/login`, body).pipe(
      tap((u) => this.storeUser(u))
    );
  }

  /** UPDATE profilu – PATCH /users/{id} */
  update(id: number, patch: Partial<User>): Observable<UserDTO> {
    // sjednotíme pojmenování – kdyby FE poslal "email", převedeme na "emailAddress"
    const body: any = { ...patch };
    if (body.email && !body.emailAddress) {
      body.emailAddress = body.email;
      delete body.email;
    }
    // heslo přes update profilu neposíláme (pokud nemáš separátní endpoint na změnu hesla)
    delete body.password;

    return this.http.patch<UserDTO>(`${this.base}/users/${id}`, body).pipe(
      tap((u) => {
        // pokud updatuješ právě přihlášeného uživatele, ulož nové hodnoty
        const me = this.currentUserSubject.value;
        if (me && me.id === id) this.storeUser(u);
      })
    );
  }

  /** Mazání účtu (asi teď nepoužiješ) */
  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/users/${id}`);
  }

  /** Odhlášení */
  logout(): void {
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  /** Pomocné */
  isLoggedIn(): boolean {
    return !!localStorage.getItem('user');
  }

  getCurrentUser(): UserDTO | null {
    return this.currentUserSubject.value;
  }

  private storeUser(user: UserDTO) {
    // sjednotíme email do obou polí, aby FE mohl používat, co se mu hodí
    const normalized: UserDTO = {
      ...user,
      email: (user as any).email ?? (user as any).emailAddress,
      emailAddress: (user as any).emailAddress ?? (user as any).email
    };
    localStorage.setItem('user', JSON.stringify(normalized));
    this.currentUserSubject.next(normalized);
  }

  private readUser(): UserDTO | null {
    const raw = localStorage.getItem('user');
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserDTO;
    } catch {
      return null;
    }
  }
}
