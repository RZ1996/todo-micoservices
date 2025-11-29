import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { UserService, UserDTO } from '../services/user-service.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html'
})
export class AccountComponent implements OnInit {
  me: UserDTO | null = null;

  // form fields
  name = '';
  surname = '';
  email = '';

  saving = false;
  error: string | null = null;
  success: string | null = null;

  constructor(private users: UserService, private router: Router) {}

  ngOnInit(): void {
    // bez guardu: ručně ošetříme nepřihlášeného
    if (!this.users.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { r: 'account' } });
      return;
    }

    this.me = this.users.getCurrentUser();
    if (this.me) {
      this.name = (this.me as any).name ?? '';
      this.surname = (this.me as any).surname ?? '';
      this.email = (this.me as any).email ?? (this.me as any).emailAddress ?? '';
    }
  }

  save(): void {
    if (!this.me) { this.error = 'Not logged in'; return; }
    this.saving = true;
    this.error = null; this.success = null;

    const patch: any = {
      name: this.name?.trim(),
      surname: this.surname?.trim()
    };
    if ('email' in (this.me as any)) patch.email = this.email?.trim();
    else patch.emailAddress = this.email?.trim();

    this.users.update(this.me.id, patch).subscribe({
      next: () => {
        this.saving = false;
        this.success = 'Profile updated';
      },
      error: (err: HttpErrorResponse) => {
        this.saving = false;
        this.error =
          err?.error?.message ??
          (typeof err?.error === 'string' ? err.error : `Update failed (HTTP ${err?.status ?? '?'})`);
      }
    });
  }

  logout(): void {
    this.users.logout();
    this.router.navigate(['/']);
  }
}
