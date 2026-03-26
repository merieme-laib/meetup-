-- Nettoyage avant de créer
DROP TABLE IF EXISTS payments, notifications, comments, followers, likes, bookings, events, categories, locations, users CASCADE;

-- Création des tables
CREATE TABLE "users" (
  "id" SERIAL PRIMARY KEY,
  "username" varchar NOT NULL,
  "email" varchar UNIQUE NOT NULL,
  "password_hash" varchar NOT NULL,
  "role" varchar DEFAULT 'user',
  "avatar_url" varchar,
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "categories" (
  "id" SERIAL PRIMARY KEY,
  "name" varchar UNIQUE NOT NULL
);

CREATE TABLE "locations" (
  "id" SERIAL PRIMARY KEY,
  "name" varchar NOT NULL,
  "address" varchar,
  "capacity" integer
);

CREATE TABLE "events" (
  "id" SERIAL PRIMARY KEY,
  "title" varchar NOT NULL,
  "description" text,
  "event_date" timestamp NOT NULL,
  "max_participants" integer,
  "price" decimal DEFAULT 0,
  "status" varchar DEFAULT 'published',
  "cover_url" varchar,
  "location_id" integer,
  "category_id" integer,
  "creator_id" integer NOT NULL,
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "bookings" (
  "id" SERIAL PRIMARY KEY,
  "user_id" integer NOT NULL,
  "event_id" integer NOT NULL,
  "status" varchar DEFAULT 'confirmed',
  "is_scanned" boolean DEFAULT false,
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "likes" (
  "user_id" integer NOT NULL,
  "event_id" integer NOT NULL,
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP,
  UNIQUE ("user_id", "event_id")
);

CREATE TABLE "followers" (
  "follower_id" integer NOT NULL,
  "followed_id" integer NOT NULL,
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP,
  UNIQUE ("follower_id", "followed_id")
);

CREATE TABLE "comments" (
  "id" SERIAL PRIMARY KEY,
  "event_id" integer NOT NULL,
  "user_id" integer NOT NULL,
  "content" text NOT NULL,
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "notifications" (
  "id" SERIAL PRIMARY KEY,
  "user_id" integer NOT NULL,
  "message" text NOT NULL,
  "is_read" boolean DEFAULT false,
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "payments" (
  "id" SERIAL PRIMARY KEY,
  "booking_id" integer UNIQUE NOT NULL,
  "user_id" integer NOT NULL,
  "amount" decimal NOT NULL,
  "payment_status" varchar DEFAULT 'success',
  "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

-- Ajout des clés étrangères (Les flèches !)
ALTER TABLE "events" ADD FOREIGN KEY ("creator_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "events" ADD FOREIGN KEY ("category_id") REFERENCES "categories" ("id") ON DELETE SET NULL;
ALTER TABLE "events" ADD FOREIGN KEY ("location_id") REFERENCES "locations" ("id") ON DELETE SET NULL;

ALTER TABLE "bookings" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "bookings" ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id") ON DELETE CASCADE;

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "likes" ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id") ON DELETE CASCADE;

ALTER TABLE "followers" ADD FOREIGN KEY ("follower_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "followers" ADD FOREIGN KEY ("followed_id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "comments" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "comments" ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id") ON DELETE CASCADE;

ALTER TABLE "notifications" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "payments" ADD FOREIGN KEY ("booking_id") REFERENCES "bookings" ("id") ON DELETE CASCADE;
ALTER TABLE "payments" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

-- ==========================================
-- JEU DE DONNÉES DE TEST 
-- ==========================================
INSERT INTO users (username, email, password_hash, role) VALUES 
('Admin_Toto', 'admin@univ.fr', 'hash_123', 'admin'),
('Bob_BDE', 'bob@asso.fr', 'hash_456', 'user'),
('Alice_Etu', 'alice@etu.fr', 'hash_789', 'user');

INSERT INTO locations (name, address, capacity) VALUES 
('Nautibus - Salle C1', 'Campus', 40),
('Amphi Ampère', 'Campus', 300);

INSERT INTO categories (name) VALUES ('Esport'), ('Conférence');

INSERT INTO events (title, description, event_date, max_participants, price, location_id, category_id, creator_id) VALUES 
('Tournoi Super Smash Bros', 'Ramenez vos manettes GC !', '2026-04-15 18:00:00', 32, 2.00, 1, 1, 2),
('Conférence IA', 'Présentation du projet', '2026-04-20 14:00:00', 150, 0.00, 2, 2, 1);

INSERT INTO bookings (user_id, event_id, status) VALUES 
(3, 1, 'confirmed'),
(2, 2, 'confirmed');