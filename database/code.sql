-- Nettoyage avant de créer
DROP TABLE IF EXISTS payments, notifications, comments, followers, likes, registrations, events, users CASCADE;

-- 1.1 User  
CREATE TABLE "users"
(
    "id"         BIGSERIAL PRIMARY KEY,              -- Identifiant unique Long
    "first_name" varchar        NOT NULL,            -- Prénom requis
    "last_name"  varchar        NOT NULL,            -- Nom requis
    "email"      varchar UNIQUE NOT NULL,            -- Identifiant de connexion
    "password"   varchar        NOT NULL,            -- Mot de passe hashé
    "bio"        text,                               -- Description courte
    "avatar_url" varchar,                            -- URL de l'image
    "role"       varchar   DEFAULT 'user',           -- rôle
    "is_deleted" boolean   DEFAULT false,            -- 0 : non supprimé  |  1 : supprimé
    "created_at" timestamp DEFAULT CURRENT_TIMESTAMP -- ISO 8601
);

-- 1.3 Event 
CREATE TABLE "events"
(
    "id"                 BIGSERIAL PRIMARY KEY,
    "title"              varchar   NOT NULL,             -- Titre requis
    "description"        text      NOT NULL,             -- Description requise
    "date"               timestamp NOT NULL,             -- Date et heure ISO 8601
    "location"           varchar   NOT NULL,             -- Adresse ou nom du lieu
    "city"               varchar   NOT NULL,             -- Ville requise
    "is_online"          boolean          DEFAULT false, -- true si en ligne
    "image_url"          varchar,                        -- URL de couverture
    "price"              double precision DEFAULT 0.0,   -- Prix en euros
    "max_participants"   integer,                        -- Capacité (null = illimitée)
    "category"           varchar   NOT NULL,             -- Valeur fixe
    "creator_id"         bigint    NOT NULL,             -- ID du créateur
    "participants_count" integer          DEFAULT 0,     -- 0 participants de base
    "likes_count"        integer          DEFAULT 0,     -- 0 like de base
    "is_cancelled"       boolean          DEFAULT false, -- 0 : L'événement est maintenu  |  1 : L'événement est annulé
    "created_at"         timestamp        DEFAULT CURRENT_TIMESTAMP
);

-- 1.4 Registration 
CREATE TABLE "registrations"
(
    "id"            BIGSERIAL PRIMARY KEY,
    "user_id"       bigint NOT NULL,
    "event_id"      bigint NOT NULL,
    "registered_at" timestamp DEFAULT CURRENT_TIMESTAMP -- ISO 8601
);

-- 1.5 Like 
CREATE TABLE "likes"
(
    "id"       BIGSERIAL PRIMARY KEY,
    "user_id"  bigint NOT NULL,
    "event_id" bigint NOT NULL,
    "liked_at" timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE ("user_id", "event_id")
);

