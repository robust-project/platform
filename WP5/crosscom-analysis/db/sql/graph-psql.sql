DROP TABLE IF EXISTS cluster CASCADE;
CREATE TABLE cluster (
    cluster_format integer NOT NULL,
    end_date date NOT NULL,
    betweenness double precision,
    id bigint NOT NULL DEFAULT nextval('s_cluster_id'),
    norm_betweenness double precision,
    norm_overall_betweenness double precision,
    flag character varying(123),
    begin_date date NOT NULL,
    orig_index integer NOT NULL,
    cluster_index integer NOT NULL,
    slice_id bigint NOT NULL
);

DROP TABLE IF EXISTS cluster_structure CASCADE;
CREATE TABLE cluster_structure (
    betweenness double precision,
    vertexid bigint NOT NULL,
    clustering_coef double precision,
    cluster_id bigint NOT NULL
);


ALTER TABLE public.cluster_structure OWNER TO vacbel;

DROP TABLE IF EXISTS network_slice CASCADE;
CREATE TABLE network_slice (
    end_date date NOT NULL,
    id bigint NOT NULL DEFAULT nextval('s_network_slice_id'),
    type integer NOT NULL,
    begin_date date NOT NULL
);


DROP TABLE IF EXISTS network_slice_structure CASCADE;
CREATE TABLE network_slice_structure (
    weight double precision NOT NULL,
    source bigint NOT NULL,
    betweeness double precision,
    sink bigint NOT NULL,
    slice_id bigint NOT NULL
);

DROP TABLE IF EXISTS network_slice_vertex CASCADE;
CREATE TABLE network_slice_vertex (
    slice_id bigint NOT NULL,
    vertexid bigint NOT NULL
);


DROP SEQUENCE IF EXISTS s_cluster_id CASCADE;
CREATE SEQUENCE s_cluster_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


DROP SEQUENCE IF EXISTS s_network_slice_id CASCADE;
CREATE SEQUENCE s_network_slice_id
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE ONLY cluster
    ADD CONSTRAINT cluster_pkey PRIMARY KEY (id);

ALTER TABLE ONLY network_slice
    ADD CONSTRAINT network_slice_pkey PRIMARY KEY (id);

DROP TABLE IF EXISTS cluster_features;
 CREATE TABLE cluster_features (
 cluster_id integer NOT NULL,
 feature_type integer NOT NULL,
 feature_value double precision NOT NULL
 );
