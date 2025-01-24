CREATE TABLE contact_form
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255),
    email      VARCHAR(255),
    message    TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
