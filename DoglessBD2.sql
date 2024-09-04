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
-- Table `dogless`.`superadmin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`superadmin` (
  `idsuperAdmin` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `correo` VARCHAR(45) NULL DEFAULT NULL,
  `clave` LONGTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`idsuperAdmin`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`zona`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`zona` (
  `idzona` VARCHAR(2) NOT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `superadmin_idsuperAdmin` INT NOT NULL,
  PRIMARY KEY (`idzona`),
  INDEX `fk_zona_superadmin1_idx` (`superadmin_idsuperAdmin` ASC) VISIBLE,
  CONSTRAINT `fk_zona_superadmin1`
    FOREIGN KEY (`superadmin_idsuperAdmin`)
    REFERENCES `dogless`.`superadmin` (`idsuperAdmin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


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
  `idzona` VARCHAR(2) NULL DEFAULT NULL,
  `clave` LONGTEXT NULL DEFAULT NULL,
  `clavetemp` VARCHAR(45) NULL,
  `fechanacimiento` VARCHAR(45) NULL,
  PRIMARY KEY (`idadminzonal`),
  INDEX `zona_id_idx_adminZonal` (`idzona` ASC) VISIBLE,
  CONSTRAINT `fk_zona_id_adminzonal`
    FOREIGN KEY (`idzona`)
    REFERENCES `dogless`.`zona` (`idzona`))
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
  `telefono` VARCHAR(9) NULL DEFAULT NULL,
  `correo` VARCHAR(45) NULL DEFAULT NULL,
  `clave` LONGTEXT NULL DEFAULT NULL,
  `codigodespachador` VARCHAR(4) NULL DEFAULT NULL,
  `ruc` VARCHAR(11) NULL DEFAULT NULL,
  `razonsocial` VARCHAR(45) NULL DEFAULT NULL,
  `direccion` VARCHAR(45) NULL DEFAULT NULL,
  `distrito` VARCHAR(45) NULL DEFAULT NULL,
  `codigojuridisccion` VARCHAR(3) NULL DEFAULT NULL,
  `idadminzonal` INT NOT NULL,
  PRIMARY KEY (`idagente`),
  INDEX `fk_agente_adminzonal1_idx` (`idadminzonal` ASC) VISIBLE,
  CONSTRAINT `fk_agente_adminzonal1`
    FOREIGN KEY (`idadminzonal`)
    REFERENCES `dogless`.`adminzonal` (`idadminzonal`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`distritos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`distritos` (
  `iddistrito` VARCHAR(2) NOT NULL,
  `idzona` INT NULL DEFAULT NULL,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`iddistrito`),
  INDEX `id_zonas_idx_distritos` (`idzona` ASC) VISIBLE,
  CONSTRAINT `fk_id_zona_distritos`
    FOREIGN KEY (`idzona`)
    REFERENCES `dogless`.`zona` (`idzona`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`usuario` (
  `idusuario` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  `clave` LONGTEXT NULL DEFAULT NULL,
  `direccion` VARCHAR(45) NULL DEFAULT NULL,
  `distrito` VARCHAR(45) NULL DEFAULT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL DEFAULT NULL,
  `iddistrito` VARCHAR(2) NOT NULL,
  PRIMARY KEY (`idusuario`),
  INDEX `fk_usuario_distritos1_idx` (`iddistrito` ASC) VISIBLE,
  CONSTRAINT `fk_usuario_distritos1`
    FOREIGN KEY (`iddistrito`)
    REFERENCES `dogless`.`distritos` (`iddistrito`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`ordenes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`ordenes` (
  `idordenes` INT NOT NULL AUTO_INCREMENT,
  `usuarioid` INT NULL DEFAULT NULL,
  `estado` ENUM('Creado', 'En Validación', 'En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta', 'Recibido') NULL DEFAULT NULL,
  `fecha` DATE NULL DEFAULT NULL,
  `direccionenvio` VARCHAR(100) NULL DEFAULT NULL,
  `total` DECIMAL(10,2) NULL DEFAULT NULL,
  `metodopago` ENUM('tarjeta') NULL DEFAULT NULL,
  `agenteid` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idordenes`),
  INDEX `usuario_id_idx_ordenes` (`usuarioid` ASC) VISIBLE,
  INDEX `agente_id_idx_ordenes` (`agenteid` ASC) VISIBLE,
  CONSTRAINT `fk_agente_id_ordenes`
    FOREIGN KEY (`agenteid`)
    REFERENCES `dogless`.`agente` (`idagente`),
  CONSTRAINT `fk_usuario_id_ordenes`
    FOREIGN KEY (`usuarioid`)
    REFERENCES `dogless`.`usuario` (`idusuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`proveedores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`proveedores` (
  `idproveedores` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL DEFAULT NULL,
  `apellido` VARCHAR(45) NULL,
  `telefono` VARCHAR(45) NULL DEFAULT NULL,
  `ruc` VARCHAR(11) NULL DEFAULT NULL,
  `dni` VARCHAR(8) NULL DEFAULT NULL,
  `tienda` VARCHAR(45) NULL DEFAULT NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL DEFAULT NULL,
  `idproveedores` INT NOT NULL,
  PRIMARY KEY (`idproveedores`),
  INDEX `fk_proveedores_proveedores1_idx` (`idproveedores` ASC) VISIBLE,
  CONSTRAINT `fk_proveedores_proveedores1`
    FOREIGN KEY (`idproveedores`)
    REFERENCES `dogless`.`proveedores` (`idproveedores`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
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
  `cantidaddisponible` INT NULL DEFAULT NULL,
  `idzona` INT NULL DEFAULT NULL,
  `proveedorid` INT NULL DEFAULT NULL,
  `modelos` VARCHAR(100) NULL DEFAULT NULL,
  `colores` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`idproductos`),
  INDEX `zonas_id_idx_productos` (`idzona` ASC) VISIBLE,
  INDEX `proveedor_id_idx_productos` (`proveedorid` ASC) VISIBLE,
  CONSTRAINT `fk_proveedor_id_productos`
    FOREIGN KEY (`proveedorid`)
    REFERENCES `dogless`.`proveedores` (`idproveedores`),
  CONSTRAINT `fk_zonas_id_productos`
    FOREIGN KEY (`idzona`)
    REFERENCES `dogless`.`zona` (`idzona`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `dogless`.`detallesorden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`detallesorden` (
  `iddetallesorden` INT NOT NULL AUTO_INCREMENT,
  `idorden` INT NULL DEFAULT NULL,
  `idproducto` INT NULL DEFAULT NULL,
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
  `usuarioid` INT NULL DEFAULT NULL,
  `productoid` INT NULL DEFAULT NULL,
  `comentario` MEDIUMTEXT NULL DEFAULT NULL,
  `satisfaccion` INT NULL DEFAULT NULL,
  `fecha` DATE NULL DEFAULT NULL,
  `atencion` INT NULL DEFAULT NULL,
  `calidad` TINYINT NULL DEFAULT NULL,
  `serecibiorapido` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idresenas`),
  INDEX `usuario_id_idx_resenas` (`usuarioid` ASC) VISIBLE,
  INDEX `producto_id_idx_resenas` (`productoid` ASC) VISIBLE,
  CONSTRAINT `fk_producto_id_resenas`
    FOREIGN KEY (`productoid`)
    REFERENCES `dogless`.`productos` (`idproductos`),
  CONSTRAINT `fk_usuario_id_resenas`
    FOREIGN KEY (`usuarioid`)
    REFERENCES `dogless`.`usuario` (`idusuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
