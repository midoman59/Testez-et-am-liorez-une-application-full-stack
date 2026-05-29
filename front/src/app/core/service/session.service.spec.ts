import { TestBed } from '@angular/core/testing';

import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should initialize as logged out', () => {
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit login state and store session information', (done) => {
    const sessionInformation = {
      token: 'jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'admin@test.com',
      firstName: 'Admin',
      lastName: 'User',
      admin: true
    };

    const values: boolean[] = [];
    const subscription = service.$isLogged().subscribe((value) => values.push(value));

    service.logIn(sessionInformation);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(sessionInformation);
    expect(values).toEqual([false, true]);

    subscription.unsubscribe();
    done();
  });

  it('should emit logout state and clear session information', (done) => {
    service.logIn({
      token: 'jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'admin@test.com',
      firstName: 'Admin',
      lastName: 'User',
      admin: true
    });

    const values: boolean[] = [];
    const subscription = service.$isLogged().subscribe((value) => values.push(value));

    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
    expect(values).toEqual([true, false]);

    subscription.unsubscribe();
    done();
  });
});
