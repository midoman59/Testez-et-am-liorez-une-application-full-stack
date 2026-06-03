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

const baseSession = {
  id: 1,
  name: 'Morning Flow',
  description: 'Yoga doux',
  date: '2026-06-01T00:00:00.000Z',
  teacher_id: 1,
  users: [1],
  createdAt: '2026-06-01T00:00:00.000Z',
  updatedAt: '2026-06-01T00:00:00.000Z'
}

const teacher = {
  id: 1,
  firstName: 'Alice',
  lastName: 'Martin',
  createdAt: '2026-01-01T00:00:00.000Z',
  updatedAt: '2026-01-01T00:00:00.000Z'
}

function loginAs(user: typeof adminUser, sessions = [baseSession]) {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: user
  }).as('loginRequest')

  cy.intercept('GET', '/api/session', {
    statusCode: 200,
    body: sessions
  }).as('sessionsRequest')

  cy.visit('/login')
  cy.get('input[formControlName="email"]').type(`${user.username}@yoga.com`)
  cy.get('input[formControlName="password"]').type('password123')
  cy.get('button[type="submit"]').click()

  cy.wait('@loginRequest')
  cy.wait('@sessionsRequest')
  cy.url().should('include', '/sessions')
}

describe('Sessions E2E', () => {
  it('affiche la liste et les boutons admin (Create, Detail, Edit)', () => {
    loginAs(adminUser)

    cy.contains('Rentals available').should('be.visible')
    cy.contains('button', 'Create').should('be.visible')
    cy.contains('button', 'Detail').should('be.visible')
    cy.contains('button', 'Edit').should('be.visible')
  })

  it('cache les actions admin pour un utilisateur non-admin', () => {
    loginAs(standardUser)

    cy.contains('button', 'Create').should('not.exist')
    cy.contains('button', 'Edit').should('not.exist')
    cy.contains('button', 'Detail').should('be.visible')
  })

  it('affiche le detail session et permet la suppression pour admin', () => {
    loginAs(adminUser)

    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: baseSession
    }).as('detailRequest')

    cy.intercept('GET', '/api/teacher/1', {
      statusCode: 200,
      body: teacher
    }).as('teacherRequest')

    cy.contains('button', 'Detail').click()
    cy.wait('@detailRequest')
    cy.wait('@teacherRequest')

    cy.url().should('include', '/sessions/detail/1')
    cy.contains('Delete').should('be.visible')

    cy.intercept('DELETE', '/api/session/1', {
      statusCode: 200,
      body: {}
    }).as('deleteRequest')

    cy.contains('button', 'Delete').click()
    cy.wait('@deleteRequest')

    cy.url().should('include', '/sessions')
    cy.contains('Session deleted !').should('be.visible')
  })

  it('permet a un non-admin de participer a une session', () => {
    loginAs(standardUser)

    let detailCall = 0
    cy.intercept('GET', '/api/session/1', (req) => {
      detailCall += 1
      req.reply({
        statusCode: 200,
        body: {
          ...baseSession,
          users: detailCall === 1 ? [1] : [1, 2]
        }
      })
    }).as('detailRequest')

    cy.intercept('GET', '/api/teacher/1', {
      statusCode: 200,
      body: teacher
    }).as('teacherRequest')

    cy.intercept('POST', '/api/session/1/participate/2', {
      statusCode: 200,
      body: {}
    }).as('participateRequest')

    cy.contains('button', 'Detail').click()
    cy.wait('@detailRequest')
    cy.wait('@teacherRequest')

    cy.contains('button', 'Participate').should('be.visible').click()
    cy.wait('@participateRequest')
    cy.wait('@detailRequest')

    cy.contains('button', 'Do not participate').should('be.visible')
  })

  it('cree une session depuis le formulaire admin', () => {
    loginAs(adminUser)

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [teacher]
    }).as('teachersRequest')

    cy.intercept('POST', '/api/session', {
      statusCode: 200,
      body: {
        ...baseSession,
        id: 2,
        name: 'Nouvelle session'
      }
    }).as('createRequest')

    cy.contains('button', 'Create').click()
    cy.url().should('include', '/sessions/create')
    cy.wait('@teachersRequest')

    cy.get('input[formControlName="name"]').type('Nouvelle session')
    cy.get('input[formControlName="date"]').type('2026-07-01')

    cy.get('mat-select[formControlName="teacher_id"]').click()
    cy.contains('mat-option', 'Alice Martin').click()

    cy.get('textarea[formControlName="description"]').type('Session de test e2e')
    cy.contains('button', 'Save').click()

    cy.wait('@createRequest')
    cy.url().should('include', '/sessions')
    cy.contains('Session created !').should('be.visible')
  })

  it('modifie une session depuis le formulaire admin', () => {
    loginAs(adminUser)

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [teacher]
    }).as('teachersRequest')

    cy.intercept('GET', '/api/session/1', {
      statusCode: 200,
      body: baseSession
    }).as('detailRequest')

    cy.intercept('PUT', '/api/session/1', {
      statusCode: 200,
      body: {
        ...baseSession,
        name: 'Morning Flow Updated'
      }
    }).as('updateRequest')

    cy.contains('button', 'Edit').click()
    cy.url().should('include', '/sessions/update/1')
    cy.wait('@teachersRequest')
    cy.wait('@detailRequest')

    cy.get('input[formControlName="name"]').clear().type('Morning Flow Updated')
    cy.contains('button', 'Save').click()

    cy.wait('@updateRequest').its('request.body').should((body) => {
      expect(body.name).to.equal('Morning Flow Updated')
    })
    cy.url().should('include', '/sessions')
    cy.contains('Session updated !').should('be.visible')
  })
})