-- Tables additionnelles
CREATE TABLE "notifications"
(
    "id"         BIGSERIAL PRIMARY KEY,
    "user_id"    bigint NOT NULL,
    "message"    text   NOT NULL, -- Message d'erreur
    "is_read"    boolean   DEFAULT false,
    "created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);

-- Clés étrangères
ALTER TABLE "events"
    ADD FOREIGN KEY ("creator_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "registrations"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "registrations"
    ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id") ON DELETE CASCADE;
ALTER TABLE "likes"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "likes"
    ADD FOREIGN KEY ("event_id") REFERENCES "events" ("id") ON DELETE CASCADE;
ALTER TABLE "notifications"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

-- JEU DE DONNÉES DE TEST AVEC RÔLES (PASSWORD: Meetup2026!)
INSERT INTO users (first_name, last_name, email, password, bio, role)
VALUES ('Merieme', 'Laib', 'merieme@meetup.fr', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Co-fondatrice Meetup - Étudiant Master Info Lyon 1', 'admin'),
       ('Yasmina', 'Abbas', 'yasmina@meetup.fr', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Co-fondatrice Meetup - Étudiant Master Info Lyon 1', 'admin'),
       ('Miloud', 'Mezianne', 'miloud@meetup.fr', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Co-fondateur Meetup - Étudiant Master Info Lyon 1', 'admin'),
       ('Doaa', 'Samadi', 'doaa@meetup.fr', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Co-fondatrice Meetup - Étudiant Master Info Lyon 1', 'admin'),
       ('Gabriel', 'Debotte', 'debotte@meetup.fr', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Co-fondateur Meetup - Étudiant Master Info Lyon 1', 'admin'),
       ('Oscar', 'Gibert', 'oscar.gibert@meetup.fr', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Co-fondateur Meetup - Étudiant Master Info Lyon 1', 'admin'),

       ('Jean', 'Dupont', 'jean@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionné de tech', 'user'),
       ('Alice', 'Etu', 'alice@etu.fr', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiante en M1', 'user'),
       ('Hercule', 'Farel', 'herculefarel@gmail.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Admin principal du site', 'user'),
       ('Lucas', 'Martin', 'lucas.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de randonnée et de photo', 'user'),
       ('Emma', 'Bernard', 'emma.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Développeuse Python à Lyon', 'user'),
       ('Thomas', 'Petit', 'thomas.p@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Cherche des partenaires de tennis', 'user'),
       ('Léa', 'Robert', 'lea.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Amatrice de jeux de société', 'user'),
       ('Nathan', 'Richard', 'nathan.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiant en design graphique', 'user'),
       ('Chloé', 'Durand', 'chloe.d@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'J''aime cuisiner des plats italiens', 'user'),
       ('Enzo', 'Dubois', 'enzo.d@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionné de cybersécurité', 'user'),
       ('Manon', 'Moreau', 'manon.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Exploratrice urbaine', 'user'),
       ('Louis', 'Laurent', 'louis.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Guitariste dans un groupe de rock', 'user'),
       ('Jade', 'Simon', 'jade.s@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Développeuse Fullstack passionnée', 'user'),
       ('Hugo', 'Michel', 'hugo.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de basket et de NBA', 'user'),
       ('Sarah', 'Lefebvre', 'sarah.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Adore les chats et le JS', 'user'),
       ('Arthur', 'Leroy', 'arthur.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Toujours prêt pour une bière', 'user'),
       ('Inès', 'Roux', 'ines.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Apprend le japonais en solo', 'user'),
       ('Paul', 'David', 'paul.d@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Ingénieur son le jour, DJ la nuit', 'user'),
       ('Lola', 'Bertrand', 'lola.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Coach en bien-être', 'user'),
       ('Théo', 'Morel', 'theo.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Collectionneur de vinyles', 'user'),
       ('Camille', 'Fournier', 'camille.f@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de cinéma d''auteur', 'user'),
       ('Noah', 'Girard', 'noah.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Geek et fier de l''être', 'user'),
       ('Zoé', 'Bonnet', 'zoe.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiante en sociologie', 'user'),
       ('Rayan', 'Muller', 'rayan.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de foot (Allez l''OL !)', 'user'),
       ('Louise', 'Lambert', 'louise.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Artiste peintre à ses heures', 'user'),
       ('Adam', 'Fontaine', 'adam.f@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Entrepreneur dans l''âme', 'user'),
       ('Clara', 'Rousseau', 'clara.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Yoga addict', 'user'),
       ('Jules', 'Vincent', 'jules.v@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Spécialiste Cloud & Azure', 'user'),
       ('Mila', 'Guerin', 'mila.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan d''escape games', 'user'),
       ('Sacha', 'Boyer', 'sacha.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Amateur de trail en montagne', 'user'),
       ('Alice', 'Gerard', 'alice.g2@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionnée d''histoire lyonnaise', 'user'),
       ('Victor', 'Mercier', 'victor.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Développeur Rust curieux', 'user'),
       ('Elena', 'Caron', 'elena.c@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de littérature fantastique', 'user'),
       ('Nolan', 'Legendre', 'nolan.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Pratique le skate depuis 10 ans', 'user'),
       ('Agathe', 'Lemoine', 'agathe.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Végétarienne et écolo', 'user'),
       ('Yaniss', 'Gauthier', 'yaniss.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Expert en IA et Data Science', 'user'),
       ('Sofia', 'Dumont', 'sofia.d@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Adore voyager sac au dos', 'user'),
       ('Kylian', 'Masson', 'kylian.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de rap français', 'user'),
       ('Eva', 'Marchand', 'eva.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiante en droit', 'user'),
       ('Simon', 'Rivière', 'simon.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de retrogaming', 'user'),
       ('Louna', 'Philippe', 'louna.p@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Amatrice de théâtre impro', 'user'),
       ('Gabriel', 'Leclerc', 'gabriel.l2@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Brasseur de bière amateur', 'user'),
       ('Romane', 'Renaud', 'romane.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Photographe de rue', 'user'),
       ('Timéo', 'Lacroix', 'timeo.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Apprenti pâtissier', 'user'),
       ('Lili', 'Gaillard', 'lili.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan d''astronomie', 'user'),
       ('Noé', 'Brunet', 'noe.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Joueur de poker passionné', 'user'),
       ('Anna', 'Sébastien', 'anna.s@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Blogueuse voyage', 'user'),
       ('Samy', 'Boulanger', 'samy.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Organisateur de LAN party', 'user'),
       ('Lucie', 'Barbier', 'lucie.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de mode vintage', 'user'),
       ('Marius', 'Giraud', 'marius.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Sculpteur sur bois', 'user'),
       ('Rose', 'Joly', 'rose.j@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Adore jardiner sur son balcon', 'user'),
       ('Axel', 'Pons', 'axel.p@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiant en STAPS', 'user'),
       ('Célia', 'Le Gall', 'celia.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Violoniste en conservatoire', 'user'),
       ('Mathieu', 'Perrin', 'mathieu.p@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan d''escalade à Vertical''Art', 'user'),
       ('Clémence', 'Lemaire', 'clemence.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Développeuse Mobile (Flutter/Swift)', 'user'),
       ('Maxime', 'Dufour', 'maxime.d@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Expert en vins et spiritueux', 'user'),
       ('Inès', 'Blanchard', 'ines.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiante en architecture', 'user'),
       ('Valentin', 'Bonneau', 'valentin.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de jeux de rôles et de SF', 'user'),
       ('Justine', 'Gillet', 'justine.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Prof de yoga et méditation', 'user'),
       ('Bastien', 'Hubert', 'bastien.h@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Data Analyst passionné de stats', 'user'),
       ('Manon', 'Maillard', 'manon.m2@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionnée de cuisine japonaise', 'user'),
       ('Corentin', 'Salvador', 'corentin.s@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Cherche du monde pour du futsal', 'user'),
       ('Camille', 'Teixeira', 'camille.t@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Freelance en marketing digital', 'user'),
       ('Florian', 'Vasseur', 'florian.v@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de modélisme et de drones', 'user'),
       ('Noémie', 'Lecomte', 'noemie.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Joueuse de harpe en orchestre', 'user'),
       ('Alexis', 'Ferry', 'alexis.f@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Organisateur de tournois E-sport', 'user'),
       ('Margaux', 'Garnier', 'margaux.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Adore le street-art lyonnais', 'user'),
       ('Killian', 'Navarro', 'killian.n@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiant en école de commerce', 'user'),
       ('Salomé', 'Lopes', 'salome.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de poterie et de céramique', 'user'),
       ('Loïc', 'Gomez', 'loic.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Développeur Java Senior', 'user'),
       ('Élisa', 'Schmitt', 'elisa.s@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionnée par l''écologie', 'user'),
       ('Robin', 'Weber', 'robin.w@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de rando en solitaire', 'user'),
       ('Julia', 'Colin', 'julia.c@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Photographe de mariage', 'user'),
       ('Quentin', 'Vidal', 'quentin.v@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionné d''astrophysique', 'user'),
       ('Romane', 'Langlois', 'romane.l2@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiante en langues étrangères', 'user'),
       ('Cédric', 'Guillaume', 'cedric.g@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Collectionneur de timbres', 'user'),
       ('Éloïse', 'Le Gall', 'eloise.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Amatrice d''opéra et de danse', 'user'),
       ('Marvin', 'Diallo', 'marvin.d@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Vidéaste sur YouTube', 'user'),
       ('Océane', 'Fernandez', 'oceane.f@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Cherche des cours de salsa', 'user'),
       ('Tristan', 'Rey', 'tristan.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de surf et de skate', 'user'),
       ('Clara', 'Jacquet', 'clara.j@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Étudiante en psychologie', 'user'),
       ('Dorian', 'Begue', 'dorian.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Expert en domotique', 'user'),
       ('Hanaé', 'Traoré', 'hanae.t@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de BD et mangas', 'user'),
       ('Alban', 'Evrard', 'alban.e@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Animateur nature', 'user'),
       ('Mélina', 'Parent', 'melina.p@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fait du bénévolat en SPA', 'user'),
       ('William', 'Hebert', 'william.h@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de CrossFit', 'user'),
       ('Tessa', 'Lacombe', 'tessa.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Journaliste indépendante', 'user'),
       ('Esteban', 'Pinto', 'esteban.p@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionné par l''espace', 'user'),
       ('Ambre', 'Allard', 'ambre.a@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de tricot moderne', 'user'),
       ('Jérémy', 'Boucher', 'jeremy.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Développeur C++ à Grenoble', 'user'),
       ('Lise', 'Carlier', 'lise.c@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Adore le thalasso et spa', 'user'),
       ('Malo', 'Rousseau', 'malo.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionné par l''histoire de France', 'user'),
       ('Maëlle', 'Mace', 'maelle.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Illustratrice jeunesse', 'user'),
       ('Augustin', 'Henry', 'augustin.h@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Chercheur en biologie moléculaire', 'user'),
       ('Clarisse', 'Voisin', 'clarisse.v@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de fitness et nutrition', 'user'),
       ('Émile', 'Besson', 'emile.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Bricoleur du dimanche', 'user'),
       ('Léa', 'Gilles', 'lea.g2@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Adore les comédies musicales', 'user'),
       ('Gaspard', 'Bouchet', 'gaspard.b@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Expert en domotique et IoT', 'user'),
       ('Wendy', 'Mallet', 'wendy.m@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Digital Nomad à Bali (parfois)', 'user'),
       ('Oscar', 'Lebrun', 'oscar.l2@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Un autre Oscar, fan de code !', 'user'),
       ('Iris', 'Reynaud', 'iris.r@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Passionnée de piano classique', 'user'),
       ('Yanis', 'Fleury', 'yanis.f@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Fan de manga et de gaming', 'user'),
       ('Capucine', 'Leveque', 'capucine.l@email.com', '$2a$10$MBUhaIRLmU0OrBkTHpsQZ.3FPRk1a.kN.K.Xh3FKfrP38vdyYa1qm',
        'Adore cuisiner des pâtisseries', 'user');

INSERT INTO events (title, description, date, location, city, is_online, price, max_participants, image_url, category,
                    creator_id)
VALUES
-- Le profil "Tech Lead" (ID 7 - Jean)
('Masterclass Java 21', 'Les nouveautés de la version 21 en profondeur.', '2026-05-12 14:00:00', 'Bâtiment Nautibus',
 'Lyon', false, 0.0, 50, 'https://www.flsh.unilim.fr/wp-content/uploads/sites/9/2015/10/IMG_2641.jpg', 'Développement',
 7),
('Spring Boot vs Quarkus', 'Le match des frameworks cloud-native.', '2026-06-10 18:30:00', 'En ligne', 'Web', true, 0.0,
 100,
 'https://www.openmindt.com/wp-content/uploads/2023/11/how-to-choose-your-programming-language-for-your-software.jpg',
 'Développement', 7),
('Clean Architecture Workshop', 'Coder pour que ça dure.', '2026-07-05 10:00:00', 'Nautibus', 'Lyon', false, 15.0, 30,
 'https://flash.cegepgarneau.ca/medias/photos/2025/09/_1920x1080_crop_center-center_none/TAG-Groupe-et-prof.jpg',
 'Développement', 7),

-- L'étudiante investie (ID 8 - Alice)
('Révisions Collectives M1', 'Entraide pour les partiels de juin.', '2026-05-20 14:00:00', 'BU Sciences', 'Lyon', false,
 0.0, 20, 'https://cache.magicmaman.com/data/photo/w1000_ci/4w/surligneur-fiche.jpg', 'Associatif', 8),
('Yoga spécial Stress', 'Relâcher la pression avant les examens.', '2026-05-22 08:00:00', 'Parc Tête d''Or', 'Lyon',
 false, 0.0, 15,
 'https://cdn-s-www.ledauphine.com/images/38C07123-1DAD-40E2-B46F-A813240436A6/NW_raw/title-1754413751.jpg',
 'Bien-être', 8),

-- L'asso de Sport (ID 10 - Lucas)
('Foot à 5 amical', 'On cherche 2 joueurs pour compléter l''équipe.', '2026-05-15 19:00:00', 'Urban Soccer',
 'Saint-Priest', false, 8.0, 2,
 'https://prmeng.rosselcdn.net/sites/default/files/dpistyles_v2/ena_16_9_extra_big/2022/04/12/node_296783/38959568/public/2022/04/12/B9730576618Z.1_20220412172024_000%2BGSSK9OF0C.2-0.jpg?itok=DreeRUCV1649776838',
 'Sport', 10),
('Sortie Vélo de route', 'Boucle de 50km dans les Monts d''Or.', '2026-05-24 09:00:00', 'Quais de Saône', 'Lyon', false,
 0.0, 12,
 'https://woody.cloudly.space/app/uploads/lesportesdusoleil/2024/04/thumbs/Velo_Route_PDS_DDAHER-0064-1920x960.webp',
 'Sport', 10),
('Initiation Escalade Bloc', 'Découverte de l''escalade sans corde.', '2026-06-05 18:00:00', 'Hold Up', 'Lyon', false,
 12.0, 15, 'https://blocbuster.fr/wp-content/uploads/2022/08/img7.jpg', 'Sport', 10),

-- Le Gamer (ID 34 - Jules)
('Tournoi Super Smash Bros', 'Ramenez vos manettes GameCube !', '2026-05-30 20:00:00', 'Bar Meltdown', 'Lyon', false,
 5.0, 32, 'https://cdn.mos.cms.futurecdn.net/WhwU833s7KkLPbieK2eYcM-1510-80.jpg', 'Gaming', 34),
('Soirée Raid WoW', 'Clean du dernier palier en guilde.', '2026-06-01 21:00:00', 'Discord', 'En ligne', true, 0.0, 200,
 'https://studio71games.com/wp-content/uploads/2025/04/4-4rcoMlw.jpg', 'Gaming', 34),

-- L'organisateur pro de Networking (ID 32 - Adam)
('Apéro des Entrepreneurs', 'Pitcher son projet en 2 minutes.', '2026-05-18 18:30:00', 'Hôtel-Dieu', 'Lyon', false, 0.0,
 60, 'https://mieuxentreprendre.fr/wp-content/uploads/2026/01/1765983763397.jpg', 'Networking', 32),
('Business Lunch', 'Déjeuner networking spécial Tech.', '2026-05-25 12:30:00', 'Brasserie des Brotteaux', 'Lyon', false,
 25.0, 25, 'https://otantik.ae/wp-content/uploads/2024/12/Business-Lunch-Img-1.jpg', 'Business', 32),
('Afterwork Co-fondateurs', 'Trouve ton associé idéal.', '2026-06-15 19:00:00', 'Le Sirius', 'Lyon', false, 0.0, 80,
 'https://www.solainn-plateforme.fr/wp-content/uploads/2026/01/comment-rencontrer-des-entrepreneurs.jpg', 'Networking',
 32),

-- Quelques événements "one-shot"
('Atelier Couture Débutant', 'Réparer ses vêtements soi-même.', '2026-06-12 14:00:00', 'Maison des Associations',
 'Lyon', false, 10.0, 8, 'https://www.mercipourlechocolat.fr/wp-content/uploads/2022/05/ateliercouturemaison05.jpg',
 'Loisirs', 50),
('Conférence IA & Éthique', 'L''IA va-t-elle nous remplacer ?', '2026-06-28 18:00:00', 'Amphi Turing', 'Lyon', false,
 0.0, 150, 'https://www.eventdrive.com/hubfs/Imported_Blog_Media/7-tips-pour-organiser-une-conference-qui-cartonne.jpg',
 'IA', 42),
('Jam Session Jazz', 'Venez avec vos instruments !', '2026-07-10 21:00:00', 'La Clef de Voûte', 'Lyon', false, 0.0, 40,
 'https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=400&q=80', 'Musique', 56),
('Visite Street Art', 'Découverte des fresques de la Croix-Rousse.', '2026-05-29 15:00:00', 'Sortie Métro C', 'Lyon',
 false, 5.0, 25,
 'https://cdn.prod.website-files.com/60e1f68bc720984ae7e18521/615adb33889251b7ddcba68a_histoire-lyon-fresque-canuts.jpeg',
 'Culture', 78);


-- 1. NETTOYAGE
DELETE
FROM registrations;
DELETE
FROM likes;

-- 2. GÉNÉRATION DES INSCRIPTIONS
INSERT INTO registrations (user_id, event_id)
SELECT u.id, e.id
FROM users u
         CROSS JOIN LATERAL (
    SELECT id, category
    FROM events
    WHERE (
              (category IN ('Sport', 'Loisirs') AND (random() + u.id * 0) < 0.03)
                  OR
              (category NOT IN ('Sport', 'Loisirs') AND (random() + u.id * 0) < 0.15)
              )
        ) e
    ON CONFLICT DO NOTHING;

-- 3. GÉNÉRATION DES LIKES
INSERT INTO likes (user_id, event_id)
SELECT u.id, e.id
FROM users u,
     events e
WHERE random() < 0.10 -- 10% de chance qu'un user aime un event
    ON CONFLICT DO NOTHING;

-- 4. SYNCHRONISATION DES COMPTEURS
UPDATE events e
SET participants_count = (SELECT COUNT(*)
                          FROM registrations r
                          WHERE r.event_id = e.id),
    likes_count        = (SELECT COUNT(*)
                          FROM likes l
                          WHERE l.event_id = e.id);