-- MySQL Workbench Forward Engineering
-- The Awesome Dogless Database
-- The Best Not-Real Website of Chinesse Importations

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


-- -----------------------------------------------------
-- Schema dogless
-- -----------------------------------------------------


-- -----------------------------------------------------
DROP DATABASE IF EXISTS `dogless`;
CREATE SCHEMA IF NOT EXISTS `dogless` DEFAULT CHARACTER SET utf8mb3 ;
USE `dogless` ;
-- -----------------------------------------------------


-- -----------------------------------------------------
-- Table `dogless`.`zonas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`zonas` (
  `idzonas` INT NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;
INSERT INTO `dogless`.`zonas` (`idzonas`, `nombre`) VALUES
(1, 'Norte'),
(2, 'Sur'),
(3, 'Este'),
(4, 'Oeste');


-- -----------------------------------------------------
-- Table `dogless`.`adminzonales`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`adminzonales` (
  `idadminzonaleses` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `telefono` VARCHAR(9) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `contrasena` VARCHAR(45) NOT NULL,
  `idzonas` INT NOT NULL,
  `contrasena_temp` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idadminzonaleses`),
  INDEX `fk_adminzonales_zonas1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `fk_adminzonales_zonas1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `dogless`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`roles` (
  `idroles` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  PRIMARY KEY (`idroles`))
