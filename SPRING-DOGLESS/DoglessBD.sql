-- MySQL Workbench Forward Engineering
-- The Awesome Dogless Database

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
-- Table `dogless`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`usuarios` (
  `idusuarios` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `apellido` VARCHAR(45) NULL,
  `dni` VARCHAR(45) NULL,
  `correo` VARCHAR(45) NULL,
  `contrasena` VARCHAR(45) NULL,
  `telefono` VARCHAR(45) NULL,
  `direccion` VARCHAR(45) NULL,
  `idroles` INT NOT NULL,
  `iddistritos` INT NOT NULL,
  `idadminzonales` INT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL,
  `ruc` VARCHAR(11) NULL,
  `codigoaduana` VARCHAR(45) NULL,
  `razonsocial` VARCHAR(45) NULL,
  `codigojurisdiccion` VARCHAR(45) NULL,
  `idzonas` INT NOT NULL,
  `fechanacimiento` DATE NULL,
  `fechabaneo` DATE NULL,
  `borrado` TINYINT DEFAULT 1,
  PRIMARY KEY (`idusuarios`),
  INDEX `idroles_idx` (`idroles` ASC) VISIBLE,
  INDEX `iddistritos_idx` (`iddistritos` ASC) VISIBLE,
  INDEX `idadminzonales_idx` (`idadminzonales` ASC) VISIBLE,
  INDEX `fk_usuarios_zonas1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `idroles`
    FOREIGN KEY (`idroles`)
    REFERENCES `dogless`.`roles` (`idroles`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `iddistritos`
    FOREIGN KEY (`iddistritos`)
    REFERENCES `dogless`.`distritos` (`iddistritos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `idadminzonales`
    FOREIGN KEY (`idadminzonales`)
    REFERENCES `dogless`.`adminzonales` (`idadminzonaleses`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuarios_zonas1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

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

-- 1. Super Administradores (ID rol = 1)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `correo`, `contrasena`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`)
VALUES 
(1, 'Fernando', 'Pérez', '11223344', 'fernando.perez@dogless.com', 'adminpass123', '987654321', NULL, 1, 1, 0, 'activo', NULL, NULL, NULL, NULL, 1),
(2, 'Luisa', 'Gómez', '22334455', 'luisa.gomez@dogless.com', 'jefepass456', '987654322', NULL, 1, 1, 0, 'activo', NULL, NULL, NULL, NULL, 1);

INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `correo`, `contrasena`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`)
VALUES
-- zonas Norte (Administradores zonales ID 1 y 2)
(3, 'Juan', 'Pérez', '12345678', 'juan.perez@norte.com', 'password123', '987654321', 'Calle Norte 101', 2, 1, 1, 'activo', NULL, NULL, NULL, NULL, 1),
(4, 'María', 'Gómez', '23456789', 'maria.gomez@norte.com', 'password456', '987654322', 'Calle Norte 102', 2, 2, 2, 'activo', NULL, NULL, NULL, NULL, 1),

-- zonas Sur (Administradores zonales ID 3 y 4)
(5, 'Carlos', 'Ramírez', '34567890', 'carlos.ramirez@sur.com', 'password789', '987654323', 'Av. Sur 101', 2, 9, 3, 'activo', NULL, NULL, NULL, NULL, 2),
(6, 'Luisa', 'Fernández', '45678901', 'luisa.fernandez@sur.com', 'password101', '987654324', 'Av. Sur 102', 2, 10, 4, 'activo', NULL, NULL, NULL, NULL, 2),

-- zonas Este (Administradores zonales ID 5 y 6)
(7, 'Ricardo', 'López', '56789012', 'ricardo.lopez@este.com', 'password202', '987654325', 'Calle Este 101', 2, 19, 5, 'activo', NULL, NULL, NULL, NULL, 3),
(8, 'Ana', 'Martínez', '67890123', 'ana.martinez@este.com', 'password303', '987654326', 'Av. Este 102', 2, 20, 6, 'activo', NULL, NULL, NULL, NULL, 3),

-- zonas Oeste (Administradores zonales ID 7 y 8)
(9, 'Pedro', 'Sánchez', '78901234', 'pedro.sanchez@oeste.com', 'password404', '987654327', 'Calle Oeste 101', 2, 26, 7, 'activo', NULL, NULL, NULL, NULL, 4),
(10, 'Gabriela', 'Navarro', '89012345', 'gabriela.navarro@oeste.com', 'password1112', '987654328', 'Av. Oeste 102', 2, 27, 8, 'activo', NULL, NULL, NULL, NULL, 4);

-- Agentes zonas Norte (Admin zonas 1Y 2)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `correo`, `contrasena`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`)
VALUES
(11, 'Jorge', 'Ramírez', '33445566', 'jorge.ramirez@norte.com', 'agenteNpass123', '987654323', 'Calle Norte 123', 3, 1, 1, 'activo', '12345678901', 'ADU001', 'Agente Norte', 'JUR001', 1),
(12, 'Rosa', 'Soto', '44556677', 'rosa.soto@norte.com', 'agenteNpass456', '987654324', 'Av. Norte 456', 3, 2, 1, 'activo', '10987654321', 'ADU002', 'Agente Norte', 'JUR002', 1),
(13, 'Luis', 'Gómez', '55667788', 'luis.gomez@norte.com', 'agenteNpass789', '987654325', 'Calle Norte 789', 3, 3, 1, 'activo', '10987654322', 'ADU003', 'Agente Norte', 'JUR003', 1),
(14, 'María', 'López', '66778899', 'maria.lopez@norte.com', 'agenteNpass012', '987654326', 'Av. Norte 012', 3, 4, 2, 'activo', '10987654323', 'ADU004', 'Agente Norte', 'JUR004', 1),
(15, 'Carlos', 'Rivas', '77889900', 'carlos.rivas@norte.com', 'agenteNpass345', '987654327', 'Calle Norte 345', 3, 5, 2, 'activo', '10987654324', 'ADU005', 'Agente Norte', 'JUR005', 1),
(16, 'Patricia', 'Flores', '88990011', 'patricia.flores@norte.com', 'agenteNpass678', '987654328', 'Av. Norte 678', 3, 6, 2, 'activo', '10987654325', 'ADU006', 'Agente Norte', 'JUR006', 1);

-- Agentes zonas Sur (Admin zonas 3 Y 4)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `correo`, `contrasena`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`)
VALUES
(17, 'Laura', 'Martínez', '99001122', 'laura.martinez@sur.com', 'agenteSpass123', '987654329', 'Calle Sur 123', 3, 9, 3, 'activo', '12345678902', 'ADU007', 'Agente Sur', 'JUR007', 2),
(18, 'Andrés', 'Pérez', '11002233', 'andres.perez@sur.com', 'agenteSpass456', '987654330', 'Av. Sur 456', 3, 10, 3, 'activo', '10987654326', 'ADU008', 'Agente Sur', 'JUR008', 2),
(19, 'Claudia', 'Vega', '22003344', 'claudia.vega@sur.com', 'agenteSpass789', '987654331', 'Calle Sur 789', 3, 11, 3, 'activo', '10987654327', 'ADU009', 'Agente Sur', 'JUR009', 2),
(20, 'Roberto', 'Ramos', '33004455', 'roberto.ramos@sur.com', 'agenteSpass012', '987654332', 'Av. Sur 012', 3, 12, 4, 'activo', '10987654328', 'ADU010', 'Agente Sur', 'JUR010', 2),
(21, 'Sofía', 'Torres', '44005566', 'sofia.torres@sur.com', 'agenteSpass345', '987654333', 'Calle Sur 345', 3, 13, 4, 'activo', '10987654329', 'ADU011', 'Agente Sur', 'JUR011', 2),
(22, 'Pablo', 'Cruz', '55006677', 'pablo.cruz@sur.com', 'agenteSpass678', '987654334', 'Av. Sur 678', 3, 14, 4, 'activo', '10987654330', 'ADU012', 'Agente Sur', 'JUR012', 2);


