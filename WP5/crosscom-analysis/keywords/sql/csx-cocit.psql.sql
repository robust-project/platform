
CREATE TABLE doi
(
  doi character varying(100) NOT NULL,
  articleid bigint NOT NULL,
  CONSTRAINT doi_articleid_pk PRIMARY KEY (articleid )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE doi
  OWNER TO vacbel;
-- Table: keywords_em

-- DROP TABLE keywords_em;

CREATE TABLE keywords_em
(
  id bigserial NOT NULL,
  articleid bigint NOT NULL,
  keyword character varying(500) NOT NULL,
  frequency integer,
  doi character varying(100) NOT NULL,
  keywordid bigint NOT NULL,
  CONSTRAINT keywords_em_pkey PRIMARY KEY (id ),
  CONSTRAINT fk_keyword_id FOREIGN KEY (keywordid)
      REFERENCES keyword_id (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE keywords_em
  OWNER TO vacbel;

-- Index: fki_keywords_em_keyword_id

-- DROP INDEX fki_keywords_em_keyword_id;

CREATE INDEX fki_keywords_em_keyword_id
  ON keywords_em
  USING btree
  (keywordid );

-- Index: fki_keywords_em_papers_doi

-- DROP INDEX fki_keywords_em_papers_doi;

CREATE INDEX fki_keywords_em_papers_doi
  ON keywords_em
  USING btree
  (doi COLLATE pg_catalog."default" );

-- Table: keyword_id

-- DROP TABLE keyword_id;

CREATE TABLE keyword_id
(
  id bigint NOT NULL,
  keyword character varying(500) NOT NULL,
  orig character varying(500) NOT NULL,
  CONSTRAINT keyword_id_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE keyword_id
  OWNER TO vacbel;

-- Index: idx_keyword_id_id

-- DROP INDEX idx_keyword_id_id;

CREATE INDEX idx_keyword_id_id
  ON keyword_id
  USING btree
  (id );

-- Table: ranked_keywords_objects

-- DROP TABLE ranked_keywords_objects;

CREATE TABLE ranked_keywords_objects
(
  keywordid bigint NOT NULL,
  tfidf double precision NOT NULL,
  slice_id bigint NOT NULL,
  objectid bigint NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ranked_keywords_objects
  OWNER TO vacbel;
