import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router, ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { TeacherService } from '../../../../core/service/teacher.service';

import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let mockSessionService: any;
  let mockSessionApiService: any;
  let mockTeacherService: any;
  let router: Router;
  let matSnackBar: MatSnackBar;

  beforeEach(async () => {
    mockSessionService = { sessionInformation: { admin: true } };
    mockSessionApiService = {
      create: jest.fn().mockReturnValue(of({})),
      update: jest.fn().mockReturnValue(of({})),
      detail: jest.fn().mockReturnValue(of({
        id: '1',
        name: 'Yoga 101',
        date: new Date().toISOString(),
        teacher_id: 't1',
        description: 'desc'
      }))
    };
    mockTeacherService = {
      all: jest.fn().mockReturnValue(of([]))
    };

    const mockRouter = { navigate: jest.fn(), url: '/sessions' };
    const mockMatSnackBar = { open: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        FormComponent,
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: Router, useValue: mockRouter },
        // Provide a minimal ActivatedRoute mock for inject() usage in the standalone component
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: (_: string) => null } } } },
        { provide: MatSnackBar, useValue: mockMatSnackBar }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize sessionForm when not update', () => {
    // default beforeEach has admin=true and url does not include 'update'
    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeDefined();
    expect(component.sessionForm?.get('name')?.value).toBe('');
  });

  it('should redirect non-admin users to /sessions', () => {
    // Arrange
    mockSessionService.sessionInformation.admin = false;
    const navigateSpy = jest.spyOn(router, 'navigate');

    // Act
    component.ngOnInit();

    // Assert
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should set onUpdate true and populate form when url includes update', () => {
    // Arrange
    const navigateSpy = jest.spyOn(router, 'navigate');
    // simulate update url
    (router as any).url = '/sessions/update/1';
    mockSessionApiService.detail.mockReturnValue(of({
      id: '1',
      name: 'Yoga Advanced',
      date: new Date('2024-01-01').toISOString(),
      teacher_id: 't2',
      description: 'd'
    }));

    // Act
    component.ngOnInit();

    // Assert
    expect(component.onUpdate).toBe(true);
    expect(component.sessionForm?.get('name')?.value).toBe('Yoga Advanced');
    expect(component.sessionForm?.get('teacher_id')?.value).toBe('t2');
  });

  it('should call create on submit when not update', () => {
    // Arrange
    component.onUpdate = false;
    component.sessionForm?.patchValue({
      name: 'New',
      date: new Date().toISOString().split('T')[0],
      teacher_id: 't1',
      description: 'desc'
    });
    const createSpy = mockSessionApiService.create;
    // avoid opening real MatSnackBar/overlay in jsdom by stubbing the private exitPage called in subscribe
    const exitSpy = jest.spyOn(component as any, 'exitPage').mockImplementation(() => {});

    // Act
    component.submit();

    // Assert
    expect(createSpy).toHaveBeenCalled();
    expect(exitSpy).toHaveBeenCalledWith('Session created !');
  });

  it('should call update on submit when update', () => {
    // Arrange
    component.onUpdate = true;
    (component as any).id = '1';
    component.sessionForm?.patchValue({
      name: 'Updated',
      date: new Date().toISOString().split('T')[0],
      teacher_id: 't1',
      description: 'desc'
    });
    const updateSpy = mockSessionApiService.update;
    // avoid opening real MatSnackBar/overlay in jsdom by stubbing the private exitPage called in subscribe
    const exitSpy = jest.spyOn(component as any, 'exitPage').mockImplementation(() => {});

    // Act
    component.submit();

    // Assert
    expect(updateSpy).toHaveBeenCalledWith('1', expect.any(Object));
    expect(exitSpy).toHaveBeenCalledWith('Session updated !');
  });
});
