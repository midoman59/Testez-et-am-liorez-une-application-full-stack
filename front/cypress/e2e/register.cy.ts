describe('Register E2E', () => {
  beforeEach(() => {
    cy.visit('/register')
  })

  it('affiche le formulaire et bloque submit si champs invalides', () => {
    cy.contains('mat-card-title', 'Register').should('be.visible')
    cy.get('button[type="submit"]').should('be.disabled')

    cy.get('input[formControlName="firstName"]').type('ab')
    cy.get('input[formControlName="lastName"]').type('cd')
    cy.get('input[formControlName="email"]').type('email-invalide')
    cy.get('input[formControlName="password"]').type('12')

    cy.get('button[type="submit"]').should('be.disabled')
  })

  it('cree un compte et redirige vers /login', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {}
    }).as('registerRequest')

    cy.get('input[formControlName="firstName"]').type('Marie')
    cy.get('input[formControlName="lastName"]').type('Dupont')
    cy.get('input[formControlName="email"]').type('marie.dupont@yoga.com')
    cy.get('input[formControlName="password"]').type('password123')
    cy.get('button[type="submit"]').click()

    cy.wait('@registerRequest')
    cy.url().should('include', '/login')
  })

  it('affiche une erreur si la creation de compte echoue', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' }
    }).as('registerError')

    cy.get('input[formControlName="firstName"]').type('Marie')
    cy.get('input[formControlName="lastName"]').type('Dupont')
    cy.get('input[formControlName="email"]').type('marie.dupont@yoga.com')
    cy.get('input[formControlName="password"]').type('password123')
    cy.get('button[type="submit"]').click()

    cy.wait('@registerError')
    cy.url().should('include', '/register')
    cy.contains('An error occurred').should('be.visible')
  })
})

