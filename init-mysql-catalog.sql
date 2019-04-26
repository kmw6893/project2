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

INSERT INTO category(
    id,
    name,
    description)
VALUES(
    4382,
    "Eight Track Tapes",
    "Our collection of the best 8-track tapes of era.  Listen to the BeeJees, Peter Frampton and other classics."
);

INSERT INTO category(
    id,
    name,
    description)
VALUES(
    8392,
    "Toys",
    "Our collection of current and vintage toys.  Here you'll find highly rated toys that will give years of playing pleasure."
);

INSERT INTO product(
    id,
	category_id,
	sku,
	item_count,
	threshold,
	recorder_amount,
	title,
	description,
	cost
    )
VALUES(
    8819,
    4382,
    13,
    2,
    1,
    10,
    "Dark Side of the Moon",
    "A classic Pink Floyd album",
    8.99
);

INSERT INTO product(
    id,
	category_id,
	sku,
	item_count,
	threshold,
	recorder_amount,
	title,
	description,
	cost
    )
VALUES(
    3682,
    4382,
    13,
    4,
    2,
    10,
    "The Cars",
    "The Cars is the debut album by the American new wave band the Cars. It was released on June 6, 1978 on Elektra Records. The album, which featured the three charting singles \"Just What I Needed,\" \"My Best Friend's Girl,\" and \"Good Times Roll,\" as well as several album-oriented rock radio hits, was a major success for the band, remaining on the charts for 139 weeks. It has been recognized as one of the band\'s best albums. -- wikipedia",
    4.95
);

INSERT INTO product(
    id,
	category_id,
	sku,
	item_count,
	threshold,
	recorder_amount,
	title,
	description,
	cost
    )
VALUES(
    9062,
    8392,
    12,
    45,
    10,
    50,
    "Rubic's Cube",
    "A mindbending toy, turn the sides to move color tiles.  The objective is to arange the cube so that each side is one color.",
    5.00
);

