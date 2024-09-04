-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema dogless
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema dogless
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dogless` DEFAULT CHARACTER SET utf8 ;
USE `dogless` ;

-- -----------------------------------------------------
-- Table `dogless`.`zonas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`zonas` (
  `idzonas` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`adminzonal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`adminzonal` (
  `idadminZonal` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `clave` LONGTEXT NULL DEFAULT NULL,
  `zona_id` INT NULL DEFAULT NULL,
  `telefono` VARCHAR(9) NULL DEFAULT NULL,
  PRIMARY KEY (`idadminZonal`),
  INDEX `zona_id_idx_adminZonal` (`zona_id` ASC) VISIBLE,
  CONSTRAINT `fk_zona_id_adminzonal`
    FOREIGN KEY (`zona_id`)
    REFERENCES `dogless`.`zonas` (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`agente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`agente` (
  `idagente` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `contrasena` LONGTEXT NULL DEFAULT NULL,
  `zona_id` INT NULL DEFAULT NULL,
  `telefono` VARCHAR(9) NULL DEFAULT NULL,
  `codigoAduana` VARCHAR(4) NULL DEFAULT NULL,
  `ruc` VARCHAR(11) NULL DEFAULT NULL,
  `razonSocial` VARCHAR(45) NULL DEFAULT NULL,
  `direccion` VARCHAR(45) NULL DEFAULT NULL,
  `distrito` VARCHAR(45) NULL DEFAULT NULL,
  `codigoJuridisccion` VARCHAR(3) NULL DEFAULT NULL,
  PRIMARY KEY (`idagente`),
  INDEX `zona_id_idx_agente` (`zona_id` ASC) VISIBLE,
  CONSTRAINT `fk_zona_id_agente`
    FOREIGN KEY (`zona_id`)
    REFERENCES `dogless`.`zonas` (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`usuariofinal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`usuariofinal` (
  `idUsuarioFinal` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `contrasena` LONGTEXT NULL DEFAULT NULL,
  `direccion` VARCHAR(45) NULL DEFAULT NULL,
  `distrito` VARCHAR(45) NULL DEFAULT NULL,
  `telefono` VARCHAR(45) NULL DEFAULT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL DEFAULT NULL,
  PRIMARY KEY (`idUsuarioFinal`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`ordenes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`ordenes` (
  `idordenes` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NULL DEFAULT NULL,
  `estado` ENUM('Creado', 'En Validación', 'En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta', 'Recibido') NULL DEFAULT NULL,
  `fechaCreacion` DATE NULL DEFAULT NULL,
  `direccionEnvio` VARCHAR(100) NULL DEFAULT NULL,
  `total` DECIMAL(10,2) NULL DEFAULT NULL,
  `metodoPago` ENUM('tarjeta') NULL DEFAULT NULL,
  `agente_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idordenes`),
  INDEX `usuario_id_idx_ordenes` (`usuario_id` ASC) VISIBLE,
  INDEX `agente_id_idx_ordenes` (`agente_id` ASC) VISIBLE,
  CONSTRAINT `fk_agente_id_ordenes`
    FOREIGN KEY (`agente_id`)
    REFERENCES `dogless`.`agente` (`idagente`),
  CONSTRAINT `fk_usuario_id_ordenes`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `dogless`.`usuariofinal` (`idUsuarioFinal`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`proveedores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`proveedores` (
  `idproveedores` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `telefono` VARCHAR(45) NULL DEFAULT NULL,
  `ruc` VARCHAR(11) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `nombre_tienda` VARCHAR(45) NULL DEFAULT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL DEFAULT NULL,
  PRIMARY KEY (`idproveedores`))
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
  `costoEnvio` DECIMAL(10,2) NULL DEFAULT NULL,
  `cantidad` INT NULL DEFAULT NULL,
  `zona_id` INT NULL DEFAULT NULL,
  `proveedor_id` INT NULL DEFAULT NULL,
  `modelos` VARCHAR(100) NULL DEFAULT NULL,
  `colores` VARCHAR(100) NULL DEFAULT NULL,
  `stock` INT NULL,
  PRIMARY KEY (`idproductos`),
  INDEX `zonas_id_idx_productos` (`zona_id` ASC) VISIBLE,
  INDEX `proveedor_id_idx_productos` (`proveedor_id` ASC) VISIBLE,
  CONSTRAINT `fk_proveedor_id_productos`
    FOREIGN KEY (`proveedor_id`)
    REFERENCES `dogless`.`proveedores` (`idproveedores`),
  CONSTRAINT `fk_zonas_id_productos`
    FOREIGN KEY (`zona_id`)
    REFERENCES `dogless`.`zonas` (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`detallesorden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`detallesorden` (
  `iddetallesOrden` INT NOT NULL AUTO_INCREMENT,
  `id_orden` INT NULL DEFAULT NULL,
  `id_producto` INT NULL DEFAULT NULL,
  `cantidad` INT NULL DEFAULT NULL,
  `precioUnitario` DECIMAL(10,2) NULL DEFAULT NULL,
  `subtotal` DECIMAL(10,2) NULL DEFAULT NULL,
  PRIMARY KEY (`iddetallesOrden`),
  INDEX `id_orden_idx_detallesOrden` (`id_orden` ASC) VISIBLE,
  INDEX `id_producto_idx_detallesOrden` (`id_producto` ASC) VISIBLE,
  CONSTRAINT `fk_id_orden_detallesOrden`
    FOREIGN KEY (`id_orden`)
    REFERENCES `dogless`.`ordenes` (`idordenes`),
  CONSTRAINT `fk_id_producto_detallesOrden`
    FOREIGN KEY (`id_producto`)
    REFERENCES `dogless`.`productos` (`idproductos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`distritos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`distritos` (
  `iddistrito` INT NOT NULL AUTO_INCREMENT,
  `id_zona` INT NULL DEFAULT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`iddistrito`),
  INDEX `id_zonas_idx_distritos` (`id_zona` ASC) VISIBLE,
  CONSTRAINT `fk_id_zona_distritos`
    FOREIGN KEY (`id_zona`)
    REFERENCES `dogless`.`zonas` (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`resenas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`resenas` (
  `idresenas` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NULL DEFAULT NULL,
  `producto_id` INT NULL DEFAULT NULL,
  `comentario` MEDIUMTEXT NULL DEFAULT NULL,
  `satisfaccion` INT NULL DEFAULT NULL,
  `fecha` DATE NULL DEFAULT NULL,
  `atencionAgente` INT NULL DEFAULT NULL,
  `calidad` TINYINT NULL DEFAULT NULL,
  `seRecibioRapido` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idresenas`),
  INDEX `usuario_id_idx_resenas` (`usuario_id` ASC) VISIBLE,
  INDEX `producto_id_idx_resenas` (`producto_id` ASC) VISIBLE,
  CONSTRAINT `fk_producto_id_resenas`
    FOREIGN KEY (`producto_id`)
    REFERENCES `dogless`.`productos` (`idproductos`),
  CONSTRAINT `fk_usuario_id_resenas`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `dogless`.`usuariofinal` (`idUsuarioFinal`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`superadmin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`superadmin` (
  `idsuperAdmin` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `clave` LONGTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`idsuperAdmin`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
