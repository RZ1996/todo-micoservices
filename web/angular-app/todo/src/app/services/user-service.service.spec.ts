import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { UserService } from './user-service.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService],
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('login() should POST to /login with {emailAddress,password} and store user', () => {
    const dummyUser = {
      id: 1,
      name: 'Ada',
      surname: 'Lovelace',
      emailAddress: 'a@b.com'
    };

    service.login({ emailAddress: 'a@b.com', password: 'x' }).subscribe(u => {
      expect(u).toEqual(dummyUser);
      expect(localStorage.getItem('user')).toBeTruthy();
      const stored = JSON.parse(localStorage.getItem('user') as string);
      expect(stored.emailAddress).toBe('a@b.com');
    });

    const req = httpMock.expectOne(r => r.method === 'POST' && r.url.endsWith('/login'));
    expect(req.request.body).toEqual({ emailAddress: 'a@b.com', password: 'x' });

    req.flush(dummyUser);
  });
});