ENGINE = InnoDB;
-- Insertar datos en la tabla `roles` con IDs específicos
INSERT INTO `dogless`.`roles` (`idroles`, `nombre`) VALUES
(1, 'Superadmin'),
(2, 'Adminzonal'),
(3, 'Agente'),
(4, 'Usuario');
-- -----------------------------------------------------
-- Table `dogless`.`distritos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`distritos` (
  `iddistritos` INT NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `idzonas` INT NOT NULL,
  PRIMARY KEY (`iddistritos`),
  INDEX `fk_distritos_zonas1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `fk_distritos_zonas1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb3;
-- Insertar datos en la tabla `distritos`
INSERT INTO `dogless`.`distritos` (`iddistritos`, `nombre`, `idzonas`) VALUES
-- zonas Norte
(1, 'Ancon', 1),
(2, 'Santa Rosa', 1),
(3, 'Carabayllo', 1),
(4, 'Puente Piedra', 1),
(5, 'Comas', 1),
(6, 'Los Olivos', 1),
(7, 'San Martín de Porres', 1),
(8, 'Independencia', 1),

-- zonas Sur
(9, 'San Juan de Miraflores', 2),
(10, 'Villa María del Triunfo', 2),
(11, 'Villa el Salvador', 2),
(12, 'Pachacamac', 2),
(13, 'Lurin', 2),
(14, 'Punta Hermosa', 2),
(15, 'Punta Negra', 2),
(16, 'San Bartolo', 2),
(17, 'Santa María del Mar', 2),
(18, 'Pucusana', 2),

-- zonas Este
(19, 'San Juan de Lurigancho', 3),
(20, 'Lurigancho/Chosica', 3),
(21, 'Ate', 3),
(22, 'El Agustino', 3),
(23, 'Santa Anita', 3),
(24, 'La Molina', 3),
(25, 'Cieneguilla', 3),

-- zonas Oeste
(26, 'Rimac', 4),
(27, 'Cercado de Lima', 4),
(28, 'Breña', 4),
(29, 'Pueblo Libre', 4),
(30, 'Magdalena', 4),
(31, 'Jesus María', 4),
(32, 'La Victoria', 4),
(33, 'Lince', 4),
(34, 'San Isidro', 4),
(35, 'San Miguel', 4),
(36, 'Surquillo', 4),
(37, 'San Borja', 4),
(38, 'Santiago de Surco', 4),
(39, 'Barranco', 4),
(40, 'Chorrillos', 4),
(41, 'San Luis', 4),
(42, 'Miraflores', 4);


-- -----------------------------------------------------
-- Table `dogless`.`preguntasproducto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`preguntasproducto` (
  `idpreguntasproducto` INT NOT NULL,
  `comentario` MEDIUMTEXT NOT NULL,
  `fechapregunta` DATE NOT NULL,
  `idusuarios` INT NOT NULL,
  `idproductos` INT NOT NULL,
  PRIMARY KEY (`idpreguntasproducto`),
  INDEX `fk_preguntasproducto_usuarios1_idx` (`idusuarios` ASC) VISIBLE,
  INDEX `fk_preguntasproducto_productos1_idx` (`idproductos` ASC) VISIBLE,
  CONSTRAINT `fk_preguntasproducto_usuarios1`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_preguntasproducto_productos1`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `dogless`.`usuarios`
-- -----------------------------------------------------
-- A ver si soporta la relación a sí misma O:
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`usuarios` (
  `idusuarios` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `pwd` VARCHAR(100) NULL DEFAULT NULL,
  `telefono` VARCHAR(45) NULL DEFAULT NULL,
  `direccion` VARCHAR(45) NULL DEFAULT NULL,
  `idroles` INT NOT NULL,
  `iddistritos` INT NOT NULL,
  `idadminzonales` INT NULL DEFAULT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL DEFAULT NULL,
  `ruc` VARCHAR(11) NULL DEFAULT NULL,
  `codigoaduana` VARCHAR(45) NULL DEFAULT NULL,
  `razonsocial` VARCHAR(45) NULL DEFAULT NULL,
  `codigojurisdiccion` VARCHAR(45) NULL DEFAULT NULL,
  `idzonas` INT NOT NULL,
  `fechanacimiento` DATE NULL DEFAULT NULL,
  `fechabaneo` DATE NULL DEFAULT NULL,
  `motivobaneo` VARCHAR(45) NULL DEFAULT NULL,
  `borrado` TINYINT NULL DEFAULT '1',
  `fotoperfil` MEDIUMBLOB NULL DEFAULT NULL,
  `usuarios_idusuarios` INT NULL,
  PRIMARY KEY (`idusuarios`),
  INDEX `idroles_idx` (`idroles` ASC) VISIBLE,
  INDEX `iddistritos_idx` (`iddistritos` ASC) VISIBLE,
  INDEX `idadminzonales_idx` (`idadminzonales` ASC) VISIBLE,
  INDEX `fk_usuarios_zonas1_idx` (`idzonas` ASC) VISIBLE,
  INDEX `fk_usuarios_usuarios1_idx` (`usuarios_idusuarios` ASC) VISIBLE,
  CONSTRAINT `fk_usuarios_zonas1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`),
  CONSTRAINT `idadminzonales`
    FOREIGN KEY (`idadminzonales`)
    REFERENCES `dogless`.`adminzonales` (`idadminzonaleses`),
  CONSTRAINT `iddistritos`
    FOREIGN KEY (`iddistritos`)
    REFERENCES `dogless`.`distritos` (`iddistritos`),
  CONSTRAINT `idroles`
    FOREIGN KEY (`idroles`)
    REFERENCES `dogless`.`roles` (`idroles`),
  CONSTRAINT `fk_usuarios_usuarios1`
    FOREIGN KEY (`usuarios_idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 59
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dogless`.`solicitudes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`solicitudes` (
  `idsolicitudes` INT NOT NULL,
  `veredicto` TINYINT NULL,
  `comentario` VARCHAR(300) NULL,
  `borrado` TINYINT DEFAULT 1,
  `idusuarios` INT NOT NULL,
  PRIMARY KEY (`idsolicitudes`),
  INDEX `fk_solicitudes_usuarios1_idx` (`idusuarios` ASC) VISIBLE,
  CONSTRAINT `fk_solicitudes_usuarios1`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `email`, `pwd`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`, `fechanacimiento`)
VALUES 
(1, 'Christopher', 'Terrones', '87623344', 'cterrones@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654321', 'Av. Segura 123', 1, 1, 0, 'activo', NULL, NULL, NULL, NULL, 1, '2000-05-15'),
(2, 'Pedro', 'Bustamante', '12834455', 'pbustamante@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654322', 'Av. Universitaria 377', 1, 1, 0, 'activo', NULL, NULL, NULL, NULL, 1, '2001-09-22');

INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `email`, `pwd`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`, `fechanacimiento`)
VALUES
-- zonas Norte (Administradores zonales ID 1 y 2)
(3, 'Brighit', 'Egusquiza', '89345678', 'begusquiza@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654321', 'Calle Norte 101', 2, 1, 1, 'activo', NULL, NULL, NULL, NULL, 1, '2002-03-10'),
(4, 'Luisa', 'Fernández', '18256789', 'lfernandez@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654322', 'Calle Norte 102', 2, 2, 2, 'activo', NULL, NULL, NULL, NULL, 1, '2003-11-30'),
-- zonas Sur (Administradores zonales ID 3 y 4)
(5, 'Carlos', 'Ramírez', '34567890', 'cramirez@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654323', 'Av. Sur 101', 2, 9, 3, 'activo', NULL, NULL, NULL, NULL, 2, '2004-07-05'),
(6, 'Alejandro', 'Gómez', '45678901', 'agomez@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654324', 'Av. Sur 102', 2, 10, 4, 'activo', NULL, NULL, NULL, NULL, 2, '2005-01-18'),
-- zonas Este (Administradores zonales ID 5 y 6)
(7, 'Ricardo', 'López', '56789012', 'rlopez@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654325', 'Calle Este 101', 2, 19, 5, 'activo', NULL, NULL, NULL, NULL, 3, '2000-12-03'),
(8, 'Ana', 'Martínez', '67890123', 'amartinez@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654326', 'Av. Este 102', 2, 20, 6, 'activo', NULL, NULL, NULL, NULL, 3, '2001-08-20'),
-- zonas Oeste (Administradores zonales ID 7 y 8)
(9, 'Estefany', 'Fuentes', '78901234', 'efuentes@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654327', 'Calle Oeste 101', 2, 26, 7, 'activo', NULL, NULL, NULL, NULL, 4, '2002-10-22'),
(10, 'Gabriela', 'Navarro', '89012345', 'gnavarro@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654328', 'Av. Oeste 102', 2, 27, 8, 'activo', NULL, NULL, NULL, NULL, 4, '2003-04-25');


-- Agentes zonas Norte (Admin zonas 1Y 2)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `email`, `pwd`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`, `fechanacimiento`) 
VALUES
(11, 'Nilo', 'Cori', '14565566', 'ncori@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654323', 'Calle Norte 123', 3, 1, 1, 'activo', '12345678901', 'ADU001', 'Agente Norte', 'JUR001', 1, '2003-03-15'),
(12, 'Rosa', 'Soto', '44556677', 'rsoto@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654324', 'Av. Norte 456', 3, 2, 1, 'activo', '10987654321', 'ADU002', 'Agente Norte', 'JUR002', 1, '2004-07-22'),
(13, 'Luis', 'Gómez', '55667788', 'lgomez@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654325', 'Calle Norte 789', 3, 3, 1, 'activo', '10987654322', 'ADU003', 'Agente Norte', 'JUR003', 1, '2002-11-30'),
(14, 'María', 'López', '66778899', 'mlopez@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654326', 'Av. Norte 012', 3, 4, 2, 'activo', '10987654323', 'ADU004', 'Agente Norte', 'JUR004', 1, '2005-01-18'),
(15, 'Carlos', 'Rivas', '77889900', 'crivas@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654327', 'Calle Norte 345', 3, 5, 2, 'activo', '10987654324', 'ADU005', 'Agente Norte', 'JUR005', 1, '2003-09-05'),
(16, 'Patricia', 'Flores', '88990011', 'pflores@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654328', 'Av. Norte 678', 3, 6, 2, 'activo', '10987654325', 'ADU006', 'Agente Norte', 'JUR006', 1, '2004-04-12');

-- Agentes zonas Sur (Admin zonas 3 Y 4)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `email`, `pwd`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`, `fechanacimiento`) 
VALUES
(17, 'Damian', 'López', '99001122', 'dlopez@sur.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654329', 'Calle Sur 123', 3, 9, 3, 'activo', '12345678902', 'ADU007', 'Agente Sur', 'JUR007', 2, '2002-06-25'),
(18, 'Andrés', 'Pérez', '11002233', 'aperez@sur.com', 'agenteSpass456', '987654330', 'Av. Sur 456', 3, 10, 3, 'activo', '10987654326', 'ADU008', 'Agente Sur', 'JUR008', 2, '2005-02-14'),
(19, 'Claudia', 'Vega', '22003344', 'cvega@sur.com', 'agenteSpass789', '987654331', 'Calle Sur 789', 3, 11, 3, 'activo', '10987654327', 'ADU009', 'Agente Sur', 'JUR009', 2, '2003-12-08'),
(20, 'Roberto', 'Ramos', '33004455', 'rramos@sur.com', 'agenteSpass012', '987654332', 'Av. Sur 012', 3, 12, 4, 'activo', '10987654328', 'ADU010', 'Agente Sur', 'JUR010', 2, '2004-08-30'),
(21, 'Sofía', 'Torres', '44005566', 'storres@sur.com', 'agenteSpass345', '987654333', 'Calle Sur 345', 3, 13, 4, 'activo', '10987654329', 'ADU011', 'Agente Sur', 'JUR011', 2, '2002-05-17'),
(22, 'Pablo', 'Cruz', '55006677', 'pcruz@sur.com', 'agenteSpass678', '987654334', 'Av. Sur 678', 3, 14, 4, 'activo', '10987654330', 'ADU012', 'Agente Sur', 'JUR012', 2, '2003-10-21');

