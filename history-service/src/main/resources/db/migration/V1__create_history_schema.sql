create table users (
    id uuid primary key,
    username varchar(120) not null unique,
    password_hash varchar(120) not null,
    role varchar(30) not null,
    paciente_id uuid
);

create table appointment_history (
    id uuid primary key,
    paciente_id uuid not null,
    medico_id uuid not null,
    data_hora_inicio timestamp with time zone not null,
    data_hora_fim timestamp with time zone,
    status varchar(30) not null,
    motivo varchar(255),
    observacoes_clinicas text,
    updated_at timestamp with time zone not null
);

insert into users (id, username, password_hash, role, paciente_id) values
('4d2f88ef-8b8a-4d9a-a96f-808b1774bc2f', 'medico', '$2a$10$kd5M6qqC6aU7NowNmwkp9ulhvFYnuGfWxCgKaP2APB8B2pMjjXYfe', 'MEDICO', null),
('1b5c76ab-dc02-44f7-9a9a-3e65bf7fd4da', 'enfermeiro', '$2a$10$CLjVhF2BhraU8IRosxjuKeXZZE4Ig7HM/TaVVfrrHP6bEh.KBXYkW', 'ENFERMEIRO', null),
('7c0562f6-f14f-4a42-9dcc-8a2737eb8871', 'paciente', '$2a$10$1ahRmMjvtVBL7VbyzpfazeJ6aLFfqjwG/DdtXEmcJ5w2/lhnk5X5y', 'PACIENTE', '7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44');

insert into appointment_history (id, paciente_id, medico_id, data_hora_inicio, data_hora_fim, status, motivo, observacoes_clinicas, updated_at) values
('a087c948-0973-4d3e-bf82-e05510f34b64', '7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44', '4d2f88ef-8b8a-4d9a-a96f-808b1774bc2f', '2026-06-10T14:00:00-03:00', '2026-06-10T14:30:00-03:00', 'AGENDADA', 'Retorno clinico', 'Paciente orientado a manter medicacao prescrita.', current_timestamp),
('bc385c9d-c2d9-4d68-b617-17958e79e8d7', '7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44', '4d2f88ef-8b8a-4d9a-a96f-808b1774bc2f', '2025-01-10T09:00:00-03:00', '2025-01-10T09:30:00-03:00', 'REALIZADA', 'Consulta inicial', 'Paciente sem intercorrencias.', current_timestamp);
