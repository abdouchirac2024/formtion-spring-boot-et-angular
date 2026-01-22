# Cours Spring Boot JPA - Gestion des Produits

## Vue d'ensemble du projet

Ce projet Spring Boot illustre la gestion d'une application de produits avec les technologies suivantes :
- **Spring Boot 3.2.1** avec Java 21
- **Spring Data JPA** pour la persistance
- **MySQL** comme base de données
- **Lombok** pour réduire le code boilerplate
- **Architecture en couches** (Entity, Repository, Service)

---

## Objectif 1 : Créer une association OneToMany entre deux entités

### Explication théorique
L'association **OneToMany** représente une relation où une entité peut être associée à plusieurs instances d'une autre entité. Dans notre cas :
- Une **Catégorie** peut contenir plusieurs **Produits**
- Un **Produit** appartient à une seule **Catégorie**

### Code de l'entité Catégorie (côté "One")

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCat;
    private String nomCat;
    private String descriptionCat;
    
    // Association OneToMany - Une catégorie a plusieurs produits
    @OneToMany(mappedBy = "categorie")
    private List<Produit> produits;
}
```

**Explications détaillées :**
- `@OneToMany(mappedBy = "categorie")` : Indique que cette entité est le côté "One" de la relation
- `mappedBy = "categorie"` : Fait référence à l'attribut "categorie" dans l'entité Produit
- `List<Produit> produits` : Collection qui contiendra tous les produits de cette catégorie

### Code de l'entité Produit (côté "Many")

```java
@Entity
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduit;
    private String nomProduit;
    private Double prixProduit;
    private Date dateCreation;
    
    // Association ManyToOne - Plusieurs produits appartiennent à une catégorie
    @ManyToOne
    private Categorie categorie;
    
    // Getters, setters, constructeurs...
}
```

**Explications détaillées :**
- `@ManyToOne` : Indique que plusieurs produits peuvent appartenir à une catégorie
- `private Categorie categorie` : Référence vers la catégorie parente
- Cette annotation crée automatiquement une clé étrangère `categorie_id` dans la table `produit`

---

## Objectif 2 : Utilisation de Lombok

### Explication théorique
**Lombok** est une bibliothèque qui génère automatiquement du code Java répétitif (getters, setters, constructeurs, etc.) grâce aux annotations.

### Configuration dans pom.xml

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### Utilisation dans l'entité Catégorie

```java
@Data                    // Génère getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Génère un constructeur sans paramètres
@AllArgsConstructor     // Génère un constructeur avec tous les paramètres
@Entity
public class Categorie {
    // Attributs seulement, pas besoin de getters/setters !
}
```

**Avantages de Lombok :**
- **Réduction du code** : Plus besoin d'écrire manuellement getters/setters
- **Maintenance facilitée** : Ajout automatique des méthodes lors de l'ajout d'attributs
- **Code plus lisible** : Focus sur la logique métier plutôt que sur le code technique

### Comparaison avec l'entité Produit (sans Lombok)

```java
// Sans Lombok - Code verbose
public class Produit {
    private Long idProduit;
    
    public Long getIdProduit() { return idProduit; }
    public void setIdProduit(Long idProduit) { this.idProduit = idProduit; }
    // ... répéter pour chaque attribut
}
```

---

## Objectif 3 : Interroger les entités en fournissant un attribut non clé

### Explication théorique
Spring Data JPA permet de créer des requêtes automatiquement basées sur les noms des méthodes, sans écrire de SQL.

### Méthodes de requête par convention de nommage

```java
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    // Recherche exacte par nom
    List<Produit> findByNomProduit(String nom);
    
    // Recherche partielle (contient)
    List<Produit> findByNomProduitContains(String nom);
    
    // Recherche par ID de catégorie
    List<Produit> findByCategorieIdCat(Long id);
}
```

**Explications détaillées :**

1. **`findByNomProduit(String nom)`**
   - Génère automatiquement : `SELECT * FROM produit WHERE nom_produit = ?`
   - Recherche exacte du nom du produit

2. **`findByNomProduitContains(String nom)`**
   - Génère automatiquement : `SELECT * FROM produit WHERE nom_produit LIKE %?%`
   - Recherche partielle dans le nom du produit

3. **`findByCategorieIdCat(Long id)`**
   - Navigue dans la relation : `produit.categorie.idCat`
   - Génère : `SELECT * FROM produit p JOIN categorie c ON p.categorie_id = c.id_cat WHERE c.id_cat = ?`

### Utilisation dans le service

```java
@Service
public class ProduitServiceImpl implements ProduitService {
    
    @Autowired
    ProduitRepository produitRepository;
    
    public List<Produit> findByNomProduit(String nom) {
        return produitRepository.findByNomProduit(nom);
    }
    
