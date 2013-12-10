CREATE TABLE item_similarity (
  vertex_1 BIGINT NOT NULL,
  vertex_2 BIGINT NOT NULL,
  similarity double NOT NULL,
  PRIMARY KEY(vertex_1, vertex_2)
);
