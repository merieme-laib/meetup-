-- Nettoyage avant de créer
DROP TABLE IF EXISTS payments, notifications, comments, followers, likes, registrations, events, users CASCADE;

-- 1.1 User  
CREATE TABLE "users" (
  "id" BIGSERIAL PRIMARY KEY, -- Identifiant unique Long 
  "first_name" varchar NOT NULL, -- Prénom requis 
  "last_name" varchar NOT NULL, -- Nom requis 
  "email" varchar UNIQUE NOT NULL, -- Identifiant de connexion 
  "password" varchar NOT NULL, -- Mot de passe hashé 
  "bio" text, -- Description courte 
  "avatar_url" varchar, -- URL de l'image 
  "role" varchar DEFAULT 'user',
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP -- ISO 8601 
);

-- 1.3 Event 
CREATE TABLE "events" (
  "id" BIGSERIAL PRIMARY KEY,
  "title" varchar NOT NULL, -- Titre requis 
  "description" text NOT NULL, -- Description requise 
  "date" timestamp NOT NULL, -- Date et heure ISO 8601 
  "location" varchar NOT NULL, -- Adresse ou nom du lieu 
  "city" varchar NOT NULL, -- Ville requise 
  "is_online" boolean DEFAULT false, -- true si en ligne 
  "image_url" varchar, -- URL de couverture 
  "price" decimal DEFAULT 0.0, -- Prix en euros 
  "max_participants" integer, -- Capacité (null = illimitée) 
  "category" varchar NOT NULL, -- Valeur fixe  
  "creator_id" bigint NOT NULL, -- ID du créateur 
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

-- 1.4 Registration 
CREATE TABLE "registrations" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" bigint NOT NULL,
  "event_id" bigint NOT NULL,
  "registered_at" timestamp DEFAULT CURRENT_TIMESTAMP -- ISO 8601 
);

-- 1.5 Like 
CREATE TABLE "likes" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" bigint NOT NULL,
  "event_id" bigint NOT NULL,
  "liked_at" timestamp DEFAULT CURRENT_TIMESTAMP,
  UNIQUE ("user_id", "event_id")
);

-- Tables additionnelles
CREATE TABLE "notifications" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" bigint NOT NULL,
  "message" text NOT NULL, -- Message d'erreur
  "is_read" boolean DEFAULT false,
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

-- Clés étrangères
ALTER TABLE "events" ADD FOREIGN KEY ("creator_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "registrations" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "registrations" ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id") ON DELETE CASCADE;
ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "likes" ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id") ON DELETE CASCADE;
ALTER TABLE "notifications" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

-- JEU DE DONNÉES DE TEST CONFORME
INSERT INTO users (first_name, last_name, email, password, bio) VALUES 
('Jean', 'Dupont', 'jean@email.com', 'bcrypt_hash_123', 'Passionné de tech'),
('Alice', 'Etu', 'alice@etu.fr', 'bcrypt_hash_456', 'Étudiante en M1');

INSERT INTO events (title, description, date, location, city, is_online, price, category, creator_id) VALUES 
('Atelier Vue.js', 'Apprendre les bases du front', '2025-04-15 19:00:00', 'Nautibus', 'Lyon', false, 0.0, 'Développement', 1),
('Webinaire Cloud', 'Le futur du SaaS', '2025-05-10 10:00:00', 'Zoom', 'En ligne', true, 15.0, 'Cloud', 1);
INSERT INTO registrations (user_id, event_id) VALUES (2, 1);