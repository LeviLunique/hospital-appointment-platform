-- A identidade do usuario passa a ser carregada pelo JWT emitido pelo scheduling-service.
-- O history-service deixa de manter sua propria tabela de usuarios.
drop table if exists users;
