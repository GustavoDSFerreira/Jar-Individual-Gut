DROP DATABASE IF EXISTS sentinel_system;

CREATE DATABASE IF NOT EXISTS sentinel_system;

USE sentinel_system;

-- TABELAS

-- EMPRESA

CREATE TABLE Empresa (
id_empresa INT PRIMARY KEY AUTO_INCREMENT,
cnpj CHAR(16) NOT NULL UNIQUE,
nome VARCHAR(45) NOT NULL,
data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ACESSO

CREATE TABLE TipoAcesso (
id_tipo_acesso INT PRIMARY KEY AUTO_INCREMENT,
tipo VARCHAR(45) NOT NULL
);

-- USUÁRIO

CREATE TABLE Usuario (
id_usuario INT PRIMARY KEY AUTO_INCREMENT,
nome VARCHAR(45) NOT NULL,
email  VARCHAR(200)  NOT NULL UNIQUE,
senha CHAR(8) NOT NULL,
data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
fk_tipo_acesso INT NOT NULL, 
CONSTRAINT fk_tipo_acesso FOREIGN KEY (fk_tipo_acesso) 
	REFERENCES TipoAcesso (id_tipo_acesso),
fk_empresa INT NOT NULL, 
CONSTRAINT fk_empresa_Usuario FOREIGN KEY (fk_empresa) 
	REFERENCES Empresa (id_empresa) ON DELETE CASCADE
);

-- TIPO COMPONENTE
CREATE TABLE TipoComponente (
id_tipo_componente INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
tipo VARCHAR(45) NOT NULL
);

-- CONFIGURAÇÃO DE ALERTAS

CREATE TABLE ConfiguracaoAlerta (
id_configuracao INT PRIMARY KEY AUTO_INCREMENT,
parametro_min DECIMAL(5,2) NOT NULL,
parametro_max DECIMAL(5,2) NOT NULL,
fk_tipo_componente INT NOT NULL,
CONSTRAINT fk_tipo_componente_configuracao FOREIGN KEY (fk_tipo_componente)
	REFERENCES TipoComponente (id_tipo_componente),
fk_empresa INT NOT NULL,
CONSTRAINT fk_empresa_configuracao FOREIGN KEY (fk_empresa)
	REFERENCES Empresa (id_empresa) ON DELETE CASCADE
);

-- SERVIDOR

CREATE TABLE Servidor (
id_servidor INT PRIMARY KEY AUTO_INCREMENT,
nome VARCHAR(45) NOT NULL,
host_name VARCHAR(45) NOT NULL,
data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
fk_empresa INT NOT NULL, 
CONSTRAINT fk_empresa_servidor FOREIGN KEY (fk_empresa) 
	REFERENCES Empresa (id_empresa) ON DELETE CASCADE
);

CREATE TABLE Componente (
id_componente INT PRIMARY KEY AUTO_INCREMENT,
nome VARCHAR(60),
total_gib DECIMAL(10,2),
data_registro DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
fk_tipo_componente INT NOT NULL,
CONSTRAINT fk_tipo_componente FOREIGN KEY (fk_tipo_componente)
	REFERENCES TipoComponente (id_tipo_componente),
fk_servidor INT NOT NULL,
CONSTRAINT fk_servidor_componente FOREIGN KEY (fk_servidor)
	REFERENCES Servidor (id_servidor) ON DELETE CASCADE
);

-- HISTÓRICO DE ALERTAS
CREATE TABLE Alerta (
id_alerta INT PRIMARY KEY AUTO_INCREMENT,
registro DECIMAL(10,2) NOT NULL,
data_registro DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
fk_componente INT NOT NULL,
CONSTRAINT fk_componente_alerta FOREIGN KEY (fk_componente)
	REFERENCES Componente (id_componente) ON DELETE CASCADE
);

-- COMPONENTES E SISTEMA

CREATE TABLE SistemaOperacionalRegistro (
id_sistema INT PRIMARY KEY AUTO_INCREMENT,
data_inicializacao DATE NOT NULL,
tempo_atividade VARCHAR(50) NOT NULL,
data_registro DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
fk_servidor INT NOT NULL,
CONSTRAINT fk_servidor_sistema FOREIGN KEY (fk_servidor)
	REFERENCES Servidor (id_servidor) ON DELETE CASCADE
);

CREATE TABLE Registro (
id_registro INT PRIMARY KEY AUTO_INCREMENT,
uso DECIMAL(10,2) NOT NULL,
data_registro DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
fk_componente INT NOT NULL,
CONSTRAINT fk_componente FOREIGN KEY (fk_componente)
	REFERENCES Componente (id_componente) ON DELETE CASCADE
);

CREATE TABLE ProcessoRegistro (
id_processo INT PRIMARY KEY AUTO_INCREMENT,
pid INT NOT NULL,
nome VARCHAR(45) NOT NULL,
uso_cpu DECIMAL(10,2) NOT NULL,
uso_memoria DECIMAL (10,2) NOT NULL,
data_registro DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
fk_servidor INT NOT NULL,
CONSTRAINT fk_servidor_processo FOREIGN KEY (fk_servidor)
	REFERENCES Servidor (id_servidor) ON DELETE CASCADE
);

CREATE TABLE RedeRegistro (
id_rede INT PRIMARY KEY AUTO_INCREMENT,
endereco_ipv4 VARCHAR(45) NOT NULL,
endereco_ipv6 VARCHAR(255) NOT NULL,
bytes_recebidos DECIMAL(10,2) NOT NULL,
bytes_enviados DECIMAL(10,2) NOT NULL,
pacotes_recebidos INT NOT NULL,
pacotes_enviados INT NOT NULL,
data_registro DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
fk_servidor INT NOT NULL,
CONSTRAINT fk_Servidor_rede FOREIGN KEY (fk_servidor)
	REFERENCES Servidor (id_servidor) ON DELETE CASCADE
);

-- INSERTS

INSERT INTO TipoComponente (tipo) VALUES
	("CPU"),
    ("MEMORIA"),
    ("DISCO");
    
-- SELECTS

SELECT * FROM Empresa;
SELECT * FROM Usuario;
SELECT * FROM TipoAcesso;
SELECT * FROM TipoComponente;
SELECT * FROM ConfiguracaoAlerta;
SELECT * FROM Alerta;
SELECT * FROM Servidor;
SELECT * FROM SistemaOperacionalRegistro;
SELECT * FROM ProcessoRegistro;
SELECT * FROM RedeRegistro;
SELECT * FROM Componente;
SELECT * FROM Registro;
