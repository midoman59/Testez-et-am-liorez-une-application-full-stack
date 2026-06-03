describe('Login E2E', () => {
  beforeEach(() => {
    cy.visit('/login')
  })

  it('affiche le formulaire et bloque submit si les champs sont invalides', () => {
    cy.contains('mat-card-title', 'Login').should('be.visible')
    cy.get('button[type="submit"]').should('be.disabled')

    cy.get('input[formControlName="email"]').type('email-invalide')
    cy.get('input[formControlName="password"]').type('ab')
    cy.get('button[type="submit"]').should('be.disabled')

    cy.get('input[formControlName="email"]').clear().type('user@yoga.com')
    cy.get('input[formControlName="password"]').clear().type('password123')
    cy.get('button[type="submit"]').should('not.be.disabled')
  })

  it('connecte un utilisateur valide et redirige vers /sessions', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      }
    }).as('loginRequest')

    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: []
    }).as('sessionsRequest')

    cy.get('input[formControlName="email"]').type('yoga@studio.com')
    cy.get('input[formControlName="password"]').type('test!1234')
    cy.get('button[type="submit"]').click()

    cy.wait('@loginRequest')
    cy.wait('@sessionsRequest')
    cy.url().should('include', '/sessions')
    cy.contains('Rentals available').should('be.visible')
  })

  it('affiche une erreur si login/password sont incorrects', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Unauthorized' }
    }).as('loginError')

    cy.get('input[formControlName="email"]').type('bad@yoga.com')
    cy.get('input[formControlName="password"]').type('wrong-password')
    cy.get('button[type="submit"]').click()

    cy.wait('@loginError')
    cy.url().should('include', '/login')
    cy.contains('An error occurred').should('be.visible')
  })
})
