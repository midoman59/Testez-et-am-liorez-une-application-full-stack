import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { LoginRequest } from '../models/loginRequest.interface';
import { RegisterRequest } from '../models/registerRequest.interface';
import { SessionInformation } from '../models/sessionInformation.interface';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call register endpoint', () => {
    const request: RegisterRequest = {
      email: 'new@test.com',
      firstName: 'New',
      lastName: 'User',
      password: 'password123'
    };

    service.register(request).subscribe();

    const req = httpMock.expectOne('/api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(null);
  });

  it('should call login endpoint', () => {
    const request: LoginRequest = {
      email: 'admin@test.com',
      password: 'password123'
    };
    const response: SessionInformation = {
      token: 'jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'admin@test.com',
      firstName: 'Admin',
      lastName: 'User',
      admin: true
    };

    service.login(request).subscribe((result) => {
      expect(result).toEqual(response);
    });

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(response);
  });
});

