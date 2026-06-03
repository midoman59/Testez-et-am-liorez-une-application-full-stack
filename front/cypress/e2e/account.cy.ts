const adminUser = {
  token: 'token',
  type: 'Bearer',
  id: 1,
  username: 'admin',
  firstName: 'Admin',
  lastName: 'User',
  admin: true
}

const standardUser = {
  token: 'token',
  type: 'Bearer',
  id: 2,
  username: 'user',
  firstName: 'Regular',
  lastName: 'User',
  admin: false
}

const adminDetail = {
  id: 1,
  email: 'admin@yoga.com',
  firstName: 'Admin',
  lastName: 'User',
  admin: true,
  password: 'hidden',
  createdAt: '2026-01-01T00:00:00.000Z',
  updatedAt: '2026-01-02T00:00:00.000Z'
}

const userDetail = {
  id: 2,
  email: 'user@yoga.com',
  firstName: 'Regular',
  lastName: 'User',
  admin: false,
  password: 'hidden',
  createdAt: '2026-01-01T00:00:00.000Z',
  updatedAt: '2026-01-02T00:00:00.000Z'
}

function loginAs(user: typeof adminUser) {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: user
  }).as('loginRequest')

  cy.intercept('GET', '/api/session', {
    statusCode: 200,
    body: []
  }).as('sessionsRequest')

  cy.visit('/login')
  cy.get('input[formControlName="email"]').type(`${user.username}@yoga.com`)
  cy.get('input[formControlName="password"]').type('password123')
  cy.get('button[type="submit"]').click()

  cy.wait('@loginRequest')
  cy.wait('@sessionsRequest')
  cy.url().should('include', '/sessions')
}

describe('Account and Logout E2E', () => {
  it('affiche les informations du compte utilisateur', () => {
    loginAs(adminUser)

    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: adminDetail
    }).as('userRequest')

    cy.contains('span', 'Account').click()
    cy.wait('@userRequest')

    cy.url().should('include', '/me')
    cy.contains('User information').should('be.visible')
    cy.contains('Email: admin@yoga.com').should('be.visible')
    cy.contains('You are admin').should('be.visible')
  })

  it('permet a un utilisateur non-admin de supprimer son compte', () => {
    loginAs(standardUser)

    cy.intercept('GET', '/api/user/2', {
      statusCode: 200,
      body: userDetail
    }).as('userRequest')

    cy.intercept('DELETE', '/api/user/2', {
      statusCode: 200,
      body: {}
    }).as('deleteRequest')

    cy.contains('span', 'Account').click()
    cy.wait('@userRequest')
    cy.contains('Delete my account:').should('be.visible')

    cy.contains('button', 'Detail').click()
    cy.wait('@deleteRequest')

    cy.url().should('include', '/login')
    cy.contains('Login').should('be.visible')
  })

  it('deconnecte l utilisateur quand il clique sur Logout', () => {
    loginAs(adminUser)

    cy.contains('span', 'Logout').click()

    cy.url().should('include', '/login')
    cy.contains('a', 'Login').should('be.visible')
    cy.contains('a', 'Register').should('be.visible')
  })
})

