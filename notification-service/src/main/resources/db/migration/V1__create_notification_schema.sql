create table notifications (
    id uuid primary key,
    consulta_id uuid not null,
    paciente_id uuid not null,
    tipo varchar(80) not null,
    status varchar(30) not null,
    payload text,
    tentativas integer not null,
    created_at timestamp with time zone not null,
    sent_at timestamp with time zone
);

create table processed_events (
    event_id uuid primary key,
    processed_at timestamp with time zone not null
);

