import { HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { customJwtInterceptorFn } from './customJwtInterceptorFn';
import { SessionService } from '../core/service/session.service';

describe('customJwtInterceptorFn', () => {
  const sessionService = {
    isLogged: false,
    sessionInformation: undefined as { token: string } | undefined
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: SessionService, useValue: sessionService }]
    });
  });

  it('should add authorization header when logged in', () => {
    sessionService.isLogged = true;
    sessionService.sessionInformation = { token: 'jwt-token' };

    const request = new HttpRequest('GET', '/api/test');
    const next = jest.fn((req: HttpRequest<unknown>) => ({ request: req }));

    TestBed.runInInjectionContext(() => customJwtInterceptorFn(request, next as unknown as HttpHandlerFn));

    expect(next).toHaveBeenCalledTimes(1);
    expect(next.mock.calls[0][0].headers.get('Authorization')).toBe('Bearer jwt-token');
  });

  it('should forward request unchanged when not logged in', () => {
    sessionService.isLogged = false;
    sessionService.sessionInformation = undefined;

    const request = new HttpRequest('GET', '/api/test');
    const next = jest.fn((req: HttpRequest<unknown>) => ({ request: req }));

    TestBed.runInInjectionContext(() => customJwtInterceptorFn(request, next as unknown as HttpHandlerFn));

    expect(next).toHaveBeenCalledTimes(1);
    expect(next.mock.calls[0][0].headers.has('Authorization')).toBe(false);
  });
});