-- Agentes zonas Este (Admin zonas 5 y 6)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `correo`, `contrasena`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`)
VALUES
(23, 'Martín', 'Hernández', '66007788', 'martin.hernandez@este.com', 'agenteEpass123', '987654335', 'Calle Este 123', 3, 19, 5, 'activo', '12345678903', 'ADU013', 'Agente Este', 'JUR013', 3),
(24, 'Verónica', 'Díaz', '77008899', 'veronica.diaz@este.com', 'agenteEpass456', '987654336', 'Av. Este 456', 3, 20, 5, 'activo', '10987654331', 'ADU014', 'Agente Este', 'JUR014', 3),
(25, 'Alberto', 'Cabrera', '88009900', 'alberto.cabrera@este.com', 'agenteEpass789', '987654337', 'Calle Este 789', 3, 21, 5, 'activo', '10987654332', 'ADU015', 'Agente Este', 'JUR015', 3),
(26, 'Lucía', 'Ortiz', '99001122', 'lucia.ortiz@este.com', 'agenteEpass012', '987654338', 'Av. Este 012', 3, 22, 6, 'activo', '10987654333', 'ADU016', 'Agente Este', 'JUR016', 3),
(27, 'Tomás', 'Fernández', '11002233', 'tomas.fernandez@este.com', 'agenteEpass345', '987654339', 'Calle Este 345', 3, 23, 6, 'activo', '10987654334', 'ADU017', 'Agente Este', 'JUR017', 3),
(28, 'Raquel', 'Paredes', '12003344', 'raquel.paredes@este.com', 'agenteEpass678', '987654340', 'Av. Este 678', 3, 24, 6, 'activo', '10987654335', 'ADU018', 'Agente Este', 'JUR018', 3);

-- Agentes zonas Oeste (Admin zonas 7  y 8)
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `correo`, `contrasena`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`)
VALUES
(29, 'Raúl', 'González', '13004455', 'raul.gonzalez@oeste.com', 'agenteWpass123', '987654341', 'Calle Oeste 123', 3, 26, 7, 'activo', '12345678904', 'ADU019', 'Agente Oeste', 'JUR019', 4),
(30, 'Diana', 'Campos', '14005566', 'diana.campos@oeste.com', 'agenteWpass456', '987654342', 'Av. Oeste 456', 3, 27, 7, 'activo', '10987654336', 'ADU020', 'Agente Oeste', 'JUR020', 4),
(31, 'Emilio', 'Méndez', '15006677', 'emilio.mendez@oeste.com', 'agenteWpass789', '987654343', 'Calle Oeste 789', 3, 28, 7, 'activo', '10987654337', 'ADU021', 'Agente Oeste', 'JUR021', 4),
(32, 'Paola', 'Moreno', '16007788', 'paola.moreno@oeste.com', 'agenteWpass012', '987654344', 'Av. Oeste 012', 3, 29, 8, 'activo', '10987654338', 'ADU022', 'Agente Oeste', 'JUR022', 4),
(33, 'Felipe', 'Santos', '17008899', 'felipe.santos@oeste.com', 'agenteWpass345', '987654345', 'Calle Oeste 345', 3, 30, 8, 'activo', '10987654339', 'ADU023', 'Agente Oeste', 'JUR023', 4),
(34, 'Alejandra', 'Ramos', '18009900', 'alejandra.ramos@oeste.com', 'agenteWpass678', '987654346', 'Av. Oeste 678', 3, 31, 8, 'activo', '10987654340', 'ADU024', 'Agente Oeste', 'JUR024', 4);