-- Agentes zonas Este (Admin zonas 5 y 6)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `email`, `pwd`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`, `fechanacimiento`,`usuarios_idusuarios`)
VALUES
(23, 'James', 'Bond', '66007788', 'jbond@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654335', 'Calle Este 123', 3, 19, 5, 'activo', '12345678903', 'ADU013', 'Agente Este', 'JUR013', 3, '2002-06-15',7),
(24, 'Verónica', 'Díaz', '77008899', 'vdiaz@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654336', 'Av. Este 456', 3, 20, 5, 'activo', '10987654331', 'ADU014', 'Agente Este', 'JUR014', 3, '2003-09-22',7),
(25, 'Alberto', 'Cabrera', '88009900', 'acabrera@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654337', 'Calle Este 789', 3, 21, 5, 'activo', '10987654332', 'ADU015', 'Agente Este', 'JUR015', 3, '2004-03-10',7),
(26, 'Lucía', 'Ortiz', '99001122', 'lortiz@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654338', 'Av. Este 012', 3, 22, 6, 'activo', '10987654333', 'ADU016', 'Agente Este', 'JUR016', 3, '2002-11-30',8),
(27, 'Tomás', 'Fernández', '11002233', 'tfernandez@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654339', 'Calle Este 345', 3, 23, 6, 'activo', '10987654334', 'ADU017', 'Agente Este', 'JUR017', 3, '2005-04-25',8),
(28, 'Raquel', 'Paredes', '12003344', 'rparedes@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654340', 'Av. Este 678', 3, 24, 6, 'activo', '10987654335', 'ADU018', 'Agente Este', 'JUR018', 3, '2003-08-18',8);

-- Agentes zonas Oeste (Admin zonas 7  y 8)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `email`, `pwd`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`, `fechanacimiento`)
VALUES
(29, 'Raúl', 'González', '13004455', 'rgonzalez@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654341', 'Calle Oeste 123', 3, 26, 7, 'activo', '12345678904', 'ADU019', 'Agente Oeste', 'JUR019', 4, '2004-07-12'),
(30, 'Diana', 'Campos', '14005566', 'dcampos@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654342', 'Av. Oeste 456', 3, 27, 7, 'activo', '10987654336', 'ADU020', 'Agente Oeste', 'JUR020', 4, '2002-05-08'),
(31, 'Emilio', 'Méndez', '15006677', 'emendez@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654343', 'Calle Oeste 789', 3, 28, 7, 'activo', '10987654337', 'ADU021', 'Agente Oeste', 'JUR021', 4, '2005-01-19'),
(32, 'Paola', 'Moreno', '16007788', 'pmoreno@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654344', 'Av. Oeste 012', 3, 29, 8, 'activo', '10987654338', 'ADU022', 'Agente Oeste', 'JUR022', 4, '2003-12-03'),
(33, 'Felipe', 'Santos', '17008899', 'fsantos@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654345', 'Calle Oeste 345', 3, 30, 8, 'activo', '10987654339', 'ADU023', 'Agente Oeste', 'JUR023', 4, '2004-10-28'),
(34, 'Alejandra', 'Ramos', '18009900', 'aramos@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654346', 'Av. Oeste 678', 3, 31, 8, 'activo', '10987654340', 'ADU024', 'Agente Oeste', 'JUR024', 4, '2002-02-14');

-- Usuarios Finales 
INSERT INTO dogless.usuarios (idusuarios, nombre, apellido, dni, email, pwd, telefono, direccion, idroles, iddistritos, idadminzonales, estado, ruc, codigoaduana, razonsocial, codigojurisdiccion, idzonas, fechanacimiento,usuarios_idusuarios)
VALUES
(35, 'Adrián', 'Tipo', '18874455', 'atipo@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654347', 'Calle Norte 1', 4, 1, NULL, 'activo', NULL, NULL, NULL, NULL, 1, '2003-05-15',23),
(36, 'Fabricio', 'Estrada', '19045566', 'festrada@gmail.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654348', 'Calle Norte 2', 4, 2, NULL, 'activo', NULL, NULL, NULL, NULL, 1, '2004-08-22',23),
(37, 'María', 'Santos', '44556677', 'maria.santos@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654349', 'Av. Norte 3', 4, 3, NULL, 'activo', NULL, NULL, NULL, NULL, 1, '2002-03-10',23),
(38, 'Raúl', 'Pérez', '55667788', 'raul.perez@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654350', 'Calle Norte 4', 4, 4, NULL, 'activo', NULL, NULL, NULL, NULL, 1, '2005-11-30',24),
(39, 'Carla', 'Mendoza', '66778899', 'carla.mendoza@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654351', 'Av. Norte 5', 4, 5, NULL, 'activo', NULL, NULL, NULL, NULL, 1, '2003-12-05',24),
(40, 'Luis', 'García', '77889900', 'luis.garcia@norte.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654352', 'Calle Norte 6', 4, 6, NULL, 'baneado', NULL, NULL, NULL, NULL, 1, '2004-06-18',24),
(41, 'Sofía', 'Rodríguez', '88990011', 'sofia.rodriguez@sur.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654353', 'Calle Sur 1', 4, 9, NULL, 'activo', NULL, NULL, NULL, NULL, 2, '2002-09-25',24),
(42, 'Juan', 'Morales', '99001122', 'juan.morales@sur.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654354', 'Av. Sur 2', 4, 10, NULL, 'baneado', NULL, NULL, NULL, NULL, 2, '2005-04-14',25),
(43, 'Mónica', 'Ruiz', '11002233', 'monica.ruiz@sur.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654355', 'Calle Sur 3', 4, 11, NULL, 'activo', NULL, NULL, NULL, NULL, 2, '2003-07-29', NULL),
(44, 'Carlos', 'Martínez', '12003344', 'carlos.martinez@sur.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654356', 'Av. Sur 4', 4, 12, NULL, 'activo', NULL, NULL, NULL, NULL, 2, '2004-02-08', NULL),
(45, 'Julia', 'Castro', '13004455', 'julia.castro@sur.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654357', 'Calle Sur 5', 4, 13, NULL, 'inactivo', NULL, NULL, NULL, NULL, 2, '2002-10-17', NULL),
(46, 'Pablo', 'Fernández', '14005566', 'pablo.fernandez@sur.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654358', 'Av. Sur 6', 4, 14, NULL, 'activo', NULL, NULL, NULL, NULL, 2, '2005-01-23', NULL),
(47, 'Andrés', 'Silva', '15006677', 'andres.silva@este.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654359', 'Calle Este 1', 4, 19, NULL, 'activo', NULL, NULL, NULL, NULL, 3, '2003-08-12', NULL),
(48, 'Elena', 'Reyes', '16007788', 'elena.reyes@este.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654360', 'Av. Este 2', 4, 20, NULL, 'activo', NULL, NULL, NULL, NULL, 3, '2004-11-05', NULL),
(49, 'Hugo', 'Cano', '17008899', 'hugo.cano@este.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654361', 'Calle Este 3', 4, 21, NULL, 'activo', NULL, NULL, NULL, NULL, 3, '2002-04-28', NULL),
(50, 'Ana', 'Ramírez', '18009900', 'ana.ramirez@este.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654362', 'Av. Este 4', 4, 22, NULL, 'baneado', NULL, NULL, NULL, NULL, 3, '2005-09-16', NULL),
(51, 'David', 'Paredes', '19001111', 'david.paredes@este.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654363', 'Calle Este 5', 4, 23, NULL, 'inactivo', NULL, NULL, NULL, NULL, 3, '2003-06-21', NULL),
(52, 'Carolina', 'Gómez', '20002222', 'carolina.gomez@este.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654364', 'Av. Este 6', 4, 24, NULL, 'activo', NULL, NULL, NULL, NULL, 3, '2004-03-15', NULL),
(53, 'Carmen', 'López', '21003333', 'carmen.lopez@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654365', 'Calle Oeste 1', 4, 26, NULL, 'activo', NULL, NULL, NULL, NULL, 4, '2002-07-09', NULL),
(54, 'Sergio', 'Castillo', '22004444', 'sergio.castillo@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654366', 'Av. Oeste 2', 4, 27, NULL, 'activo', NULL, NULL, NULL, NULL, 4, '2005-12-30', NULL),
(55, 'Isabel', 'Mendoza', '23005555', 'isabel.mendoza@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654367', 'Calle Oeste 3', 4, 28, NULL, 'activo', NULL, NULL, NULL, NULL, 4, '2003-02-14', NULL),
(56, 'Jorge', 'Vega', '24006666', 'jorge.vega@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654368', 'Av. Oeste 4', 4, 29, NULL, 'inactivo', NULL, NULL, NULL, NULL, 4, '2004-10-03', NULL),
(57, 'Lucía', 'Carrillo', '25007777', 'lucia.carrillo@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654369', 'Calle Oeste 5', 4, 30, NULL, 'activo', NULL, NULL, NULL, NULL, 4, '2002-01-19', NULL),
(58, 'Emilio', 'Vargas', '26008888', 'emilio.vargas@oeste.com', '$2a$12$SroLYpp9Is6usCmU4EUIOe693IPZnGunKoQ68G1qmq9knxHpPHBGu', '987654370', 'Av. Oeste 6', 4, 31, NULL, 'activo', NULL, NULL, NULL, NULL, 4, '2005-05-27', NULL);

