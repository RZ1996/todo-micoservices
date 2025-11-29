import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../services/user-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  // POZOR: používáme emailAddress kvůli backendu
  emailAddress = '';
  password = '';
  loading = false;
  errorMessage: string | null = null;

  constructor(private users: UserService, private router: Router) {}

  onSubmit() {
    this.errorMessage = null;

    if (!this.emailAddress.trim() || !this.password.trim()) {
      this.errorMessage = 'Email and password are required';
      return;
    }

    this.loading = true;
    this.users.login({ emailAddress: this.emailAddress.trim(), password: this.password }).subscribe({
      next: (user) => {
        this.loading = false;
        if (user && user.id) {
          this.router.navigate(['/tasks']);
        } else {
          this.errorMessage = 'Invalid email or password';
        }
      },
      error: (err) => {
        this.loading = false;
        // ukaž hlášku ze serveru, pokud ji posílá
        this.errorMessage =
          err?.error?.message ||
          (typeof err?.error === 'string' ? err.error : null) ||
          (err.status === 401 ? 'Invalid email or password' :
           err.status === 400 ? 'Bad request (check field names: emailAddress/password)' :
           'Login failed');
        // console.error('Login error', err);
      }
    });
  }
}
