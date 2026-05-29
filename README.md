# Yoga App - Application Full Stack

Application de gestion de cours de Yoga avec authentification sécurisée JWT.

---

## 📋 Table des matières

- [Structure du projet](#structure-du-projet)
- [Pré-requis](#pré-requis)
- [Installation](#installation)
- [Lancer l'application](#lancer-lapplication)
- [Lancer les tests](#lancer-les-tests)
- [Générer les rapports de couverture](#générer-les-rapports-de-couverture)

---

## 📁 Structure du projet

```
Testez-et-am-liorez-une-application-full-stack/
├── back/              # Backend Spring Boot avec tests
│   ├── src/
│   ├── pom.xml        # Configuration Maven
│   ├── compose.yaml   # Configuration Docker
│   └── README.md      # Documentation backend
│
├── front/             # Frontend Angular (à venir)
│   └── package.json
│
└── README.md          # Ce fichier
```

---

## 🔧 Pré-requis

### Pour le Backend

- **JDK 21** - Installation : télécharger depuis [oracle.com](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.9.3+** - Installation : https://archive.apache.org/dist/maven/maven-3/3.9.3/binaries/
- **Docker Desktop** - Installation : https://www.docker.com/products/docker-desktop
- **Docker Compose** - Inclus dans Docker Desktop

### Vérification de l'installation

```powershell
java -version       # Doit afficher Java 21
mvn -version       # Doit afficher Maven 3.9.3+
docker --version   # Doit afficher Docker
docker-compose --version  # Doit afficher Docker Compose
```

---

## 📦 Installation

### 1. Cloner le projet

```powershell
cd C:\Projects
git clone <URL_DU_DEPOT>
cd Testez-et-am-liorez-une-application-full-stack
```

### 2. Préparer l'environnement

Assurez-vous que **Docker Desktop est démarré** avant de lancer l'application.

---

## 🚀 Lancer l'application

### Backend (Spring Boot)

```powershell
cd back
mvn spring-boot:run
```

**Résultat attendu :**
- L'application démarre sur `http://localhost:8080`
- Docker crée et démarre automatiquement le container MySQL
- Les tables de base de données sont initialisées

**Logs de démarrage :**
```
:: Spring Boot ::                (v3.5.5)
[back] [           main] c.o.s.SpringBootSecurityJwtApplication   : Starting...
...
[back] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080
```

### Vérifier la connexion à la base de données

1. Ouvrez **Docker Desktop**
2. Vous devriez voir le container `back_mysql` en cours d'exécution
3. Pour se connecter à la base de données, cliquez sur le container et allez dans l'onglet `Exec`
4. Exécutez :
```bash
mysql -u user_test -p
# Mot de passe : test_password
use test;
select * from users;
```

### Authentification par défaut

```
Email : yoga@studio.com
Mot de passe : test!1234
```

### Importer la collection Postman

1. Ouvrez **Postman**
2. Cliquez sur `Import`
3. Sélectionnez le fichier : `back/postman/yoga.postman_collection.json`

---

## 🧪 Lancer les tests

### Backend - Tests unitaires et d'intégration

```powershell
cd back
mvn clean test
```

**Résultat :**
- Tous les tests unitaires s'exécutent
- Les tests d'intégration (IT) s'exécutent également
- Les résultats s'affichent à la fin de l'exécution

**Arrêter après avoir exécuté les tests :**
```powershell
mvn clean test help:active-profiles
```

### Exécuter un test spécifique

```powershell
mvn -Dtest=AuthControllerTest test
```

### Exécuter uniquement les tests d'intégration

```powershell
mvn failsafe:integration-test
```

---

## 📊 Générer les rapports de couverture

### Rapport de couverture JaCoCo (Backend)

```powershell
cd back
mvn clean test jacoco:report
```

**Le rapport est généré à :**
```
back/target/site/jacoco/index.html
```

**Pour ouvrir le rapport directement :**

**Windows (PowerShell) :**
```powershell
cd back
Start-Process "target/site/jacoco/index.html"
```

**Ou (n'importe quel système) :**
```powershell
cd back
mvn clean test jacoco:report
```

Puis ouvrez manuellement `target/site/jacoco/index.html` dans votre navigateur.

### Vérifier les métriques de couverture

Durant l'exécution de `mvn clean test`, JaCoCo vérifie automatiquement que :
- **Couverture des instructions (INSTRUCTION)** ≥ 80%
- **Couverture des branches (BRANCH)** ≥ 80%
- **Couverture des lignes (LINE)** ≥ 80%
- **Couverture des méthodes (METHOD)** ≥ 80%

Si les critères ne sont pas respectés, le build **échoue**.

**Exemple de rapport de couverture actuel :**
```
back/target/site/jacoco/    (Dossier contenant le rapport HTML)
back/target/jacoco.exec     (Fichier de données JaCoCo)
```

### Fichiers de rapport

| Fichier | Description |
|---------|-------------|
| `target/site/jacoco/index.html` | Rapport HTML interactif |
| `target/site/jacoco/status.txt` | Résumé textuel |
| `target/jacoco.exec` | Données brutes de couverture |

---

## 📈 Amélioration de la couverture

Pour identifier les zones avec une faible couverture :

1. Exécutez : `mvn clean test jacoco:report`
2. Ouvrez `target/site/jacoco/index.html`
3. Cliquez sur les classes pour voir le détail ligne par ligne
4. Les lignes **non couvertes** apparaissent en **rouge**
5. Les lignes **partiellement couvertes** apparaissent en **jaune**
6. Ajoutez des tests pour les branches manquantes

---

## 🔍 Ressources utiles

### Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Maven Documentation](https://maven.apache.org/guides/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Docker Documentation](https://docs.docker.com/)

### Collection Postman

Fichier : `back/postman/yoga.postman_collection.json`

Contient tous les endpoints de l'API :
- Authentification
- Gestion des utilisateurs
- Gestion des sessions
- Gestion des professeurs

---

## 💡 Commandes utiles

```powershell
# Nettoyer le projet
mvn clean

# Compiler sans tester
mvn compile

# Exécuter les tests
mvn test

# Générer la couverture
mvn jacoco:report

# Lancer l'application
mvn spring-boot:run

# Générer le JAR final
mvn package

# Afficher les dépendances
mvn dependency:tree
```

---

## 🐛 Dépannage

### Docker Compose ne démarre pas
- [ ] Vérifiez que Docker Desktop est **en cours d'exécution**
- [ ] Vérifiez les ports 3306 (MySQL) n'est pas utilisé
- [ ] Exécutez : `docker-compose down` puis `mvn spring-boot:run`

### Erreur « Cannot connect to MySQL »
- [ ] Attendez 10-15 secondes après le démarrage de Docker
- [ ] Vérifiez les logs : `docker logs back_mysql`
- [ ] Redémarrez le container : `docker-compose restart`

### Tests échouent avec « Low Coverage »
- [ ] Exécutez : `mvn clean test jacoco:report`
- [ ] Ouvrez le rapport pour identifier les zones manquantes
- [ ] Ajoutez les tests correspondants

---

## ✅ Checklist de démarrage

- [ ] JDK 21 installé et configuré
- [ ] Maven 3.9.3+ installé et configuré
- [ ] Docker Desktop démarré
- [ ] Cloner le projet
- [ ] `mvn spring-boot:run` lancé
- [ ] Docker container MySQL en cours d'exécution
- [ ] `mvn clean test` exécuté avec succès
- [ ] Rapport JaCoCo généré

---

## 📝 Notes

- Le frontend sera intégré ultérieurement
- Actuellement, seul le backend est documenté et testé
- Les rapports de couverture se concentrent sur le backend

---

**Dernière mise à jour :** Mai 2026

Pour toute question ou problème, consultez la documentation spécifique :
- Backend : `back/README.md`
- Frontend : `front/README.md` (à venir)