-- -----------------------------------------------------
-- Table `dogless`.`ordenes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`ordenes` (
  `idordenes` INT NOT NULL AUTO_INCREMENT,
  `estado` ENUM('Creado', 'En Validación', 'En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta', 'Recibido','Cancelado') NULL DEFAULT NULL,
  `fecha` DATE NULL DEFAULT NULL,
  `direccionenvio` VARCHAR(100) NULL DEFAULT NULL,
  `total` DECIMAL(10,2) NULL DEFAULT NULL,
  `metodopago` ENUM('tarjeta') NULL DEFAULT NULL,
  `idusuarios` INT NOT NULL,
  `borrado` INT DEFAULT 1,
  PRIMARY KEY (`idordenes`),
  INDEX `idusuarios_idx` (`idusuarios` ASC) VISIBLE,
  CONSTRAINT `idusuarios`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;
-- Insertar datos en la tabla `ordenes`
INSERT INTO `dogless`.`ordenes` 
(`estado`, `fecha`, `direccionenvio`, `total`, `metodopago`, `idusuarios`)
VALUES
-- Orden 1 para el usuario 35
('Creado', '2024-09-01', 'Calle Norte 1, Lima', 1599.97, 'tarjeta', 35),
-- Orden 2 para el usuario 36
('En Proceso', '2024-09-02', 'Av. Norte 2, Lima', 1399.98, 'tarjeta', 36),
-- Orden 3 para el usuario 41
('En Ruta', '2024-09-03', 'Calle Sur 3, Lima', 489.97, 'tarjeta', 41),
-- Orden 4 para el usuario 42
('Recibido', '2024-06-04', 'Av. Sur 4, Lima', 1199.99, 'tarjeta', 42),
-- Orden 5 para el usuario 47
('En Aduanas', '2024-09-05', 'Calle Este 1, Lima', 750.99, 'tarjeta', 47),
-- Orden 6 para el usuario 48
('Arribo al País', '2024-09-06', 'Av. Este 2, Lima', 799.99, 'tarjeta', 48),
-- Orden 7 para el usuario 53
('En Ruta', '2024-07-31', 'Calle Oeste 1, Lima', 599.98, 'tarjeta', 53),
-- Orden 8 para el usuario 54
('Creado', '2024-08-08', 'Av. Oeste 2, Lima', 999.98, 'tarjeta', 54),
-- Orden 9 para el usuario 49
('En Validación', '2024-09-09', 'Calle Este 3, Lima', 129.98, 'tarjeta', 49),
-- Orden 10 para el usuario 55
('En Aduanas', '2024-09-10', 'Calle Norte 5, Lima', 1350.50, 'tarjeta', 55),
-- Orden 11 para el usuario 32
('En Aduanas', '2024-09-10', 'Calle Oeste 10, Lima', 1350.50, 'tarjeta', 32),
-- Orden 11 para el usuario 32
('Recibido', '2024-06-09', 'Av. Sur 9, Lima', 1199.99, 'tarjeta', 11);

-- -----------------------------------------------------
-- Table `dogless`.`proveedores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`proveedores` (
  `idproveedores` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `telefono` VARCHAR(45) NULL DEFAULT NULL,
  `ruc` VARCHAR(11) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `tienda` VARCHAR(45) NULL DEFAULT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL DEFAULT NULL,
  `borrado` TINYINT DEFAULT 1,
  PRIMARY KEY (`idproveedores`),
  INDEX `fk_proveedores_proveedores1_idx` (`idproveedores` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- Insertar datos en la tabla `proveedores`
INSERT INTO `dogless`.`proveedores` 
(`nombre`, `apellido`, `telefono`, `ruc`, `dni`, `tienda`, `estado`) 
VALUES
-- Proveedor 1
('Carlos', 'Gómez', '987654321', '12345678901', '12345678', 'ElectroShop', 'activo'),
-- Proveedor 2
('María', 'Fernández', '987654322', '10987654321', '23456789', 'TechWorld', 'baneado'),
-- Proveedor 3
('Juan', 'López', '987654323', '11223344556', '34567890', 'GadgetStore', 'activo'),
-- Proveedor 4
('Ana', 'Martínez', '987654324', '99887766554', '45678901', 'PhotoPro', 'activo'),
-- Proveedor 5
('Ricardo', 'Pérez', '987654325', '22334455667', '56789012', 'Impresiones3D', 'activo'),
-- Proveedor 6
('Lucía', 'Ramos', '987654326', '33445566778', '67890123', 'DroneTech', 'inactivo'),
-- Proveedor 7
('Roberto', 'Cruz', '987654327', '44556677889', '78901234', 'GameHouse', 'activo'),
-- Proveedor 8
('Sofía', 'Santos', '987654328', '55667788990', '89012345', 'SmartShop', 'activo'),
-- Proveedor 9
('Miguel', 'Reyes', '987654329', '66778899001', '90123456', 'TecnoZone', 'activo'),
-- Proveedor 10
('Carla', 'Vega', '987654330', '77889900112', '01234567', 'PCMaster', 'activo'),
-- Proveedor 11
('Luis', 'Pérez', '987654331', '77889900113', '01234568', 'ElectroShop', 'activo'),
-- Proveedor 12
('Ana', 'González', '987654332', '77889900114', '01234569', 'ElectroShop', 'activo'),
-- Proveedor 13
('Mario', 'Ramos', '987654333', '77889900115', '01234570', 'GameHouse', 'activo'),
-- Proveedor 14
('Laura', 'Martínez', '987654334', '77889900116', '01234571', 'TechWorld', 'activo'),
-- Proveedor 15
('Carlos', 'Sánchez', '987654335', '77889900117', '01234572', 'TechWorld', 'activo'),
-- Proveedor 16
('Sofía', 'Jiménez', '987654336', '77889900118', '01234573', 'TechWorld', 'activo'),
-- Proveedor 17
('Ricardo', 'López', '987654337', '77889900119', '01234574', 'TechWorld', 'activo');


-- -----------------------------------------------------
-- Table `dogless`.`productos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`productos` (
  `idproductos` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `descripcion` VARCHAR(500) NULL DEFAULT NULL,
  `categoria` VARCHAR(45) NULL DEFAULT NULL,
  `precio` DECIMAL(10,2) NULL DEFAULT NULL,
  `costoenvio` DECIMAL(10,2) NULL DEFAULT NULL,
  `idproveedores` INT NOT NULL,
  `modelos` VARCHAR(100) NULL DEFAULT NULL,
  `colores` VARCHAR(100) NULL DEFAULT NULL,
  `aprobado` VARCHAR(10) NULL DEFAULT NULL,
  `borrado` INT DEFAULT 1,
  `estado` VARCHAR(20) NULL DEFAULT NULL,
  `imagenprod` MEDIUMBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`idproductos`),
  INDEX `proveedor_id_idx_productos` (`idproveedores` ASC) VISIBLE,
  CONSTRAINT `fk_proveedor_id_productos`
    FOREIGN KEY (`idproveedores`)
    REFERENCES `dogless`.`proveedores` (`idproveedores`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;
INSERT INTO `dogless`.`productos` 
(`nombre`, `descripcion`, `categoria`, `precio`, `costoenvio`, `idproveedores`, `modelos`, `colores`, `aprobado`) 
VALUES
('Smartphone X', 'Teléfono móvil avanzado', 'Electrónica', 599.99, 9.99, 1, 'Modelo X2023, Modelo XPro', 'Negro, Blanco, Azul', 'Aprobado'),
('Laptop Y', 'Computadora portátil', 'Informática', 899.99, 12.99, 2, 'Laptop YUltra, Laptop YMax', 'Plata, Gris Oscuro', 'Aprobado'),
('Auriculares Z', 'Auriculares inalámbricos', 'Accesorios', 49.99, 5.99, 3, 'Z-Lite, Z-Pro', 'Rojo, Negro', 'Aprobado'),
('Cámara Fotográfica', 'Cámara DSLR con lente 18-55mm', 'Fotografía', 1200.00, 15.00, 4, 'CamFX, CamUltra', 'Negro, Plata', 'Aprobado'),
('Tablet W', 'Tableta gráfica para diseño', 'Electrónica', 350.50, 10.50, 1, 'Tablet W-Lite, Tablet W-Pro', 'Negro, Blanco', 'Aprobado'),
('Impresora 3D', 'Impresora 3D con tecnología FDM', 'Equipos', 750.99, 20.00, 2, 'Impresora 3D X1, Impresora 3D Z2', 'Negro, Azul', 'Aprobado'),
('Drone Pro', 'Drone con cámara 4K', 'Electrónica', 499.99, 18.99, 3, 'Drone Pro 2023, Drone Pro X', 'Negro, Blanco', 'Aprobado'),
('Consola de Videojuegos', 'Consola de videojuegos de última generación', 'Videojuegos', 399.99, 14.99, 4, 'Console V2023, Console Max', 'Negro, Blanco', 'Aprobado'),
('Reloj Inteligente', 'Reloj inteligente con monitor de salud', 'Accesorios', 199.99, 7.99, 1, 'Watch X2023, Watch Pro', 'Negro, Blanco, Rosa', 'Aprobado'),
('Teclado Mecánico', 'Teclado mecánico retroiluminado', 'Periféricos', 89.99, 4.99, 2, 'Keyboard Pro, Keyboard X', 'Negro, Blanco, Rojo', 'Aprobado'),
('Monitor Curvo', 'Monitor curvo de 34 pulgadas', 'Periféricos', 449.99, 19.99, 3, 'CurveMax, CurvePro', 'Negro, Plata', 'Pendiente'),
('Altavoces Bluetooth', 'Altavoces portátiles con conexión Bluetooth', 'Accesorios', 79.99, 6.99, 4, 'SoundMax, SoundPro', 'Negro, Azul, Rojo', 'Pendiente'),
('Disco Duro Externo', 'Disco duro externo de 2TB', 'Almacenamiento', 89.99, 4.99, 1, 'StorageX, StoragePro', 'Negro, Plata', 'Pendiente'),
('Ratón Gamer', 'Ratón ergonómico para gaming', 'Periféricos', 59.99, 3.99, 2, 'GamerX, GamerPro', 'Negro, Rojo, Verde', 'Pendiente');



-- -----------------------------------------------------
-- Table `dogless`.`detallesorden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`detallesorden` (
  `iddetallesorden` INT NOT NULL AUTO_INCREMENT,
  `idorden` INT NOT NULL,
  `idproducto` INT NOT NULL,
  `cantidad` INT NULL DEFAULT NULL,
  `preciounitario` DECIMAL(10,2) NULL DEFAULT NULL,
  `subtotal` DECIMAL(10,2) NULL DEFAULT NULL,
  `borrado` INT DEFAULT 1,
  PRIMARY KEY (`iddetallesorden`),
  INDEX `id_orden_idx_detallesOrden` (`idorden` ASC) VISIBLE,
  INDEX `id_producto_idx_detallesOrden` (`idproducto` ASC) VISIBLE,
  CONSTRAINT `fk_id_orden_detallesOrden`
    FOREIGN KEY (`idorden`)
    REFERENCES `dogless`.`ordenes` (`idordenes`),
  CONSTRAINT `fk_id_producto_detallesOrden`
    FOREIGN KEY (`idproducto`)
    REFERENCES `dogless`.`productos` (`idproductos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `dogless`.`importaciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`importaciones` (
  `idimportaciones` INT NOT NULL AUTO_INCREMENT,
  `cantidad` VARCHAR(45) NULL,
  `fecha_pedido` DATE NULL,
  `aprobar` VARCHAR(20) NULL,
  `borrado` INT NULL,
  `idzonas` INT NOT NULL,
  `idproductos` INT NOT NULL,
  PRIMARY KEY (`idimportaciones`),
  INDEX `fk_importaciones_zonas1_idx` (`idzonas` ASC) VISIBLE,
  INDEX `fk_importaciones_productos1_idx` (`idproductos` ASC) VISIBLE,
  CONSTRAINT `fk_importaciones_zonas1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_importaciones_productos1`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`reposicion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`reposicion` (
  `idreposicion` INT NOT NULL,
  `cantidad` INT NULL,
  `fecha_pedido` DATE NULL,
  `aprobar` VARCHAR(45) NULL,
  `idproductos` INT NOT NULL,
  `idzonas` INT NOT NULL,
  `borrado` INT DEFAULT 1,
  PRIMARY KEY (`idreposicion`),
  INDEX `fk_reposicion_productos1_idx` (`idproductos` ASC) VISIBLE,
  INDEX `fk_reposicion_zonas1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `fk_reposicion_productos1`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_reposicion_zonas1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

INSERT INTO `dogless`.`importaciones` 
(`cantidad`, `fecha_pedido`, `aprobar`, `borrado`, `idzonas`, `idproductos`) 
VALUES 
('32', '2024-09-05', 'aprobado', 1, 2, 7),
('45', '2024-09-12', 'pendiente', 1, 1, 3),
('28', '2024-09-18', 'pendiente', 1, 3, 11),
('56', '2024-09-03', 'aprobado', 1, 4, 14),
('37', '2024-09-22', 'pendiente', 1, 2, 5),
('41', '2024-09-15', 'pendiente', 1, 1, 9),
('33', '2024-09-08', 'pendiente', 1, 3, 2),
('52', '2024-09-25', 'aprobado', 1, 4, 12),
('39', '2024-09-10', 'pendiente', 1, 2, 6),
('47', '2024-09-20', 'pendiente', 1, 1, 8);

-- Insertar datos en la tabla `reposicion`
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('4', '20', '2024-09-03', NULL, '1', '1');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('5', '15', '2024-09-04', 'aprobado', '2', '2');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('6', '30', '2024-09-05', NULL, '3', '3');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('7', '10', '2024-09-06', 'rechazado', '4', '1');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('8', '25', '2024-09-07', NULL, '5', '2');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('9', '40', '2024-09-08', 'aprobado', '6', '3');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('10', '60', '2024-09-09', 'rechazado', '7', '1');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('11', '50', '2024-09-10', NULL, '8', '2');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('12', '20', '2024-09-11', 'aprobado', '9', '3');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('13', '35', '2024-09-12', NULL, '10', '4');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('14', '45', '2024-09-13', 'rechazado', '11', '1');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('15', '15', '2024-09-14', NULL, '12', '2');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('16', '55', '2024-09-15', 'aprobado', '13', '3');
INSERT INTO `dogless`.`reposicion` (`idreposicion`, `cantidad`, `fecha_pedido`, `aprobar`, `idproductos`, `idzonas`) VALUES ('17', '30', '2024-09-16', NULL, '14', '4');


-- Insertar datos en la tabla `detallesorden`
INSERT INTO `dogless`.`detallesorden` 
(`idorden`, `idproducto`, `cantidad`, `preciounitario`, `subtotal`) 
VALUES
-- Orden 1: Compra de varios productos por un usuario final
(1, 1, 2, 599.99, 1199.98), -- 2 unidades de Smartphone X
(1, 3, 1, 49.99, 49.99), -- 1 unidad de Auriculares Z
(1, 5, 1, 350.50, 350.50), -- 1 unidad de Tablet W

-- Orden 2: Compra por otro usuario final
(2, 2, 1, 899.99, 899.99), -- 1 unidad de Laptop Y
(2, 7, 1, 499.99, 499.99), -- 1 unidad de Drone Pro

-- Orden 3: Compra de accesorios y periféricos por un usuario final
(3, 9, 2, 199.99, 399.98), -- 2 unidades de Reloj Inteligente
(3, 10, 1, 89.99, 89.99), -- 1 unidad de Teclado Mecánico

-- Orden 4: Compra de equipos de alta gama
(4, 4, 1, 1200.00, 1200.00), -- 1 unidad de Cámara Fotográfica
(4, 6, 1, 750.99, 750.99), -- 1 unidad de Impresora 3D

-- Orden 5: Compra de artículos de entretenimiento
(5, 8, 1, 399.99, 399.99), -- 1 unidad de Consola de Videojuegos
(5, 3, 2, 49.99, 99.98); -- 2 unidades de Auriculares Z

-- -----------------------------------------------------
-- Table `dogless`.`resenas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`resenas` (
  `idresenas` INT NOT NULL AUTO_INCREMENT,
  `idproductos` INT NOT NULL,
  `comentario` MEDIUMTEXT NULL DEFAULT NULL,
  `satisfaccion` INT NULL DEFAULT NULL,
  `fecha` DATE NULL DEFAULT NULL,
  `atencion` INT NULL DEFAULT NULL,
  `calidad` TINYINT NULL DEFAULT NULL,
  `serecibiorapido` INT NULL DEFAULT NULL,
  `usuarioid` INT NOT NULL,
  `aprobar` VARCHAR(45) NULL,
  `tipo` INT NULL,
  PRIMARY KEY (`idresenas`),
  INDEX `producto_id_idx_resenas` (`idproductos` ASC) VISIBLE,
  INDEX `usuarioid_idx` (`usuarioid` ASC) VISIBLE,
  CONSTRAINT `fk_producto_id_resenas`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`),
  CONSTRAINT `usuarioid`
    FOREIGN KEY (`usuarioid`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;
-- Insertar datos en la tabla `resenas`
INSERT INTO `dogless`.`resenas` 
(`idproductos`, `comentario`, `satisfaccion`, `fecha`, `atencion`, `calidad`, `serecibiorapido`, `usuarioid`, `tipo`)
VALUES
-- Reseñas para el producto 1: Smartphone X
(1, 'Gran producto, funciona perfectamente y llegó a tiempo.', 5, '2024-09-01', 5, 5, 1, 35,1),
(1, 'Buen teléfono, pero la entrega fue más lenta de lo esperado.', 4, '2024-09-05', 4, 4, 0, 36,1),
(1, 'Calidad aceptable, aunque esperaba algo más.', 3, '2024-09-10', 3, 3, 1, 37,1),

-- Reseñas para el producto 2: Laptop Y
(2, 'Excelente laptop, ideal para trabajar y estudiar.', 5, '2024-09-02', 5, 5, 1, 41,1),
(2, 'El rendimiento es bueno, pero el envío fue muy lento.', 3, '2024-09-07', 3, 4, 0, 42,1),
(2, 'Producto de calidad, aunque la atención podría mejorar.', 4, '2024-09-12', 4, 5, 1, 43,1),

-- Reseñas para el producto 3: Auriculares Z
(3, 'Auriculares cómodos y con buena calidad de sonido.', 5, '2024-09-03', 5, 5, 1, 47,1),
(3, 'Buena relación calidad-precio, aunque podrían mejorar la calidad.', 4, '2024-09-08', 4, 3, 1, 48,1),
(3, 'La entrega fue rápida, pero la calidad del producto no es la mejor.', 2, '2024-09-14', 2, 2, 1, 49,1),

-- Reseñas para el producto 4: Cámara Fotográfica
(4, 'Impresionante calidad de imagen, lo recomiendo para fotógrafos.', 5, '2024-09-04', 5, 5, 1, 53,1),
(4, 'Buena cámara, pero la atención al cliente fue regular.', 4, '2024-09-09', 3, 4, 0, 54,1),

-- Reseñas para el producto 5: Tablet W
(5, 'Buena tableta para diseñadores, llegó rápido.', 5, '2024-09-05', 5, 5, 1, 50,1),
(5, 'Funcional, pero el envío fue más lento de lo esperado.', 3, '2024-09-10', 3, 4, 0, 51,1),

-- Reseñas para el producto 6: Impresora 3D
(6, 'La impresora funciona muy bien, aunque tardó en llegar.', 4, '2024-09-06', 4, 5, 0, 55,1),
(6, 'Producto de calidad, pero la atención fue deficiente.', 3, '2024-09-11', 2, 4, 0, 56,1),

-- Reseñas para el producto 7: Drone Pro
(7, 'El drone es excelente, todo perfecto y llegó rápido.', 5, '2024-09-07', 5, 5, 1, 57,1),
(7, 'Buen producto, pero la calidad de construcción podría mejorar.', 4, '2024-09-12', 3, 4, 1, 58,1),

-- Reseñas para el producto 8: Consola de Videojuegos
(8, 'Espectacular consola, la entrega fue rápida y sin problemas.', 5, '2024-09-08', 5, 5, 1, 53,1),
(8, 'La consola es buena, pero me tardó demasiado en llegar.', 3, '2024-09-13', 2, 4, 0, 54,1),

-- Reseñas para el producto 9: Reloj Inteligente
(9, 'Excelente reloj, el monitoreo de salud es preciso.', 5, '2024-09-09', 5, 5, 1, 40,1),
(9, 'Buen producto, pero la batería dura poco.', 3, '2024-09-14', 3, 3, 1, 39,1),

-- Reseñas para el producto 10: Teclado Mecánico
(10, 'El teclado es increíble para jugar, lo recomiendo.', 5, '2024-09-10', 5, 5, 1, 41,1),
(10, 'Buen teclado, pero esperaba algo mejor en la calidad de los materiales.', 4, '2024-09-15', 3, 4, 1, 49,1);

INSERT INTO `dogless`.`resenas` 
(`idproductos`, `comentario`, `fecha`, `usuarioid`, `tipo`)
VALUES
-- Preguntas frecuentes para el producto 1: Smartphone X
(1, '¿Este smartphone tiene garantía internacional?', '2024-09-20', 35, 2),
(1, '¿Incluye cargador y auriculares en la caja?', '2024-09-21', 36, 2),

-- Preguntas frecuentes para el producto 2: Laptop Y
(2, '¿Puedo expandir la memoria RAM de esta laptop?', '2024-09-22', 41, 2),
(2, '¿Es compatible con programas de edición de video pesados?', '2024-09-23', 42, 2),

-- Preguntas frecuentes para el producto 3: Auriculares Z
(3, '¿Tienen cancelación de ruido activa?', '2024-09-24', 47, 2),
(3, '¿Son compatibles con dispositivos Android y iOS?', '2024-09-25', 48, 2),

-- Preguntas frecuentes para el producto 4: Cámara Fotográfica
(4, '¿Viene con accesorios como lentes adicionales?', '2024-09-26', 53, 2),
(4, '¿Se puede conectar vía WiFi para transferir fotos?', '2024-09-27', 54, 2),

-- Preguntas frecuentes para el producto 5: Tablet W
(5, '¿Es compatible con un lápiz digital?', '2024-09-28', 50, 2),
(5, '¿Se le puede añadir una tarjeta de memoria?', '2024-09-29', 51, 2),

-- Preguntas frecuentes para el producto 6: Impresora 3D
(6, '¿Qué tipo de filamento usa esta impresora?', '2024-09-30', 55, 2),
(6, '¿Necesita algún tipo de software específico para funcionar?', '2024-10-01', 56, 2),

-- Preguntas frecuentes para el producto 7: Drone Pro
(7, '¿Cuál es la duración de la batería en vuelo?', '2024-10-02', 57, 2),
(7, '¿Incluye control remoto o solo se maneja con la app?', '2024-10-03', 58, 2),

-- Preguntas frecuentes para el producto 8: Consola de Videojuegos
(8, '¿Es compatible con juegos de generaciones anteriores?', '2024-10-04', 53, 2),
(8, '¿Incluye algún juego o debo comprarlos aparte?', '2024-10-05', 54, 2),

-- Preguntas frecuentes para el producto 9: Reloj Inteligente
(9, '¿Es resistente al agua?', '2024-10-06', 40, 2),
(9, '¿Tiene GPS integrado?', '2024-10-07', 39, 2),

-- Preguntas frecuentes para el producto 10: Teclado Mecánico
(10, '¿Tiene retroiluminación en las teclas?', '2024-10-08', 41, 2),
(10, '¿Es compatible con sistemas Mac?', '2024-10-09', 49, 2);


-- -----------------------------------------------------
-- Table `dogless`.`stockproductos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`stockproductos` (
  `idproductos` INT NOT NULL,
  `cantidad` INT NULL,
  `idzonas` INT NOT NULL,
  `borrado` TINYINT DEFAULT 1,
  INDEX `idproductos_idx` (`idproductos` ASC) VISIBLE,
  INDEX `fk_stockproductos_zonas1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `idproductos`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_stockproductos_zonas1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
-- Insertar datos en la tabla `stockproductos`
INSERT INTO `dogless`.`stockproductos` 
(`idproductos`, `cantidad`, `idzonas`)
VALUES
-- Stock para la zonas Norte (ID zonas 1)
(1, 50, 1), -- 50 unidades de Smartphone X en la zona Norte
(2, 30, 1), -- 30 unidades de Laptop Y en la zona Norte
(3, 100, 1), -- 100 unidades de Auriculares Z en la zona Norte
(4, 15, 1), -- 15 unidades de Cámara Fotográfica en la zona Norte
(5, 40, 1), -- 40 unidades de Tablet W en la zona Norte
(6, 10, 1), -- 10 unidades de Impresora 3D en la zona Norte
(8, 10, 1), -- 10 unidades 
(9, 10, 1), -- 10 unidades 

-- Stock para la zonas Sur (ID zonas 2)
(1, 40, 2), -- 40 unidades de Smartphone X en la zona Sur
(2, 25, 2), -- 25 unidades de Laptop Y en la zona Sur
(3, 90, 2), -- 90 unidades de Auriculares Z en la zona Sur
(7, 20, 2), -- 20 unidades de Drone Pro en la zona Sur
(8, 35, 2), -- 35 unidades de Consola de Videojuegos en la zona Sur

-- Stock para la zonas Este (ID zonas 3)
(1, 60, 3), -- 60 unidades de Smartphone X en la zona Este
(4, 10, 3), -- 10 unidades de Cámara Fotográfica en la zona Este
(5, 50, 3), -- 50 unidades de Tablet W en la zona Este
(6, 5, 3),  -- 5 unidades de Impresora 3D en la zona Este
(9, 70, 3), -- 70 unidades de Reloj Inteligente en la zona Este
(10, 80, 3), -- 80 unidades de Teclado Mecánico en la zona Este

-- Stock para la zonas Oeste (ID zonas 4)
(2, 20, 4), -- 20 unidades de Laptop Y en la zona Oeste
(3, 120, 4), -- 120 unidades de Auriculares Z en la zona Oeste
(7, 15, 4), -- 15 unidades de Drone Pro en la zona Oeste
(9, 50, 4), -- 50 unidades de Reloj Inteligente en la zona Oeste
(10, 65, 4); -- 65 unidades de Teclado Mecánico en la zona Oeste

-- Solicitudes
INSERT INTO `dogless`.`solicitudes` (`idsolicitudes`, `veredicto`, `idusuarios`, `comentario`) 
VALUES 
(1, NULL, '40', NULL),
(2, NULL, '41', NULL),
(3, NULL, '42', NULL),
(4, NULL, '43', NULL),
(5, NULL, '44', NULL),
(6, '1', '16', 'Gracias por tu interés. Hemos recibido tu solicitud y está en proceso.'),
(7, '1', '15', 'Enhorabuena, tu solicitud ha sido aceptada. Nos pondremos en contacto contigo.'),
(8, '0', '14', 'No has sido seleccionado en esta ocasión. Te invitamos a postular nuevamente en el futuro.'),
(9, '1', '13', 'Tu solicitud ha sido aprobada condicionalmente, faltan documentos.'),
(10, '1', '11', 'Estamos evaluando tu perfil. Te informaremos en los próximos días.'),
(11, '1', '12', 'Has sido aceptado. Pronto recibirás más información.');

-- -----------------------------------------------------
-- Table `dogless`.`reclamos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`reclamos` (
  `idreclamos` INT NOT NULL AUTO_INCREMENT,
  `descripcion` VARCHAR(1000) NULL,
  `idusuarios` INT NOT NULL,
  PRIMARY KEY (`idreclamos`),
  INDEX `fk_reclamos_usuarios1_idx` (`idusuarios` ASC) VISIBLE,
  CONSTRAINT `fk_reclamos_usuarios1`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Table `dogless`.`reportes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`reportes` (
  `idreportes` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `tipo` VARCHAR(45) NULL,
  `fecha` DATETIME NULL,
  `descripcion` VARCHAR(300) NULL,
  `idusuarios` INT NOT NULL,
  `borrado` TINYINT DEFAULT 1,
  PRIMARY KEY (`idreportes`),
  INDEX `fk_reportes_usuarios1_idx` (`idusuarios` ASC) VISIBLE,
  CONSTRAINT `fk_reportes_usuarios1`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Datos de Reportes

INSERT INTO `dogless`.`reportes` (`nombre`, `tipo`, `fecha`, `descripcion`, `idusuarios`) VALUES
('Reporte 1', 'Reporte Ordenes Totales', '2024-09-05 10:30:00', 'Resumen de órdenes de la primera semana de septiembre', 3),
('Reporte 2', 'Reporte por Agente', '2024-09-12 14:45:00', 'Desempeño del agente Carlos Ruiz en la segunda semana', 1),
('Reporte 3', 'Reporte por Fecha', '2024-09-18 09:15:00', 'Análisis de ventas de mediados de mes', 4),
('Reporte 4', 'Reporte por Usuario', '2024-09-22 16:00:00', 'Actividad del usuario María González en la tercera semana', 2),
('Reporte 5', 'Reporte Ordenes Totales', '2024-09-07 11:30:00', 'Resumen de órdenes del primer fin de semana', 5),
('Reporte 6', 'Reporte por Agente', '2024-09-15 13:45:00', 'Evaluación quincenal del agente Ana Martínez', 3),
('Reporte 7', 'Reporte por Fecha', '2024-09-25 10:00:00', 'Comparativa de ventas de la cuarta semana', 1),
('Reporte 8', 'Reporte por Usuario', '2024-09-28 15:30:00', 'Resumen de actividades del usuario Juan Pérez', 4),
('Reporte 9', 'Reporte Ordenes Totales', '2024-09-20 09:45:00', 'Análisis de órdenes canceladas a mitad de mes', 2),
('Reporte 10', 'Reporte por Fecha', '2024-09-30 17:00:00', 'Reporte de cierre mensual de septiembre', 5);

-- -----------------------------------------------------
-- Table `dogless`.`tarjetas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`tarjetas` (
  `idtarjetas` INT NOT NULL AUTO_INCREMENT,
  `numero` VARCHAR(45) NULL,
  `tipo` VARCHAR(45) NULL,
  `cvv` VARCHAR(45) NULL,
  `fecha` DATE NULL,
  PRIMARY KEY (`idtarjetas`))
ENGINE = InnoDB;

INSERT INTO `dogless`.`tarjetas` (`numero`, `tipo`, `cvv`, `fecha`) VALUES
('4532856974125896', 'BCP', '453', '2024-08-15'),
('4785123698547412', 'BBVA', '789', '2025-03-22'),
('5632147896321458', 'Interbank', '321', '2024-12-01'),
('4589632587413698', 'Scotiabank', '147', '2026-01-30'),
('4752136987456321', 'BCP', '963', '2025-07-14'),
('5478963214589632', 'BBVA', '258', '2024-09-28'),
('4589632514789632', 'Interbank', '741', '2025-11-05'),
('5632147896325874', 'Scotiabank', '852', '2026-04-17'),
('4785236987451236', 'BCP', '369', '2024-06-23'),
('5478963215478963', 'BBVA', '159', '2025-05-09');

-- -----------------------------------------------------
-- Table `dogless`.`mensajes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`mensajes` (
  `idmensajes` INT NOT NULL AUTO_INCREMENT,
  `texto` VARCHAR(200) NULL,
  `idusuarios` INT NOT NULL,
  PRIMARY KEY (`idmensajes`),
  INDEX `fk_mensajes_usuarios1_idx` (`idusuarios` ASC) VISIBLE,
  CONSTRAINT `fk_mensajes_usuarios1`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`conversaciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`conversaciones` (
  `idconversaciones` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(255) NOT NULL,
  `fecha_inicio` DATETIME NOT NULL,
  `fecha_fin` DATETIME NULL,
  `estado` VARCHAR(50) DEFAULT 'activa',
  PRIMARY KEY (`idconversaciones`)
)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`messages` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `content` VARCHAR(1000) NOT NULL,
  `sender` VARCHAR(50) NOT NULL,
  `timestamp` DATETIME NOT NULL,
  `idusuarios` INT NULL,
  `idconversaciones` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_messages_usuarios_idx` (`idusuarios` ASC) VISIBLE,
  INDEX `fk_messages_conversaciones_idx` (`idconversaciones` ASC) VISIBLE,
  CONSTRAINT `fk_messages_usuarios`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_messages_conversaciones`
    FOREIGN KEY (`idconversaciones`)
    REFERENCES `dogless`.`conversaciones` (`idconversaciones`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
