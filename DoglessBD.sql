-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema dogless
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dogless` DEFAULT CHARACTER SET utf8mb3 ;
USE `dogless` ;

-- -----------------------------------------------------
-- Table `dogless`.`zona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`zona` (
  `idzona` INT NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`idzona`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `dogless`.`adminzonal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`adminzonal` (
  `idadminzonal` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `telefono` VARCHAR(9) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `contraseña` VARCHAR(45) NULL DEFAULT NULL,
  `zona_idzona` INT NOT NULL,
  PRIMARY KEY (`idadminzonal`),
  INDEX `fk_adminzonal_zona1_idx` (`zona_idzona` ASC) VISIBLE,
  CONSTRAINT `fk_adminzonal_zona1`
    FOREIGN KEY (`zona_idzona`)
    REFERENCES `dogless`.`zona` (`idzona`)
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


-- -----------------------------------------------------
-- Table `dogless`.`distritos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`distritos` (
  `iddistrito` INT NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `zona_idzona` INT NOT NULL,
  PRIMARY KEY (`iddistrito`),
  INDEX `fk_distritos_zona1_idx` (`zona_idzona` ASC) VISIBLE,
  CONSTRAINT `fk_distritos_zona1`
    FOREIGN KEY (`zona_idzona`)
    REFERENCES `dogless`.`zona` (`idzona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `dogless`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`usuarios` (
  `idusuarios` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `apellido` VARCHAR(45) NULL,
  `dni` VARCHAR(45) NULL,
  `correo` VARCHAR(45) NULL,
  `contraseña` VARCHAR(45) NULL,
  `telefono` VARCHAR(45) NULL,
  `direccion` VARCHAR(45) NULL,
  `idrol` INT NOT NULL,
  `distritoid` INT NOT NULL,
  `adminzonalid` INT NOT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL,
  `ruc` VARCHAR(11) NULL,
  `codigoaduana` VARCHAR(45) NULL,
  `razonsocial` VARCHAR(45) NULL,
  `codigojuridiccion` VARCHAR(45) NULL,
  `zona_idzona` INT NOT NULL,
  PRIMARY KEY (`idusuarios`),
  INDEX `idrol_idx` (`idrol` ASC) VISIBLE,
  INDEX `iddistrito_idx` (`distritoid` ASC) VISIBLE,
  INDEX `adminzonalid_idx` (`adminzonalid` ASC) VISIBLE,
  INDEX `fk_usuarios_zona1_idx` (`zona_idzona` ASC) VISIBLE,
  CONSTRAINT `idrol`
    FOREIGN KEY (`idrol`)
    REFERENCES `dogless`.`roles` (`idroles`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `distritoid`
    FOREIGN KEY (`distritoid`)
    REFERENCES `dogless`.`distritos` (`iddistrito`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `adminzonalid`
    FOREIGN KEY (`adminzonalid`)
    REFERENCES `dogless`.`adminzonal` (`idadminzonal`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuarios_zona1`
    FOREIGN KEY (`zona_idzona`)
    REFERENCES `dogless`.`zona` (`idzona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


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
  `usuariosid` INT NOT NULL,
  PRIMARY KEY (`idordenes`),
  INDEX `usuariosid_idx` (`usuariosid` ASC) VISIBLE,
  CONSTRAINT `usuariosid`
    FOREIGN KEY (`usuariosid`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


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
DEFAULT CHARACTER SET = utf8mb3;


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
  `proveedorid` INT NOT NULL,
  `modelos` VARCHAR(100) NULL DEFAULT NULL,
  `colores` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`idproductos`),
  INDEX `proveedor_id_idx_productos` (`proveedorid` ASC) VISIBLE,
  CONSTRAINT `fk_proveedor_id_productos`
    FOREIGN KEY (`proveedorid`)
    REFERENCES `dogless`.`proveedores` (`idproveedores`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


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
-- Table `dogless`.`resenas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`resenas` (
  `idresenas` INT NOT NULL AUTO_INCREMENT,
  `productoid` INT NOT NULL,
  `comentario` MEDIUMTEXT NULL DEFAULT NULL,
  `satisfaccion` INT NULL DEFAULT NULL,
  `fecha` DATE NULL DEFAULT NULL,
  `atencion` INT NULL DEFAULT NULL,
  `calidad` TINYINT NULL DEFAULT NULL,
  `serecibiorapido` INT NULL DEFAULT NULL,
  `usuarioid` INT NOT NULL,
  PRIMARY KEY (`idresenas`),
  INDEX `producto_id_idx_resenas` (`productoid` ASC) VISIBLE,
  INDEX `usuarioid_idx` (`usuarioid` ASC) VISIBLE,
  CONSTRAINT `fk_producto_id_resenas`
    FOREIGN KEY (`productoid`)
    REFERENCES `dogless`.`productos` (`idproductos`),
  CONSTRAINT `usuarioid`
    FOREIGN KEY (`usuarioid`)
    REFERENCES `dogless`.`usuarios` (`idusuarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Table `dogless`.`stockproductos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`stockproductos` (
  `productoid` INT NOT NULL,
  `cantidad` INT NULL,
  `zona_idzona` INT NOT NULL,
  INDEX `productoid_idx` (`productoid` ASC) VISIBLE,
  INDEX `fk_stockproductos_zona1_idx` (`zona_idzona` ASC) VISIBLE,
  CONSTRAINT `productoid`
    FOREIGN KEY (`productoid`)
    REFERENCES `dogless`.`productos` (`idproductos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_stockproductos_zona1`
    FOREIGN KEY (`zona_idzona`)
    REFERENCES `dogless`.`zona` (`idzona`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