    public List<Produit> findByNomProduitContains(String nom) {
        return produitRepository.findByNomProduitContains(nom);
    }
}
```

---

## Objectif 4 : Écrire des requêtes @Query en utilisant le langage JPQL

### Explication théorique
**JPQL (Java Persistence Query Language)** permet d'écrire des requêtes personnalisées en utilisant les noms des entités Java plutôt que les tables de base de données.

### Requête JPQL avec paramètres nommés

```java
@Query("select p from Produit p where p.nomProduit like %:nom and p.prixProduit > :prix")
List<Produit> findByNomPrix(@Param("nom") String nom, @Param("prix") Double prix);
```

**Explications détaillées :**
- `select p from Produit p` : Sélectionne l'entité Produit (pas la table)
- `p.nomProduit like %:nom` : Recherche partielle avec paramètre nommé `:nom`
- `p.prixProduit > :prix` : Condition sur le prix avec paramètre nommé `:prix`
- `@Param("nom")` : Lie le paramètre Java au paramètre JPQL

### Requête JPQL avec tri personnalisé

```java
@Query("select p from Produit p order by p.nomProduit ASC, p.prixProduit ASC")
List<Produit> trierProduitsNomsPrix();
```

**Explications :**
- Tri multiple : d'abord par nom (ASC), puis par prix (ASC)
- Plus flexible que les méthodes de convention pour des tris complexes

---

## Objectif 5 : Écrire des requêtes @Query en passant des entités en paramètre

### Requête avec entité en paramètre

```java
@Query("select p from Produit p where p.categorie = ?1")
List<Produit> findByCategorie(Categorie categorie);
```

**Explications détaillées :**
- `p.categorie = ?1` : Compare directement avec l'objet Categorie passé en paramètre
- `?1` : Paramètre positionnel (premier paramètre de la méthode)
- JPA compare automatiquement les IDs des entités

### Utilisation dans le service

```java
public List<Produit> findByCategorie(Categorie categorie) {
    return produitRepository.findByCategorie(categorie);
}
```

### Exemple d'utilisation

```java
// Récupérer une catégorie
Categorie electronique = categorieRepository.findById(1L).get();

// Trouver tous les produits de cette catégorie
List<Produit> produitsElectronique = produitService.findByCategorie(electronique);
```

---

## Objectif 6 : Interroger les produits selon l'id de leur catégorie

### Méthode par convention de nommage

```java
List<Produit> findByCategorieIdCat(Long id);
```

**Explication :**
- Navigation dans la relation : `produit.categorie.idCat`
- Spring Data JPA comprend automatiquement le chemin de navigation
- Plus simple que d'écrire une requête JPQL

### Comparaison avec la requête JPQL équivalente

```java
// Équivalent en JPQL (plus verbeux)
@Query("select p from Produit p where p.categorie.idCat = ?1")
List<Produit> findByCategorie(Long categorieId);
```

---

## Objectif 7 : Trier les données

### Tri simple par convention de nommage

```java
List<Produit> findByOrderByNomProduitAsc();
```

**Explications :**
- `OrderBy` : Indique un tri
- `NomProduitAsc` : Tri par nom de produit en ordre croissant
- Génère automatiquement : `ORDER BY nom_produit ASC`

### Tri complexe avec @Query

```java
@Query("select p from Produit p order by p.nomProduit ASC, p.prixProduit ASC")
List<Produit> trierProduitsNomsPrix();
```

**Avantages du tri avec @Query :**
- Tri sur plusieurs colonnes
- Contrôle précis de l'ordre de tri
- Possibilité de combiner avec des conditions WHERE

---

## Objectif 8 : Ajouter les méthodes du Repository à la couche Service

### Interface ProduitService

```java
public interface ProduitService {
    // CRUD de base
    Produit saveProduit(Produit p);
    Produit updateProduit(Produit p);
    void deleteProduit(Produit p);
    void deleteProduitById(Long id);
    Produit getProduit(Long id);
    List<Produit> getAllProduits();
    
    // Méthodes de recherche personnalisées
    List<Produit> findByNomProduit(String nom);
    List<Produit> findByNomProduitContains(String nom);
    List<Produit> findByNomPrix(String nom, Double prix);
    List<Produit> findByCategorie(Categorie categorie);
    List<Produit> findByCategorieIdCat(Long id);
    List<Produit> findByOrderByNomProduitAsc();
    List<Produit> trierProduitsNomsPrix();
}
```

### Implémentation ProduitServiceImpl

```java
@Service
public class ProduitServiceImpl implements ProduitService {

    @Autowired
    ProduitRepository produitRepository;
    
    // Délégation vers le repository
    @Override
    public List<Produit> findByNomProduit(String nom) {
        return produitRepository.findByNomProduit(nom);
    }

    @Override
    public List<Produit> findByNomProduitContains(String nom) {
        return produitRepository.findByNomProduitContains(nom);
    }

    @Override
    public List<Produit> findByNomPrix(String nom, Double prix) {
        return produitRepository.findByNomPrix(nom, prix);
    }
    
    // ... autres méthodes
}
```

**Explications de l'architecture :**

1. **Séparation des responsabilités**
   - **Repository** : Accès aux données uniquement
   - **Service** : Logique métier et orchestration

2. **Avantages de cette approche**
   - **Testabilité** : Possibilité de mocker le service
   - **Réutilisabilité** : Le service peut être utilisé par plusieurs contrôleurs
   - **Logique métier centralisée** : Validation, transformation des données

3. **Pattern de délégation**
   - Le service délègue les appels au repository
   - Possibilité d'ajouter de la logique métier avant/après l'appel au repository

---

## Configuration de la base de données

### application.properties

```properties
# Configuration MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/spring_DB?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root

# Configuration JPA/Hibernate
spring.jpa.show-sql=true                    # Affiche les requêtes SQL générées
spring.jpa.hibernate.ddl-auto=update       # Met à jour automatiquement le schéma
```

**Explications :**
- `createDatabaseIfNotExist=true` : Crée la base si elle n'existe pas
- `show-sql=true` : Utile pour le debug et l'apprentissage
- `ddl-auto=update` : Met à jour les tables selon les entités

---

## Résumé des concepts clés

1. **Relations JPA** : `@OneToMany` et `@ManyToOne` pour modéliser les associations
2. **Lombok** : Réduction drastique du code boilerplate
3. **Query Methods** : Génération automatique de requêtes par convention de nommage
4. **JPQL** : Requêtes personnalisées orientées objet
5. **Architecture en couches** : Séparation Repository/Service pour une meilleure organisation
6. **Spring Data JPA** : Abstraction puissante pour la persistance des données

Ce projet illustre parfaitement les bonnes pratiques Spring Boot pour une application de gestion de données avec des relations entre entités.