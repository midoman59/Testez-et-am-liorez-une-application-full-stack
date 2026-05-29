import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { AuthGuard } from '../../guards/auth.guard';
import { SessionService } from './session.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  const router = { navigate: jest.fn() };
  const sessionService = { isLogged: false };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: router },
        { provide: SessionService, useValue: sessionService }
      ]
    });
    guard = TestBed.inject(AuthGuard);
    router.navigate.mockClear();
  });

  it('should redirect to login when user is not logged', () => {
    sessionService.isLogged = false;

    expect(guard.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['login']);
  });

  it('should allow access when user is logged', () => {
    sessionService.isLogged = true;

    expect(guard.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });
});

