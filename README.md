# My Wargame App

Application web pour les figurinistes : inventaire de peintures, suivi d'avancement des armées (assemblage → sous-couche → peinture → soclage).

- **Frontend** : Nuxt 4 + Nuxt UI (`frontend/`)
- **Backend** : Spring Boot 4 / Java 25, JWT, Flyway (`backend/`)
- **Base** : PostgreSQL 17 via Docker (port hôte **5433**)

## Démarrer en local

Prérequis : Docker Desktop, JDK 25, Node 24 (Maven est fourni par le wrapper `mvnw`).

Crée ton fichier `.env` à partir du modèle, puis remplis `POSTGRES_PASSWORD` et `APP_JWT_SECRET` (le fichier `.env` est ignoré par git, ne le commite jamais) :

```bash
cp .env.example .env
```

```bash
openssl rand -base64 48
```

```bash
docker compose up -d
```

```bash
cd backend && ./mvnw spring-boot:run
```

```bash
cd frontend && npm install && npm run dev
```

L'app est sur http://localhost:3000, l'API sur http://localhost:8080.

Au premier démarrage, le backend importe le catalogue de peintures (~9 900 références, 32 marques).

### Tests

```bash
cd backend && ./mvnw test
```

Les tests d'intégration démarrent un PostgreSQL via Testcontainers (Docker doit tourner) et n'ont pas besoin du `.env`.

## Configuration

Les secrets ne sont jamais dans le code : ils viennent de `.env` (lu par Docker Compose et par Spring Boot) ou des variables d'environnement. Sans `POSTGRES_PASSWORD` ni `APP_JWT_SECRET`, le backend refuse de démarrer.

| Variable | Défaut | Rôle |
|---|---|---|
| `POSTGRES_PASSWORD` | **aucun, obligatoire** | Mot de passe PostgreSQL (conteneur et backend) |
| `APP_JWT_SECRET` | **aucun, obligatoire** | Clé HS256 des JWT, 32 caractères minimum |
| `POSTGRES_DB` / `POSTGRES_USER` | `mywargame` | Nom de la base et utilisateur |
| `DB_URL` | `jdbc:postgresql://localhost:5433/mywargame` | URL JDBC |
| `APP_CORS_ORIGINS` | `http://localhost:3000,http://localhost:3001` | Origines autorisées |
| `NUXT_PUBLIC_API_BASE` | `http://localhost:8080` | URL de l'API vue par le front |

## API

| Méthode | Route | |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | Renvoie `{ token, user }` |
| GET | `/api/me` | Utilisateur courant |
| GET | `/api/brands` | Marques du catalogue |
| GET | `/api/paints?brand=&q=&page=&size=` | Catalogue paginé |
| GET / POST / DELETE | `/api/inventory`, `/api/inventory/{id}` | Peintures possédées |
| GET / POST / DELETE | `/api/armies`, `/api/armies/{id}` | Armées + % d'avancement |

## Catalogue de peintures

Les fichiers de `backend/src/main/resources/catalog/` viennent de [Arcturus5404/miniature-paints](https://github.com/Arcturus5404/miniature-paints) (licence MIT, copie dans le même dossier). Pour les mettre à jour :

```bash
powershell -File scripts/update-catalog.ps1
```

L'import ne se fait que si la table `paint` est vide.

## Feuille de route

1. ~~Bases de l'app~~ (auth, catalogue, squelette des pages)
2. Inventaire complet : niveau du pot, quantité, notes, alertes « presque vide »
3. Armées : unités, figurines, changement d'étape, % par unité
4. Bonus : recettes de peinture, liste de courses, galerie avant/après
