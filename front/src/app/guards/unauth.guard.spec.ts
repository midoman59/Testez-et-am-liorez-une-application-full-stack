import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../core/service/session.service';

describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  const router = { navigate: jest.fn() };
  const sessionService = { isLogged: false };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        { provide: Router, useValue: router },
        { provide: SessionService, useValue: sessionService }
      ]
    });
    guard = TestBed.inject(UnauthGuard);
    router.navigate.mockClear();
  });

  it('should redirect to rentals when user is logged', () => {
    sessionService.isLogged = true;

    expect(guard.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['rentals']);
  });

  it('should allow access when user is not logged', () => {
    sessionService.isLogged = false;

    expect(guard.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });
});

