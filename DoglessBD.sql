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
    REFERENCES `dogless`.`zonas` (`idzonas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`distritos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`distritos` (
  `iddistrito` INT NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `idzonas` INT NOT NULL,
  PRIMARY KEY (`iddistrito`),
  INDEX `fk_distritos_zona1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `fk_distritos_zona1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`))
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
  `idusuarios` VARCHAR(2) NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(45) NULL DEFAULT NULL,
  `correo` VARCHAR(45) NULL DEFAULT NULL,
  `contraseña` VARCHAR(45) NULL DEFAULT NULL,
  `telefono` VARCHAR(45) NULL DEFAULT NULL,
  `direccion` VARCHAR(45) NULL DEFAULT NULL,
  `idrol` INT NOT NULL,
  `iddistrito` INT NOT NULL,
  `adminzonalid` INT NOT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL DEFAULT NULL,
  `ruc` VARCHAR(11) NULL DEFAULT NULL,
  `codigoaduana` VARCHAR(45) NULL DEFAULT NULL,
  `razonsocial` VARCHAR(45) NULL DEFAULT NULL,
  `codigojuridiccion` VARCHAR(45) NULL DEFAULT NULL,
  `idzonas` INT NOT NULL,
  PRIMARY KEY (`idusuarios`),
  INDEX `idrol_idx` (`idrol` ASC) VISIBLE,
  INDEX `iddistrito_idx` (`iddistrito` ASC) VISIBLE,
  INDEX `adminzonalid_idx` (`adminzonalid` ASC) VISIBLE,
  INDEX `fk_usuarios_zona1_idx` (`idzonas` ASC) VISIBLE,
  CONSTRAINT `adminzonalid`
    FOREIGN KEY (`adminzonalid`)
    REFERENCES `dogless`.`adminzonales` (`idadminzonales`),
  CONSTRAINT `distritoid`
    FOREIGN KEY (`iddistrito`)
    REFERENCES `dogless`.`distritos` (`iddistrito`),
  CONSTRAINT `fk_usuarios_zona1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`),
  CONSTRAINT `idrol`
    FOREIGN KEY (`idrol`)
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
  `idusuarios` VARCHAR(2) NOT NULL,
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
  `idusuarios` VARCHAR(2) NOT NULL,
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
    REFERENCES `dogless`.`zonas` (`idzonas`),
  CONSTRAINT `productoid`
    FOREIGN KEY (`idproductos`)
    REFERENCES `dogless`.`productos` (`idproductos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
