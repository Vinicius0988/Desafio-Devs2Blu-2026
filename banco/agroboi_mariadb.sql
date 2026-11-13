-- AgroBoi - banco simplificado para MariaDB 10.6 ou superior.
-- Execute uma vez em um banco novo. Validacoes de negocio ficam no backend.

CREATE DATABASE IF NOT EXISTS agroboi
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agroboi;

CREATE TABLE fazenda (
    id BIGINT NOT NULL DEFAULT 1 PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    nome_responsavel VARCHAR(150) NOT NULL,
    municipio VARCHAR(100) NULL,
    uf CHAR(2) NULL,
    endereco VARCHAR(255) NULL,
    CHECK (id = 1)
) ENGINE=InnoDB;

CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL DEFAULT 'ADMINISTRADOR',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE raca (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE animal (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    identificacao VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(100) NULL,
    sexo VARCHAR(5) NOT NULL,
    data_nascimento DATE NULL,
    raca_id BIGINT NULL,
    mae_id BIGINT NULL,
    pai_id BIGINT NULL,
    situacao VARCHAR(15) NOT NULL DEFAULT 'NA_FAZENDA',
    observacoes TEXT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (raca_id) REFERENCES raca (id),
    FOREIGN KEY (mae_id) REFERENCES animal (id),
    FOREIGN KEY (pai_id) REFERENCES animal (id)
) ENGINE=InnoDB;

CREATE TABLE pesagem (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    data_hora DATETIME NOT NULL,
    peso_kg DECIMAL(10,3) NOT NULL,
    observacoes TEXT NULL,
    FOREIGN KEY (animal_id) REFERENCES animal (id)
) ENGINE=InnoDB;

CREATE TABLE registro_sanitario (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    tipo VARCHAR(15) NOT NULL,
    data_aplicacao DATE NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    dose DECIMAL(10,3) NULL,
    unidade_dose VARCHAR(20) NULL,
    proxima_aplicacao DATE NULL,
    observacoes TEXT NULL,
    FOREIGN KEY (animal_id) REFERENCES animal (id)
) ENGINE=InnoDB;

CREATE TABLE reproducao (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    matriz_id BIGINT NOT NULL,
    reprodutor_id BIGINT NULL,
    referencia_reprodutor_externo VARCHAR(150) NULL,
    tipo VARCHAR(15) NOT NULL,
    data_evento DATE NOT NULL,
    resultado VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_prevista_parto DATE NULL,
    data_desfecho DATE NULL,
    observacoes TEXT NULL,
    FOREIGN KEY (matriz_id) REFERENCES animal (id),
    FOREIGN KEY (reprodutor_id) REFERENCES animal (id)
) ENGINE=InnoDB;

CREATE TABLE alimentacao (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    animal_id BIGINT NULL,
    data DATE NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    quantidade DECIMAL(12,3) NULL,
    unidade VARCHAR(20) NULL,
    observacoes TEXT NULL,
    FOREIGN KEY (animal_id) REFERENCES animal (id)
) ENGINE=InnoDB;

CREATE TABLE despesa (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    animal_id BIGINT NULL,
    categoria VARCHAR(15) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    data DATE NOT NULL,
    valor DECIMAL(14,2) NOT NULL,
    observacoes TEXT NULL,
    FOREIGN KEY (animal_id) REFERENCES animal (id)
) ENGINE=InnoDB;

CREATE TABLE venda (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL,
    comprador VARCHAR(150) NOT NULL,
    observacoes TEXT NULL
) ENGINE=InnoDB;

CREATE TABLE item_venda (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    animal_id BIGINT NOT NULL,
    valor DECIMAL(14,2) NOT NULL,
    UNIQUE (venda_id, animal_id),
    FOREIGN KEY (venda_id) REFERENCES venda (id),
    FOREIGN KEY (animal_id) REFERENCES animal (id)
) ENGINE=InnoDB;

CREATE TABLE movimentacao (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    tipo VARCHAR(7) NOT NULL,
    motivo VARCHAR(10) NOT NULL,
    data DATE NOT NULL,
    item_venda_id BIGINT NULL UNIQUE,
    origem_destino VARCHAR(255) NULL,
    observacoes TEXT NULL,
    FOREIGN KEY (animal_id) REFERENCES animal (id),
    FOREIGN KEY (item_venda_id) REFERENCES item_venda (id)
) ENGINE=InnoDB;

-- Cadastre a fazenda com id = 1 e os dados reais da propriedade.
-- A senha do usuario deve ser armazenada como hash gerado pelo backend.
-- Nao excluir animais com historico; atualizar sua situacao.
