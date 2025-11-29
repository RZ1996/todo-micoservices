import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { UserService, UserDTO } from '../services/user-service.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  name = '';
  surname = '';
  emailAddress = '';
  password = '';

  loading = false;
  errorMessage: string | null = null;

  constructor(private users: UserService, private router: Router) {}

  onSubmit(): void {
    this.errorMessage = null;

    const name = this.name.trim();
    const surname = this.surname.trim();
    const emailAddress = this.emailAddress.trim();
    const password = this.password;

    if (!name || !surname || !emailAddress || !password) {
      this.errorMessage = 'All fields are required';
      return;
    }

    this.loading = true;

    // payload – držíme se názvů, které očekává backend (emailAddress)
    const payload: { name: string; surname: string; emailAddress: string; password: string } = {
      name,
      surname,
      emailAddress,
      password
    };

    this.users.create(payload as any).subscribe({
      next: (user: UserDTO) => {
        this.loading = false;
        // create() v UserService dělá autologin (tap -> storeUser),
        // tady jen po úspěchu přesměrujeme
        if (user && user.id) {
          this.router.navigate(['/tasks']);
        } else {
          this.errorMessage = 'Registration failed';
        }
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message ??
          (typeof err?.error === 'string' ? err.error : null) ??
          'Registration failed';
      }
    });
  }
}
