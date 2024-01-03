CREATE ROLE schwarz CREATEDB LOGIN PASSWORD '12345678!';

--- create schema 'library' ---
CREATE SCHEMA library AUTHORIZATION schwarz;

--- create table category in database 'schwarz_db' ---
CREATE TABLE library.category
(
    id          BIGSERIAL PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    created_on  TIMESTAMP NOT NULL DEFAULT now(),
    updated_on  TIMESTAMP NULL,
    CONSTRAINT description_uk UNIQUE (description)
);

--- create comments on columns ---
COMMENT ON COLUMN library.category.id IS 'Primary key.';
COMMENT ON COLUMN library.category.description IS 'The description of the category.';
COMMENT ON COLUMN library.category.created_on IS 'The creation time of the entry.';
COMMENT ON COLUMN library.category.updated_on IS 'The update time of the entry.';

--- create table customer in database 'schwarz_db' ---
CREATE TABLE library.customer
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(30) NOT NULL,
    email       VARCHAR(30) NOT NULL,
    password    VARCHAR(100) NOT NULL,
    created_on  TIMESTAMP NOT NULL DEFAULT now(),
    updated_on  TIMESTAMP NULL,
    CONSTRAINT email_uk UNIQUE (email)
);

--- create comments on columns ---
COMMENT ON COLUMN library.customer.id IS 'Primary key.';
COMMENT ON COLUMN library.customer.name IS 'The name of the customer.';
COMMENT ON COLUMN library.customer.email IS 'The email address of the customer.';
COMMENT ON COLUMN library.customer.password IS 'The password of the customer. Not encrypted.';
COMMENT ON COLUMN library.customer.created_on IS 'The creation time of the entry.';
COMMENT ON COLUMN library.customer.updated_on IS 'The update time of the entry.';


--- create table book in database 'schwarz_db' ---
CREATE TABLE library.book
(
    id              BIGSERIAL PRIMARY KEY,
    author          VARCHAR(30) NOT NULL,
    title           VARCHAR(100) NOT NULL,
    publisher       VARCHAR(50) NOT NULL,
    publishing_year DATE NOT NULL,
    category_id     INT8 NOT NULL,
    created_on      TIMESTAMP NOT NULL DEFAULT now(),
    updated_on      TIMESTAMP NULL,
    CONSTRAINT category_fk FOREIGN KEY (category_id) REFERENCES library.category(id)
);

--- create comments on columns ---
COMMENT ON COLUMN library.book.id IS 'Primary key.';
COMMENT ON COLUMN library.book.author IS 'Author of the book.';
COMMENT ON COLUMN library.book.title IS 'Title of the book.';
COMMENT ON COLUMN library.book.publisher IS 'The book publisher.';
COMMENT ON COLUMN library.book.publishing_year IS 'The year the book was published in "YYYY" format';
COMMENT ON COLUMN library.book.category_id IS 'The ID of the category, to which the book was registered.';
COMMENT ON COLUMN library.book.created_on IS 'The creation time of the entry.';
COMMENT ON COLUMN library.book.updated_on IS 'The update time of the entry.';

--- grant privileges on schema ---
GRANT ALL PRIVILEGES ON SCHEMA library TO schwarz;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA library TO schwarz;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA library TO schwarz;