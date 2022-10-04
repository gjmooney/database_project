INSERT INTO person (employee_id, name) VALUES (1, 'Hideo Kojima');
INSERT INTO person (employee_id, name) VALUES (2, 'Yosuke Matsuda');
INSERT INTO person (employee_id, name) VALUES (3, 'Donald Glove');
INSERT INTO person (employee_id, name) VALUES (4, 'Randy Yuske');
INSERT INTO person (employee_id, name) VALUES (5, 'Mike Timber');
INSERT INTO person (employee_id, name) VALUES (6, 'Barry Manillow');
INSERT INTO person (employee_id, name) VALUES (7, 'Roger Sugoe');
INSERT INTO person (employee_id, name) VALUES (8, 'Chris Granite');
INSERT INTO person (employee_id, name) VALUES (9, 'Peter Parker');
INSERT INTO person (employee_id, name) VALUES (10, 'Holly Ruther');

INSERT INTO publisher (company_id, name) VALUES (1, 'Kojima Productions');
INSERT INTO publisher (company_id, name) VALUES (2, 'Square Enix');
INSERT INTO publisher (company_id, name) VALUES (3, 'Really Sweet Games');


INSERT INTO works_for (employee_id, company_id) VALUES (1, 1);
INSERT INTO works_for (employee_id, company_id) VALUES (2, 2);
INSERT INTO works_for (employee_id, company_id) VALUES (3, 1);
INSERT INTO works_for (employee_id, company_id) VALUES (4, 3);
INSERT INTO works_for (employee_id, company_id) VALUES (6, 1);
INSERT INTO works_for (employee_id, company_id) VALUES (7, 2);
INSERT INTO works_for (employee_id, company_id) VALUES (8, 1);
INSERT INTO works_for (employee_id, company_id) VALUES (9, 3);

INSERT INTO designer (employee_id, salary, employment_date) VALUES (1, 80000, '2005-04-01');
INSERT INTO designer (employee_id, salary, employment_date) VALUES (3, 65000, '2009-01-01');
INSERT INTO designer (employee_id, salary, employment_date) VALUES (6, 40000, '2014-03-01');
INSERT INTO designer (employee_id, salary, employment_date) VALUES (7, 110000, '2020-06-01');
INSERT INTO designer (employee_id, salary, employment_date) VALUES (8, 50000, '2002-10-01');
INSERT INTO designer (employee_id, salary, employment_date) VALUES (9, 75000, '1998-12-01');

INSERT INTO ceo (employee_id) VALUES (2);
INSERT INTO ceo (employee_id) VALUES (4);

INSERT INTO game (game_id, title, profit, genre, release_date) VALUES (1, 'Death Stranding', 27000000, 'Action', '2019-11-08');
INSERT INTO game (game_id, title, profit, genre, release_date) VALUES (2, 'Final Fantasy VII', 21000000, 'RPG', '1997-12-31');
INSERT INTO game (game_id, title, profit, genre, release_date) VALUES (1, 'Pikmin', 27000000, 'Strategy', '2001-10-26');
INSERT INTO game (game_id, title, profit, genre, release_date) VALUES (2, 'Wooden Turbine Gas', 21000000, 'Stealth', '2021-12-12');

INSERT INTO works_on (employee_id, game_id) VALUES (1, 1);
INSERT INTO works_on (employee_id, game_id) VALUES (3, 3);
INSERT INTO works_on (employee_id, game_id) VALUES (6, 2);
INSERT INTO works_on (employee_id, game_id) VALUES (7, 2);
INSERT INTO works_on (employee_id, game_id) VALUES (8, 4);
INSERT INTO works_on (employee_id, game_id) VALUES (9, 4);

INSERT INTO publish (company_id, game_id) VALUES (1, 1);
INSERT INTO publish (company_id, game_id) VALUES (2, 2);
INSERT INTO publish (company_id, game_id) VALUES (3, 3);
INSERT INTO publish (company_id, game_id) VALUES (3, 4);

INSERT INTO rating (game_id, reviewer, score) VALUES (1, 'IGN', 8);
INSERT INTO rating (game_id, reviewer, score) VALUES (2, 'GameSpot', 10);
INSERT INTO rating (game_id, reviewer, score) VALUES (3, 'Dunkey', 9);
INSERT INTO rating (game_id, reviewer, score) VALUES (4, 'GameSpot', 4);
