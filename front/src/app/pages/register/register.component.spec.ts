import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../core/service/auth.service';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule
      ],
      providers: [AuthService]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  // ============ TESTS UNITAIRES - CRÉATION ============
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with onError=false', () => {
    expect(component.onError).toBe(false);
  });

  // ============ TESTS UNITAIRES - INITIALISATION FORM ============
  it('should initialize form with empty fields', () => {
    expect(component.form.get('email')?.value).toBe('');
    expect(component.form.get('firstName')?.value).toBe('');
    expect(component.form.get('lastName')?.value).toBe('');
    expect(component.form.get('password')?.value).toBe('');
  });

  it('should have invalid form when empty', () => {
    expect(component.form.invalid).toBe(true);
  });

  // ============ TESTS UNITAIRES - VALIDATION EMAIL ============
  it('should have invalid email when not provided', () => {
    const emailControl = component.form.get('email');
    expect(emailControl?.hasError('required')).toBe(true);
  });

  it('should have invalid email when format is incorrect', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.hasError('email')).toBe(true);
  });

  it('should have valid email when correct format', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('test@example.com');
    expect(emailControl?.hasError('email')).toBe(false);
  });

  // ============ TESTS UNITAIRES - VALIDATION FIRSTNAME ============
  it('should have invalid firstName when not provided', () => {
    const firstNameControl = component.form.get('firstName');
    expect(firstNameControl?.hasError('required')).toBe(true);
  });

  it('should have invalid firstName when less than 3 characters', () => {
    const firstNameControl = component.form.get('firstName');
    firstNameControl?.setValue('ab');
    expect(firstNameControl?.hasError('minlength')).toBe(true);
  });

  it('should have invalid firstName when more than 20 characters', () => {
    const firstNameControl = component.form.get('firstName');
    firstNameControl?.setValue('a'.repeat(21));
    expect(firstNameControl?.hasError('maxlength')).toBe(true);
  });

  it('should have valid firstName when between 3 and 20 characters', () => {
    const firstNameControl = component.form.get('firstName');
    firstNameControl?.setValue('John');
    expect(firstNameControl?.valid).toBe(true);
  });

  // ============ TESTS UNITAIRES - VALIDATION LASTNAME ============
  it('should have invalid lastName when not provided', () => {
    const lastNameControl = component.form.get('lastName');
    expect(lastNameControl?.hasError('required')).toBe(true);
  });

  it('should have invalid lastName when less than 3 characters', () => {
    const lastNameControl = component.form.get('lastName');
    lastNameControl?.setValue('ab');
    expect(lastNameControl?.hasError('minlength')).toBe(true);
  });

  it('should have invalid lastName when more than 20 characters', () => {
    const lastNameControl = component.form.get('lastName');
    lastNameControl?.setValue('a'.repeat(21));
    expect(lastNameControl?.hasError('maxlength')).toBe(true);
  });

  it('should have valid lastName when between 3 and 20 characters', () => {
    const lastNameControl = component.form.get('lastName');
    lastNameControl?.setValue('Doe');
    expect(lastNameControl?.valid).toBe(true);
  });

  // ============ TESTS UNITAIRES - VALIDATION PASSWORD ============
  it('should have invalid password when not provided', () => {
    const passwordControl = component.form.get('password');
    expect(passwordControl?.hasError('required')).toBe(true);
  });

  it('should have invalid password when less than 3 characters', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('ab');
    expect(passwordControl?.hasError('minlength')).toBe(true);
  });

  it('should have invalid password when more than 40 characters', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('a'.repeat(41));
    expect(passwordControl?.hasError('maxlength')).toBe(true);
  });

  it('should have valid password when between 3 and 40 characters', () => {
    const passwordControl = component.form.get('password');
    passwordControl?.setValue('password123');
    expect(passwordControl?.valid).toBe(true);
  });

  // ============ TESTS UNITAIRES - FORM COMPLET ============
  it('should have valid form when all fields are correct', () => {
    component.form.get('email')?.setValue('test@example.com');
    component.form.get('firstName')?.setValue('John');
    component.form.get('lastName')?.setValue('Doe');
    component.form.get('password')?.setValue('password123');
    expect(component.form.valid).toBe(true);
  });

  // ============ TESTS D'INTÉGRATION - SUBMIT SUCCESS ============
  it('should call register service on valid submit (INTEGRATION)', () => {
    // Arrange
    jest.spyOn(authService, 'register').mockReturnValue(of(void 0));
    jest.spyOn(router, 'navigate');

    // Act
    component.form.get('email')?.setValue('new@example.com');
    component.form.get('firstName')?.setValue('John');
    component.form.get('lastName')?.setValue('Doe');
    component.form.get('password')?.setValue('password123');
    component.submit();

    // Assert
    expect(authService.register).toHaveBeenCalledWith({
      email: 'new@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  // ============ TESTS D'INTÉGRATION - SUBMIT ERROR ============
  it('should set onError=true on register failure (INTEGRATION)', (done) => {
    // Arrange
    const registerError = new Error('Email already exists');
    jest.spyOn(authService, 'register').mockReturnValue(throwError(() => registerError));

    // Act
    component.form.get('email')?.setValue('existing@example.com');
    component.form.get('firstName')?.setValue('John');
    component.form.get('lastName')?.setValue('Doe');
    component.form.get('password')?.setValue('password123');
    component.submit();

    // Assert
    setTimeout(() => {
      expect(component.onError).toBe(true);
      done();
    }, 100);
  });

  // ============ TESTS D'INTÉGRATION - TEMPLATE ============
  it('should display error message when onError is true (INTEGRATION)', () => {
    // Arrange
    component.onError = true;
    fixture.detectChanges();

    // Act
    const errorElement = fixture.nativeElement.querySelector('.error');

    // Assert
    expect(errorElement).toBeTruthy();
    expect(errorElement.textContent).toContain('An error occurred');
  });

  it('should not display error message when onError is false (INTEGRATION)', () => {
    // Arrange
    component.onError = false;
    fixture.detectChanges();

    // Act
    const errorElement = fixture.nativeElement.querySelector('.error');

    // Assert
    expect(errorElement).toBeFalsy();
  });

  it('should disable submit button when form is invalid (INTEGRATION)', () => {
    // Arrange
    component.form.get('email')?.setValue('');
    component.form.get('firstName')?.setValue('');
    component.form.get('lastName')?.setValue('');
    component.form.get('password')?.setValue('');
    fixture.detectChanges();

    // Act
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');

    // Assert
    expect(submitButton.disabled).toBe(true);
  });

  it('should enable submit button when form is valid (INTEGRATION)', () => {
    // Arrange
    component.form.get('email')?.setValue('test@example.com');
    component.form.get('firstName')?.setValue('John');
    component.form.get('lastName')?.setValue('Doe');
    component.form.get('password')?.setValue('password123');
    fixture.detectChanges();

    // Act
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');

    // Assert
    expect(submitButton.disabled).toBe(false);
  });
});