-- Usuarios Finales 
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `correo`, `contrasena`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojurisdiccion`, `idzonas`)
VALUES
(35, 'Laura', 'Suárez', '22334455', 'laura.suarez@norte.com', 'userFpass1!', '987654347', 'Calle Norte 1', 4, 1, NULL, 'activo', NULL, NULL, NULL, NULL, 1),
(36, 'Javier', 'Ortiz', '33445566', 'javier.ortiz@norte.com', 'userFpass2#$', '987654348', 'Calle Norte 2', 4, 2, NULL, 'activo', NULL, NULL, NULL, NULL, 1),
(37, 'María', 'Santos', '44556677', 'maria.santos@norte.com', 'userFpass3&*', '987654349', 'Av. Norte 3', 4, 3, NULL, 'activo', NULL, NULL, NULL, NULL, 1),
(38, 'Raúl', 'Pérez', '55667788', 'raul.perez@norte.com', 'userFpass4@!', '987654350', 'Calle Norte 4', 4, 4, NULL, 'activo', NULL, NULL, NULL, NULL, 1),
(39, 'Carla', 'Mendoza', '66778899', 'carla.mendoza@norte.com', 'userFpass5^&', '987654351', 'Av. Norte 5', 4, 5, NULL, 'activo', NULL, NULL, NULL, NULL, 1),
(40, 'Luis', 'García', '77889900', 'luis.garcia@norte.com', 'userFpass6*%', '987654352', 'Calle Norte 6', 4, 6, NULL, 'baneado', NULL, NULL, NULL, NULL, 1),
(41, 'Sofía', 'Rodríguez', '88990011', 'sofia.rodriguez@sur.com', 'userFpass7$$', '987654353', 'Calle Sur 1', 4, 9, NULL, 'activo', NULL, NULL, NULL, NULL, 2),
(42, 'Juan', 'Morales', '99001122', 'juan.morales@sur.com', 'userFpass8!!', '987654354', 'Av. Sur 2', 4, 10, NULL, 'baneado', NULL, NULL, NULL, NULL, 2),
(43, 'Mónica', 'Ruiz', '11002233', 'monica.ruiz@sur.com', 'userFpass9@@', '987654355', 'Calle Sur 3', 4, 11, NULL, 'activo', NULL, NULL, NULL, NULL, 2),
(44, 'Carlos', 'Martínez', '12003344', 'carlos.martinez@sur.com', 'userFpass10%%', '987654356', 'Av. Sur 4', 4, 12, NULL, 'activo', NULL, NULL, NULL, NULL, 2),
(45, 'Julia', 'Castro', '13004455', 'julia.castro@sur.com', 'userFpass11^^', '987654357', 'Calle Sur 5', 4, 13, NULL, 'inactivo', NULL, NULL, NULL, NULL, 2),
(46, 'Pablo', 'Fernández', '14005566', 'pablo.fernandez@sur.com', 'userFpass12&&', '987654358', 'Av. Sur 6', 4, 14, NULL, 'activo', NULL, NULL, NULL, NULL, 2),
(47, 'Andrés', 'Silva', '15006677', 'andres.silva@este.com', 'userFpass13**', '987654359', 'Calle Este 1', 4, 19, NULL, 'activo', NULL, NULL, NULL, NULL, 3),
(48, 'Elena', 'Reyes', '16007788', 'elena.reyes@este.com', 'userFpass14##', '987654360', 'Av. Este 2', 4, 20, NULL, 'activo', NULL, NULL, NULL, NULL, 3),
(49, 'Hugo', 'Cano', '17008899', 'hugo.cano@este.com', 'userFpass15@@', '987654361', 'Calle Este 3', 4, 21, NULL, 'activo', NULL, NULL, NULL, NULL, 3),
(50, 'Ana', 'Ramírez', '18009900', 'ana.ramirez@este.com', 'userFpass16$$', '987654362', 'Av. Este 4', 4, 22, NULL, 'baneado', NULL, NULL, NULL, NULL, 3),
(51, 'David', 'Paredes', '19001111', 'david.paredes@este.com', 'userFpass17##', '987654363', 'Calle Este 5', 4, 23, NULL, 'inactivo', NULL, NULL, NULL, NULL, 3),
(52, 'Carolina', 'Gómez', '20002222', 'carolina.gomez@este.com', 'userFpass18&&', '987654364', 'Av. Este 6', 4, 24, NULL, 'activo', NULL, NULL, NULL, NULL, 3),
(53, 'Carmen', 'López', '21003333', 'carmen.lopez@oeste.com', 'userFpass19%%', '987654365', 'Calle Oeste 1', 4, 26, NULL, 'activo', NULL, NULL, NULL, NULL, 4),
(54, 'Sergio', 'Castillo', '22004444', 'sergio.castillo@oeste.com', 'userFpass20!!', '987654366', 'Av. Oeste 2', 4, 27, NULL, 'activo', NULL, NULL, NULL, NULL, 4),
(55, 'Isabel', 'Mendoza', '23005555', 'isabel.mendoza@oeste.com', 'userFpass21##', '987654367', 'Calle Oeste 3', 4, 28, NULL, 'activo', NULL, NULL, NULL, NULL, 4),
(56, 'Jorge', 'Vega', '24006666', 'jorge.vega@oeste.com', 'userFpass22$$', '987654368', 'Av. Oeste 4', 4, 29, NULL, 'inactivo', NULL, NULL, NULL, NULL, 4),
(57, 'Lucía', 'Carrillo', '25007777', 'lucia.carrillo@oeste.com', 'userFpass23&&', '987654369', 'Calle Oeste 5', 4, 30, NULL, 'activo', NULL, NULL, NULL, NULL, 4),
(58, 'Emilio', 'Vargas', '26008888', 'emilio.vargas@oeste.com', 'userFpass24**', '987654370', 'Av. Oeste 6', 4, 31, NULL, 'activo', NULL, NULL, NULL, NULL, 4);
-- -----------------------------------------------------
-- Table `dogless`.`ordenes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`ordenes` (
  `idordenes` INT NOT NULL AUTO_INCREMENT,
  `estado` ENUM('Creado', 'En Validación', 'En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta', 'Recibido') NULL DEFAULT NULL,
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
('Recibido', '2024-09-04', 'Av. Sur 4, Lima', 1199.99, 'tarjeta', 42),
-- Orden 5 para el usuario 47
('En Aduanas', '2024-09-05', 'Calle Este 1, Lima', 750.99, 'tarjeta', 47),
-- Orden 6 para el usuario 48
('Arribo al País', '2024-09-06', 'Av. Este 2, Lima', 799.99, 'tarjeta', 48),
-- Orden 7 para el usuario 53
('En Ruta', '2024-09-07', 'Calle Oeste 1, Lima', 599.98, 'tarjeta', 53),
-- Orden 8 para el usuario 54
('Creado', '2024-09-08', 'Av. Oeste 2, Lima', 999.98, 'tarjeta', 54),
-- Orden 9 para el usuario 49
('En Validación', '2024-09-09', 'Calle Este 3, Lima', 129.98, 'tarjeta', 49),
-- Orden 10 para el usuario 55
('En Aduanas', '2024-09-10', 'Calle Norte 5, Lima', 1350.50, 'tarjeta', 55);


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
('Carla', 'Vega', '987654330', '77889900112', '01234567', 'PCMaster', 'activo');



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
(`idproductos`, `comentario`, `satisfaccion`, `fecha`, `atencion`, `calidad`, `serecibiorapido`, `usuarioid`)
VALUES
-- Reseñas para el producto 1: Smartphone X
(1, 'Gran producto, funciona perfectamente y llegó a tiempo.', 5, '2024-09-01', 5, 5, 1, 35),
(1, 'Buen teléfono, pero la entrega fue más lenta de lo esperado.', 4, '2024-09-05', 4, 4, 0, 36),
(1, 'Calidad aceptable, aunque esperaba algo más.', 3, '2024-09-10', 3, 3, 1, 37),

-- Reseñas para el producto 2: Laptop Y
(2, 'Excelente laptop, ideal para trabajar y estudiar.', 5, '2024-09-02', 5, 5, 1, 41),
(2, 'El rendimiento es bueno, pero el envío fue muy lento.', 3, '2024-09-07', 3, 4, 0, 42),
(2, 'Producto de calidad, aunque la atención podría mejorar.', 4, '2024-09-12', 4, 5, 1, 43),

-- Reseñas para el producto 3: Auriculares Z
(3, 'Auriculares cómodos y con buena calidad de sonido.', 5, '2024-09-03', 5, 5, 1, 47),
(3, 'Buena relación calidad-precio, aunque podrían mejorar la calidad.', 4, '2024-09-08', 4, 3, 1, 48),
(3, 'La entrega fue rápida, pero la calidad del producto no es la mejor.', 2, '2024-09-14', 2, 2, 1, 49),

-- Reseñas para el producto 4: Cámara Fotográfica
(4, 'Impresionante calidad de imagen, lo recomiendo para fotógrafos.', 5, '2024-09-04', 5, 5, 1, 53),
(4, 'Buena cámara, pero la atención al cliente fue regular.', 4, '2024-09-09', 3, 4, 0, 54),

-- Reseñas para el producto 5: Tablet W
(5, 'Buena tableta para diseñadores, llegó rápido.', 5, '2024-09-05', 5, 5, 1, 50),
(5, 'Funcional, pero el envío fue más lento de lo esperado.', 3, '2024-09-10', 3, 4, 0, 51),

-- Reseñas para el producto 6: Impresora 3D
(6, 'La impresora funciona muy bien, aunque tardó en llegar.', 4, '2024-09-06', 4, 5, 0, 55),
(6, 'Producto de calidad, pero la atención fue deficiente.', 3, '2024-09-11', 2, 4, 0, 56),

-- Reseñas para el producto 7: Drone Pro
(7, 'El drone es excelente, todo perfecto y llegó rápido.', 5, '2024-09-07', 5, 5, 1, 57),
(7, 'Buen producto, pero la calidad de construcción podría mejorar.', 4, '2024-09-12', 3, 4, 1, 58),

-- Reseñas para el producto 8: Consola de Videojuegos
(8, 'Espectacular consola, la entrega fue rápida y sin problemas.', 5, '2024-09-08', 5, 5, 1, 53),
(8, 'La consola es buena, pero me tardó demasiado en llegar.', 3, '2024-09-13', 2, 4, 0, 54),

-- Reseñas para el producto 9: Reloj Inteligente
(9, 'Excelente reloj, el monitoreo de salud es preciso.', 5, '2024-09-09', 5, 5, 1, 40),
(9, 'Buen producto, pero la batería dura poco.', 3, '2024-09-14', 3, 3, 1, 39),

-- Reseñas para el producto 10: Teclado Mecánico
(10, 'El teclado es increíble para jugar, lo recomiendo.', 5, '2024-09-10', 5, 5, 1, 41),
(10, 'Buen teclado, pero esperaba algo mejor en la calidad de los materiales.', 4, '2024-09-15', 3, 4, 1, 49);


-- -----------------------------------------------------
-- Table `dogless`.`stockproductos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`stockproductos` (
  `idproductos` INT NOT NULL,
  `cantidad` INT NULL,
  `idzonas` INT NOT NULL,
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
('5', '1', '16', 'Gracias por tu interés. Hemos recibido tu solicitud y está en proceso.'),
('6', '1', '15', 'Enhorabuena, tu solicitud ha sido aceptada. Nos pondremos en contacto contigo.'),
('7', '0', '14', 'No has sido seleccionado en esta ocasión. Te invitamos a postular nuevamente en el futuro.'),
('8', '1', '13', 'Tu solicitud ha sido aprobada condicionalmente, faltan documentos.'),
('9', '1', '11', 'Estamos evaluando tu perfil. Te informaremos en los próximos días.'),
('10', '1', '12', 'Has sido aceptado. Pronto recibirás más información.');

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
