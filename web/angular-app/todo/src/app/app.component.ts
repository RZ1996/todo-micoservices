import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { UserService } from './services/user-service.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit, OnDestroy {
  private sub?: Subscription;
  currentUrl = '/';

  constructor(private router: Router, private users: UserService) {}

  ngOnInit(): void {
    // sleduj aktuální URL kvůli přepínání položek navbaru
    this.sub = this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => this.currentUrl = e.urlAfterRedirects || this.router.url);
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  // veřejné (intro) stránky – jen Login/Register (bez Home)
  isPublicPage(): boolean {
    // ošetří i query stringy
    return this.currentUrl === '/' ||
           this.currentUrl.startsWith('/?') ||
           this.currentUrl.startsWith('/login') ||
           this.currentUrl.startsWith('/register');
  }

  isLoggedIn(): boolean { return this.users.isLoggedIn(); }

  logout(): void {
    this.users.logout();
    this.router.navigate(['/']); // po odhlášení domů
  }

  // programatický přechod na Account
  goAccount(): void {
    if (!this.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { r: 'account' } });
      return;
    }
    this.router.navigate(['/account']);
  }
}
