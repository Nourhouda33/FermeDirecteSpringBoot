# Rapport Technique — FermeDirecte
## Système de Gestion d'une Boutique en Ligne (ShopFlow)

**Année universitaire :** 2025 – 2026  
**Enseignante :** Dr. Ing. Ghada Feki  
**Projet :** Mini-Projet Backend Spring Boot / Frontend Angular

---

## 1. Schéma d'Architecture Technique

### 1.1 Architecture en Couches

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT (Angular / Postman)              │
│                    HTTP Requests (JSON)                     │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                  COUCHE SÉCURITÉ                             │
│   JwtAuthFilter → SecurityConfig → UserDetailsServiceImpl   │
│   (Validation JWT à chaque requête protégée)                │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                COUCHE CONTROLLER (REST)                     │
│  AuthController │ ProductController │ CartController        │
│  OrderController │ ReviewController │ CouponController      │
│  CategoryController │ AddressController │ DashboardController│
│  SellerProfileController                                    │
│  (@RestController, @Valid, codes HTTP, DTOs)                │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                  COUCHE SERVICE (Métier)                    │
│  AuthService │ ProductService │ CartService                 │
│  OrderService │ ReviewService │ CouponService               │
│  CategoryService │ AddressService │ DashboardService        │
│  SellerProfileService                                       │
│  (@Service, @Transactional, règles métier)                  │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│               COUCHE REPOSITORY (Données)                   │
│  UserRepository │ ProductRepository │ CartRepository        │
│  OrderRepository │ ReviewRepository │ CouponRepository      │
│  CategoryRepository │ AddressRepository                     │
│  SellerProfileRepository                                    │
│  (JpaRepository, JPQL, méthodes dérivées)                   │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                  COUCHE ENTITÉS (JPA)                       │
│  User │ SellerProfile │ Address │ Category                  │
│  Product │ ProductVariant │ Cart │ CartItem                 │
│  Order │ OrderItem │ Coupon │ Review                        │
│  (Jakarta Persistence, relations, contraintes)              │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                BASE DE DONNÉES MySQL                        │
│              (FermeDirecte — 12 tables)                     │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Flux de Données — Exemple : Passer une Commande

```
Angular                Spring Boot                    MySQL
  │                        │                             │
  │── POST /api/orders ──► │                             │
  │   (Bearer Token)       │                             │
  │                   JwtAuthFilter                      │
  │                   valide le token                    │
  │                        │                             │
  │                   OrderController                    │
  │                   @Valid OrderRequest                │
  │                        │                             │
  │                   OrderService                       │
  │                   - vérif stock                      │
  │                   - calcul total                     │
  │                   - appliquer coupon                 │
  │                   - vider panier                     │
  │                        │── save(order) ─────────────►│
  │                        │◄─ Order sauvegardé ─────────│
  │◄── 201 OrderResponse ──│                             │
```

---

## 2. Modèle de Données

### 2.1 Diagramme des Entités

```
User (1) ──────────── (1) SellerProfile
  │                         │
  │ (1-N)                   │ (1-N)
  ▼                         ▼
Address                  Product (N-M) Category
                            │
                            │ (1-N)
                            ▼
                        ProductVariant

User (1) ──── (1) Cart ──── (1-N) CartItem ──── Product
                                               └──── ProductVariant (nullable)

User (1) ──── (1-N) Order ──── (1-N) OrderItem ──── Product
                  │                              └──── ProductVariant (nullable)
                  └──── Address
                  └──── Coupon (nullable)

User (1) ──── (1-N) Review ──── Product
```

### 2.2 Tables MySQL Générées

| Table | Colonnes principales |
|---|---|
| users | id, email, mot_de_passe, prenom, nom, role, actif, refresh_token |
| seller_profiles | id, user_id, nom_boutique, description, logo, note |
| addresses | id, user_id, rue, ville, code_postal, pays, principal |
| categories | id, nom, description, parent_id |
| products | id, nom, description, prix, prix_promo, stock, actif, image_url, seller_profile_id |
| product_variants | id, product_id, attribut, valeur, stock_supplementaire, prix_delta |
| carts | id, user_id, date_modification |
| cart_items | id, cart_id, product_id, product_variant_id, quantite |
| orders | id, client_id, address_id, coupon_id, numero_commande, statut, statut_paiement, sous_total, frais_livraison, total_ttc, date_commande |
| order_items | id, order_id, product_id, product_variant_id, quantite, prix_unitaire |
| coupons | id, code, type, valeur, date_expiration, usages_max, usages_actuels, actif |
| reviews | id, client_id, product_id, note, commentaire, date_creation, approuve |

---

## 3. Choix d'Implémentation

### 3.1 Stack Technologique

