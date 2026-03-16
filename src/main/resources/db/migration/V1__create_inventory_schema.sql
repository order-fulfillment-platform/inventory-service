CREATE TABLE products (
    id                  UUID            NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    available_quantity  INTEGER         NOT NULL,
    price               NUMERIC(10,2)   NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE reservations (
    id                  UUID            NOT NULL,
    order_id            UUID            NOT NULL,
    product_id          UUID            NOT NULL,
    quantity_reserved   INTEGER         NOT NULL,
    status              VARCHAR(50)     NOT NULL,
    created_at          TIMESTAMP       NOT NULL,
    CONSTRAINT pk_reservations PRIMARY KEY (id)
);

CREATE TABLE outbox_events (
    id                  UUID            NOT NULL,
    aggregate_id        UUID            NOT NULL,
    event_type          VARCHAR(100)    NOT NULL,
    payload             TEXT            NOT NULL,
    created_at          TIMESTAMP       NOT NULL,
    processed           BOOLEAN         NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_outbox_events PRIMARY KEY (id)
);

CREATE INDEX idx_outbox_events_processed
    ON outbox_events(processed)
    WHERE processed = FALSE;

CREATE INDEX idx_reservations_order_id
    ON reservations(order_id);