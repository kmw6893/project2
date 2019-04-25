CREATE USER 'cust'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON *. * to 'cust'@'%';
CREATE DATABASE catalog;
USE catalog

CREATE TABLE category(
	id INTEGER NOT NULL,
	name VARCHAR (100),
	description VARCHAR (1000),
	PRIMARY KEY (id)
);

CREATE TABLE product(
	id INTEGER NOT NULL,
	category_id INTEGER NOT NULL,
	sku INTEGER,
	item_count INTEGER,
	threshold INTEGER,
	recorder_amount INTEGER,
	title VARCHAR (100),
	description VARCHAR (1000),
	cost FLOAT,
	FOREIGN KEY (category_id) REFERENCES category(id),
	PRIMARY KEY(id)
);