| Technologie | Version | Justification |
|---|---|---|
| Java | 21 LTS | Dernière version LTS, performances améliorées |
| Spring Boot | 3.2.4 | Jakarta EE 10, Spring Security 6 |
| MySQL | 8.x | Base de données relationnelle robuste |
| JWT (jjwt) | 0.12.3 | API moderne, sécurité access + refresh token |
| Lombok | 1.18.30 | Réduction du code boilerplate |
| MapStruct | 1.5.5 | Mapping Entity ↔ DTO performant |
| Springdoc OpenAPI | 2.3.0 | Documentation Swagger UI automatique |

### 3.2 Sécurité JWT

Le système utilise deux tokens :
- **Access Token** : durée 15 minutes (`jwt.expiration=900000`)
- **Refresh Token** : durée 7 jours (`jwt.refresh-expiration=604800000`)

Flux d'authentification :
```
1. POST /api/auth/login → retourne accessToken + refreshToken
2. Chaque requête → Header: Authorization: Bearer <accessToken>
3. Token expiré → POST /api/auth/refresh avec refreshToken
4. Déconnexion → POST /api/auth/logout (invalide le refreshToken en base)
```

Le `JwtAuthFilter` intercepte chaque requête, valide le token et injecte l'authentification dans le `SecurityContextHolder`.

### 3.3 Gestion des Rôles

```java
ADMIN   → gestion globale, catégories, utilisateurs, modération avis
SELLER  → gestion de ses produits, consultation commandes reçues
CUSTOMER → panier, commandes, avis
```

Implémenté avec `@PreAuthorize("hasRole('ADMIN')")` sur chaque endpoint sensible et `@EnableMethodSecurity` dans `SecurityConfig`.

### 3.4 Logique Métier — Commande

Lors du passage d'une commande (`OrderService.passerCommande`) :
1. Vérification que le panier n'est pas vide
2. Vérification du stock pour chaque article
3. Application du coupon (PERCENT ou FIXED)
4. Calcul : `totalTTC = sousTotal - remise + fraisLivraison`
5. Génération du numéro unique : `ORD-2026-XXXXX`
6. Décrémentation du stock de chaque produit
7. Vidage du panier après confirmation

### 3.5 Gestion des Variantes (FermeDirecte)

Adapté au contexte agricole tunisien :
- `attribut` = "Poids"
- `valeur` = "1kg", "5kg", "10kg", "25kg"
- `prixDelta` = supplément de prix par rapport au prix de base
- `stockSupplementaire` = stock individuel par variante

### 3.6 Cohérence du Nommage

Tous les champs sont en **français** pour correspondre au contexte métier :

| Concept | Champ Java | Colonne MySQL |
|---|---|---|
| Mot de passe | `motDePasse` | `mot_de_passe` |
| Prénom | `prenom` | `prenom` |
| Statut commande | `statut` | `statut` |
| Sous-total | `sousTotal` | `sous_total` |
| Date création | `dateCreation` | `date_creation` |

---

## 4. Endpoints REST

### 4.1 Authentification `/api/auth`

| Méthode | Endpoint | Accès | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Inscription client ou vendeur |
| POST | `/auth/login` | Public | Connexion, retourne tokens |
| POST | `/auth/refresh` | Public | Renouvellement access token |
| POST | `/auth/logout` | Public | Invalidation refresh token |

### 4.2 Produits `/api/products`

| Méthode | Endpoint | Accès | Description |
|---|---|---|---|
| GET | `/products` | Public | Liste paginée |
| GET | `/products/{id}` | Public | Détail produit |
| GET | `/products/search?q=` | Public | Recherche full-text |
| GET | `/products/filter?min=&max=` | Public | Filtre par prix |
| POST | `/products` | SELLER/ADMIN | Créer produit |
| PUT | `/products/{id}` | SELLER/ADMIN | Modifier produit |
| DELETE | `/products/{id}` | SELLER/ADMIN | Désactiver (soft delete) |

### 4.3 Panier `/api/cart`

| Méthode | Endpoint | Accès | Description |
|---|---|---|---|
| GET | `/cart` | Authentifié | Voir panier |
| POST | `/cart/items` | Authentifié | Ajouter article |
| PUT | `/cart/items/{id}` | Authentifié | Modifier quantité |
| DELETE | `/cart/items/{id}` | Authentifié | Retirer article |

### 4.4 Commandes `/api/orders`

| Méthode | Endpoint | Accès | Description |
|---|---|---|---|
| POST | `/orders` | CUSTOMER | Passer commande |
| GET | `/orders/my` | Authentifié | Mes commandes |
| GET | `/orders/{id}` | Authentifié | Détail commande |
| GET | `/orders` | ADMIN | Toutes les commandes |
| PUT | `/orders/{id}/status` | SELLER/ADMIN | Mettre à jour statut |
| PUT | `/orders/{id}/cancel` | CUSTOMER | Annuler commande |

### 4.5 Autres Endpoints

