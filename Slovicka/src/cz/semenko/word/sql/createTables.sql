CREATE TABLE tables
(
   id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT tables_pk PRIMARY KEY,
   conn_string VARCHAR (225) NOT NULL
);

INSERT INTO tables (conn_string) VALUES ('dummy');

CREATE TABLE associations
(
   id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT associations_pk PRIMARY KEY,
   cell_id INTEGER,
   src_id INTEGER NOT NULL,
   src_tbl INTEGER NOT NULL,
   tgt_id INTEGER NOT NULL,
   tgt_tbl INTEGER NOT NULL,
   cost INTEGER NOT NULL
);

CREATE TABLE cells
(
   id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY CONSTRAINT cells_pk PRIMARY KEY,
   src VARCHAR(30),
   type SMALLINT
);

ALTER TABLE associations ADD CONSTRAINT cell_id_fk Foreign Key
(
   cell_id
)
REFERENCES cells (id);

ALTER TABLE associations ADD CONSTRAINT src_tables_fk Foreign Key
(
   src_tbl
)
REFERENCES tables (id);

ALTER TABLE associations ADD CONSTRAINT tgt_tables_fk Foreign Key
(
   tgt_tbl
)
REFERENCES tables (id);

ALTER TABLE associations ADD CONSTRAINT src_cells_fk Foreign Key
(
   src_id
)
REFERENCES cells (id);

ALTER TABLE associations ADD CONSTRAINT tgt_cells_fk Foreign Key
(
   tgt_id
)
REFERENCES cells (id);

create index cells_src_idx on cells (src);
create index cells_type_idx on cells (type);

create index assoc_cell_idx on associations(cell_id);
create index assoc_src_idx on associations(src_id);
create index assoc_tgt_idx on associations(tgt_id);
create index assoc_cost_idx on associations(cost);

ALTER TABLE cells ALTER COLUMN id RESTART WITH 0;
ALTER TABLE associations ALTER COLUMN id RESTART WITH 0;

-- Dummy cell to reference from removed associations
INSERT INTO cells (id, src, type) VALUES (0, NULL, 1);
--commit;
