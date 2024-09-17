-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


-- -----------------------------------------------------
-- Schema dogless
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema dogless
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dogless` DEFAULT CHARACTER SET utf8 ;
USE `dogless` ;

-- -----------------------------------------------------
-- Table `dogless`.`zona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`zona` (
  `idzonas` INT NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`adminzonales`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`adminzonales` (
  `idadminzonales` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `telefono` VARCHAR(9) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `clave` VARCHAR(45) NULL DEFAULT NULL,
  `idzonas` INT NOT NULL,
  PRIMARY KEY (`idadminzonales`),
  INDEX `fk_adminzonal_zona1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `fk_adminzonal_zona1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zona` (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`distritos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`distritos` (
  `iddistritos` INT NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `idzonas` INT NOT NULL,
  PRIMARY KEY (`iddistritos`),
  INDEX `fk_distritos_zona1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `fk_distritos_zona1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zona` (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`roles` (
  `idroles` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`idroles`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`usuarios` (
  `idusuarios` INT NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(45) NULL DEFAULT NULL,
  `correo` VARCHAR(45) NULL DEFAULT NULL,
  `clave` VARCHAR(45) NULL DEFAULT NULL,
  `telefono` VARCHAR(45) NULL DEFAULT NULL,
  `direccion` VARCHAR(45) NULL DEFAULT NULL,
  `idroles` INT NOT NULL,
  `iddistritos` INT NOT NULL,
  `idadminzonales` INT NOT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL DEFAULT NULL,
  `ruc` VARCHAR(11) NULL DEFAULT NULL,
  `codigoaduana` VARCHAR(45) NULL DEFAULT NULL,
  `razonsocial` VARCHAR(45) NULL DEFAULT NULL,
  `codigojuridiccion` VARCHAR(45) NULL DEFAULT NULL,
  `idzonas` INT NOT NULL,
  PRIMARY KEY (`idusuarios`),
  INDEX `idrol_idx` (`idroles` ASC) VISIBLE,
  INDEX `iddistrito_idx` (`iddistritos` ASC) VISIBLE,
  INDEX `adminzonalid_idx` (`idadminzonales` ASC) VISIBLE,
  INDEX `fk_usuarios_zona1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `adminzonalid`
    FOREIGN KEY (`idadminzonales`)
    REFERENCES `dogless`.`adminzonales` (`idadminzonales`),
  CONSTRAINT `distritoid`
    FOREIGN KEY (`iddistritos`)
    REFERENCES `dogless`.`distritos` (`iddistritos`),
  CONSTRAINT `fk_usuarios_zona1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zona` (`idzonas`),
  CONSTRAINT `idrol`
    FOREIGN KEY (`idroles`)
    REFERENCES `dogless`.`roles` (`idroles`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


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
  PRIMARY KEY (`idordenes`),
  INDEX `usuariosid_idx` (`idusuarios` ASC) VISIBLE,
  CONSTRAINT `usuariosid`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


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
  PRIMARY KEY (`idproveedores`),
  INDEX `fk_proveedores_proveedores1_idx` (`idproveedores` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`productos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`productos` (
  `idproductos` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `descripcion` VARCHAR(45) NULL DEFAULT NULL,
  `categoria` VARCHAR(45) NULL DEFAULT NULL,
  `precio` DECIMAL(10,2) NULL DEFAULT NULL,
  `costoenvio` DECIMAL(10,2) NULL DEFAULT NULL,
  `idproveedores` INT NOT NULL,
  `modelos` VARCHAR(100) NULL DEFAULT NULL,
  `colores` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`idproductos`),
  INDEX `proveedor_id_idx_productos` (`idproveedores` ASC) VISIBLE,
  CONSTRAINT `fk_proveedor_id_productos`
    FOREIGN KEY (`idproveedores`)
    REFERENCES `dogless`.`proveedores` (`idproveedores`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`detallesorden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`detallesorden` (
  `iddetallesorden` INT NOT NULL AUTO_INCREMENT,
  `idordenes` INT NOT NULL,
  `idproductos` INT NOT NULL,
  `cantidad` INT NULL DEFAULT NULL,
  `preciounitario` DECIMAL(10,2) NULL DEFAULT NULL,
  `subtotal` DECIMAL(10,2) NULL DEFAULT NULL,
  PRIMARY KEY (`iddetallesorden`),
  INDEX `id_orden_idx_detallesOrden` (`idordenes` ASC) VISIBLE,
  INDEX `id_producto_idx_detallesOrden` (`idproductos` ASC) VISIBLE,
  CONSTRAINT `fk_id_orden_detallesOrden`
    FOREIGN KEY (`idordenes`)
    REFERENCES `dogless`.`ordenes` (`idordenes`),
  CONSTRAINT `fk_id_producto_detallesOrden`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


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
  `idusuarios` INT NOT NULL,
  PRIMARY KEY (`idresenas`),
  INDEX `producto_id_idx_resenas` (`idproductos` ASC) VISIBLE,
  INDEX `usuarioid_idx` (`idusuarios` ASC) VISIBLE,
  CONSTRAINT `fk_producto_id_resenas`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`),
  CONSTRAINT `usuarioid`
    FOREIGN KEY (`idusuarios`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`stockproductos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`stockproductos` (
  `idproductos` INT NOT NULL,
  `cantidad` INT NULL DEFAULT NULL,
  `idzonas` INT NOT NULL,
  INDEX `productoid_idx` (`idproductos` ASC) VISIBLE,
  INDEX `fk_stockproductos_zona1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `fk_stockproductos_zona1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zona` (`idzonas`),
  CONSTRAINT `productoid`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

INSERT INTO `dogless`.`roles` (`idroles`, `nombre`) VALUES ('1', 'Superadmin');
INSERT INTO `dogless`.`roles` (`idroles`, `nombre`) VALUES ('2', 'Adminzonal');
INSERT INTO `dogless`.`roles` (`idroles`, `nombre`) VALUES ('3', 'Agente');
INSERT INTO `dogless`.`roles` (`idroles`, `nombre`) VALUES ('4', 'Usuario');

INSERT INTO `dogless`.`zona` (`idzonas`, `nombre`) VALUES
(1, 'Norte'),
(2, 'Sur'),
(3, 'Este'),
(4, 'Oeste');

INSERT INTO `dogless`.`distritos` (`iddistritos`, `nombre`, `idzonas`) VALUES
(1, 'Ancon', 1),
(2, 'Santa Rosa', 1),
(3, 'Carabayllo', 1),
(4, 'Puente Piedra', 1),
(5, 'Comas', 1),
(6, 'Los Olivos', 1),
(7, 'San Martin de Porres', 1),
(8, 'Independencia', 1),
(9, 'San Juan de Miraflores', 2),
(10, 'Villa Maria del Triunfo', 2),
(11, 'Villa el Salvador', 2),
(12, 'Pachacamac', 2),
(13, 'Lurin', 2),
(14, 'Punta Hermosa', 2),
(15, 'Punta Negra', 2),
(16, 'San Bartolo', 2),
(17, 'Santa Maria del Mar', 2),
(18, 'Pucusana', 2),
(19, 'San Juan de Lurigancho', 3),
(20, 'Lurigancho/Chosica', 3),
(21, 'Ate', 3),
(22, 'El Agustino', 3),
(23, 'Santa Anita', 3),
(24, 'La Molina', 3),
(25, 'Cieneguilla', 3),
(26, 'Rimac', 4),
(27, 'Cercado de Lima', 4),
(28, 'Breña', 4),
(29, 'Pueblo Libre', 4),
(30, 'Magdalena', 4),
(31, 'Jesus Maria', 4),
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

/*
-- Inserciones actualizadas para la tabla usuarios
INSERT INTO `dogless`.`usuarios` (`idusuarios`, `nombre`, `apellido`, `dni`, `correo`, `clave`, `telefono`, `direccion`, `idroles`, `iddistritos`, `idadminzonales`, `estado`, `ruc`, `codigoaduana`, `razonsocial`, `codigojuridiccion`, `idzonas`) VALUES
(1, 'Alejandro', 'Gómez', '1234567', 'agomez@gmail.com', '1234', '987654321', 'Av. Arequipa 123, Lima', 4, 5, 2, 1, '45-1234567-8', '1', NULL, 3, 2),
(2, 'María', 'López', '2345678', 'mlopez@gmail.com', '1234', '923456789', 'Jr. Cusco 456, Lima', 4, 12, 3, 1, '67-2345678-9', '1', NULL, 2, 4),
(3, 'Carlos', 'Martínez', '3456789', 'cmartinez@gmail.com', '1234', '945678901', 'Calle Los Pinos 789, Lima', 4, 8, 1, 0, '23-3456789-1', '1', NULL, 1, 1),
(4, 'Laura', 'Rodríguez', '4567890', 'lrodriguez@gmail.com', '1234', '978901234', 'Av. La Marina 1011, Lima', 4, 15, 4, 1, '89-4567890-2', '1', NULL, 4, 3),
(5, 'Javier', 'Fernández', '5678901', 'jfernandez@gmail.com', '1234', '912345678', 'Jr. Huancayo 1213, Lima', 4, 3, 2, 1, '34-5678901-5', '1', NULL, 2, 2),
(6, 'Ana', 'García', '6789012', 'agarcia@gmail.com', '1234', '956789012', 'Av. Brasil 1415, Lima', 4, 18, 1, 0, '78-6789012-3', '1', NULL, 3, 4),
(7, 'Pedro', 'Sánchez', '7890123', 'psanchez@gmail.com', '1234', '990123456', 'Calle Los Álamos 1617, Lima', 4, 7, 3, 1, '12-7890123-7', '1', NULL, 1, 1),
(8, 'Carmen', 'Pérez', '8901234', 'cperez@gmail.com', '1234', '934567890', 'Jr. Tacna 1819, Lima', 4, 20, 4, 1, '56-8901234-4', '1', NULL, 4, 3),
(9, 'Miguel', 'Torres', '9012345', 'mtorres@gmail.com', '1234', '967890123', 'Av. Javier Prado 2021, Lima', 4, 2, 2, 0, '90-9012345-6', '1', NULL, 2, 2),
(10, 'Isabel', 'Díaz', '0123456', 'idiaz@gmail.com', '1234', '901234567', 'Calle Las Palmeras 2223, Lima', 4, 14, 1, 1, '43-0123456-1', '1', NULL, 3, 4),
(11, 'Roberto', 'Vargas', '1234560', 'rvargas@gmail.com', '1234', '945678901', 'Jr. Iquitos 2425, Lima', 4, 9, 3, 1, '21-1234560-8', '1', NULL, 1, 1),
(12, 'Lucía', 'Mendoza', '2345601', 'lmendoza@gmail.com', '1234', '978901234', 'Av. Salaverry 2627, Lima', 4, 16, 4, 0, '87-2345601-3', '1', NULL, 4, 3),
(13, 'Fernando', 'Ruiz', '3456012', 'fruiz@gmail.com', '1234', '912345678', 'Calle Los Cipreses 2829, Lima', 4, 1, 2, 1, '32-3456012-5', '1', NULL, 2, 2),
(14, 'Sofía', 'Castro', '4560123', 'scastro@gmail.com', '1234', '956789012', 'Jr. Huánuco 3031, Lima', 4, 11, 1, 1, '76-4560123-7', '1', NULL, 3, 4),
(15, 'Diego', 'Herrera', '5601234', 'dherrera@gmail.com', '1234', '990123456', 'Av. Universitaria 3233, Lima', 4, 19, 3, 0, '10-5601234-9', '1', NULL, 1, 1);
*/