| Endpoint | Accès | Description |
|---|---|---|
| GET `/categories` | Public | Arbre de catégories |
| POST/PUT/DELETE `/categories` | ADMIN | Gestion catégories |
| POST `/reviews` | CUSTOMER | Poster un avis |
| GET `/reviews/product/{id}` | Public | Avis d'un produit |
| PUT `/reviews/{id}/approve` | ADMIN | Approuver avis |
| POST/PUT/DELETE `/coupons` | ADMIN | Gestion coupons |
| GET `/coupons/validate/{code}` | Public | Valider coupon |
| GET `/dashboard/admin` | ADMIN | Stats globales |
| GET `/dashboard/seller` | SELLER | Stats vendeur |
| GET/POST/PUT/DELETE `/addresses` | Authentifié | Gestion adresses |
| GET/POST `/sellers/me` | SELLER | Profil boutique |

---

## 5. Difficultés Rencontrées et Solutions

### 5.1 Incohérence des noms de champs

**Problème :** Les entités mélangeaient français et anglais (`name`/`nom`, `price`/`prix`, `quantity`/`quantite`), causant des erreurs JPA (`No property 'name' found for type 'Product'`).

**Solution :** Standardisation complète en français de tous les champs dans les entités, repositories, DTOs et JPQL.

### 5.2 Erreurs mappedBy JPA

**Problème :** Après renommage des champs, les annotations `mappedBy` ne correspondaient plus aux noms des champs dans les entités enfants.

**Solution :** Synchronisation systématique — `mappedBy = "client"` dans `User` correspond exactement au champ `private User client` dans `Review`.

### 5.3 Connexion base de données

**Problème :** Le projet était configuré pour PostgreSQL mais MySQL était installé localement.

**Solution :** Ajout du driver `mysql-connector-j` dans `pom.xml` et configuration de `application.properties` avec `createDatabaseIfNotExist=true`.

### 5.4 Spring Security 6 — Routes publiques

**Problème :** Le `context-path=/api` causait un double préfixe dans `SecurityConfig` (`/api/api/auth/**`).

**Solution :** Les routes dans `SecurityConfig` ne doivent pas inclure le `context-path` — utiliser `/auth/**` au lieu de `/api/auth/**`.

### 5.5 JWT API 0.12.x

**Problème :** L'API jjwt 0.12.x a changé — `Jwts.parserBuilder()` est supprimé.

**Solution :** Utilisation de `Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()`.

---

## 6. Instructions d'Installation et Lancement

### 6.1 Prérequis

- Java 21 JDK
- MySQL 8.x
- Maven 3.x
- Node.js 18+ (pour Angular)

### 6.2 Backend Spring Boot

```bash
# 1. Cloner le projet
git clone <url>

# 2. Configurer la base de données dans application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/FermeDirecte?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=<votre_mot_de_passe>

# 3. Lancer l'application
./mvnw spring-boot:run

# L'application démarre sur http://localhost:8081/api
```

### 6.3 Swagger UI

```
http://localhost:8081/api/swagger-ui/index.html
```

1. Utiliser `POST /auth/login` pour obtenir le token
2. Cliquer sur **Authorize** 🔒
3. Entrer : `Bearer <votre_token>`

### 6.4 Frontend Angular (à venir)

```bash
cd frontend
npm install
ng serve
# Démarre sur http://localhost:4200
```

---

## 7. Structure du Projet

```
src/main/java/com/FermeDirecte/FermeDirecte/
├── config/
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   └── CorsConfig.java
├── controller/
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── ReviewController.java
│   ├── CouponController.java
│   ├── CategoryController.java
│   ├── AddressController.java
│   ├── SellerProfileController.java
│   └── DashboardController.java
├── service/
│   ├── AuthService.java
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   ├── ReviewService.java
│   ├── CouponService.java
│   ├── CategoryService.java
│   ├── AddressService.java
│   ├── SellerProfileService.java
│   └── DashboardService.java
├── repository/
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── OrderRepository.java
│   ├── ReviewRepository.java
│   ├── CouponRepository.java
│   ├── CategoryRepository.java
│   ├── AddressRepository.java
│   └── SellerProfileRepository.java
├── entity/
│   ├── User.java
│   ├── SellerProfile.java
│   ├── Address.java
│   ├── Category.java
│   ├── Product.java
│   ├── ProductVariant.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Coupon.java
│   └── Review.java
├── dto/
│   ├── auth/
│   ├── product/
│   ├── cart/
│   ├── order/
│   ├── review/
│   ├── coupon/
│   ├── category/
│   ├── address/
│   ├── seller/
│   ├── dashboard/
│   └── user/
├── enums/
│   ├── Role.java
│   ├── OrderStatus.java
│   ├── PaymentStatus.java
│   └── CouponType.java
├── exception/
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── security/
│   ├── JwtService.java
│   └── UserDetailsServiceImpl.java
└── filter/
    └── JwtAuthFilter.java
```
