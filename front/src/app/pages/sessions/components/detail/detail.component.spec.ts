import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';

import { DetailComponent } from './detail.component';
import { SessionService } from '../../../../core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { TeacherService } from '../../../../core/service/teacher.service';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  let mockSessionService: any;
  let mockSessionApiService: any;
  let mockTeacherService: any;
  let router: Router;
  let matSnackBar: MatSnackBar;

  beforeEach(async () => {
    mockSessionService = { sessionInformation: { id: 10, admin: true } };

    const sampleSession = {
      id: '1',
      name: 'Yoga',
      date: new Date().toISOString(),
      teacher_id: 't1',
      description: 'desc',
      users: [10]
    } as any;

    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of(sampleSession)),
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({}))
    };

    mockTeacherService = {
      detail: jest.fn().mockReturnValue(of({ id: 't1', name: 'Teacher' }))
    };

    const mockRouter = { navigate: jest.fn() };
    const mockMatSnackBar = { open: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [DetailComponent, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: (_: string) => '1' } } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
    // ensure component uses our local mocks instead of any real Material overlay implementation
    (component as any).matSnackBar = mockMatSnackBar;
    (component as any).router = mockRouter;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set isAdmin from session service', () => {
    expect(component.isAdmin).toBe(true);
  });

  it('should set isParticipate when session users contains current user', (done) => {
    // session$ is an observable that emits from the mock detail; subscribe to ensure tap executed
    component.session$.subscribe((s) => {
      expect(component.isParticipate).toBe(true);
      done();
    });
  });

  it('should call delete and navigate when delete is called', () => {
    const deleteSpy = mockSessionApiService.delete;
    const snackSpy = jest.spyOn(matSnackBar, 'open');
    const navSpy = jest.spyOn(router, 'navigate');

    component.delete();

    expect(deleteSpy).toHaveBeenCalledWith('1');
    expect(snackSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(navSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should call participate and trigger refresh', () => {
    const partSpy = mockSessionApiService.participate;
    const nextSpy = jest.spyOn((component as any).refreshSession$, 'next');

    component.participate();

    expect(partSpy).toHaveBeenCalledWith('1', '10');
    expect(nextSpy).toHaveBeenCalled();
  });

  it('should call unParticipate and trigger refresh', () => {
    const unPartSpy = mockSessionApiService.unParticipate;
    const nextSpy = jest.spyOn((component as any).refreshSession$, 'next');

    component.unParticipate();

    expect(unPartSpy).toHaveBeenCalledWith('1', '10');
    expect(nextSpy).toHaveBeenCalled();
  });

});
