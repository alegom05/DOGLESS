-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema dogless
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dogless` DEFAULT CHARACTER SET utf8 ;
USE `dogless` ;

-- -----------------------------------------------------
-- Table `dogless`.`usuarioFinal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`usuarioFinal` (
  `idUsuarioFinal` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `apellido` VARCHAR(45) NULL,
  `dni` VARCHAR(8) NULL,
  `email` VARCHAR(45) NULL,
  `contrasena` LONGTEXT NULL,
  `direccion` VARCHAR(45) NULL,
  `distrito` VARCHAR(45) NULL,
  `telefono` VARCHAR(45) NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL,
  PRIMARY KEY (`idUsuarioFinal`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`superAdmin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`superAdmin` (
  `idsuperAdmin` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `apellido` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `contrasena` LONGTEXT NULL,
  PRIMARY KEY (`idsuperAdmin`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`zonas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`zonas` (
  `idzonas` INT NOT NULL AUTO_INCREMENT,
  `nombre_zona` VARCHAR(45) NULL,
  PRIMARY KEY (`idzonas`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`adminZonal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`adminZonal` (
  `idadminZonal` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `apellido` VARCHAR(45) NULL,
  `dni` VARCHAR(8) NULL,
  `email` VARCHAR(45) NULL,
  `contrasena` LONGTEXT NULL,
  `zona_id` INT NULL,
  `telefono` VARCHAR(9) NULL,
  PRIMARY KEY (`idadminZonal`),
  INDEX `zona_id_idx_adminZonal` (`zona_id` ASC),
  CONSTRAINT `fk_zona_id_adminzonal`
    FOREIGN KEY (`zona_id`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`agente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`agente` (
  `idagente` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `apellido` VARCHAR(45) NULL,
  `dni` VARCHAR(8) NULL,
  `email` VARCHAR(45) NULL,
  `contrasena` LONGTEXT NULL,
  `zona_id` INT NULL,
  `telefono` VARCHAR(9) NULL,
  `codigo_aduana` VARCHAR(4) NULL,
  `ruc` VARCHAR(11) NULL,
  `razon_social` VARCHAR(45) NULL,
  `direccion` VARCHAR(45) NULL,
  `distrito` VARCHAR(45) NULL,
  `codigo_juridisccion` VARCHAR(3) NULL,
  PRIMARY KEY (`idagente`),
  INDEX `zona_id_idx_agente` (`zona_id` ASC),
  CONSTRAINT `fk_zona_id_agente`
    FOREIGN KEY (`zona_id`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`proveedores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`proveedores` (
  `idproveedores` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `telefono_contacto` VARCHAR(45) NULL,
  `ruc` VARCHAR(11) NULL,
  `dni` VARCHAR(8) NULL,
  `nombre_tienda` VARCHAR(45) NULL,
  `estado` ENUM('activo', 'inactivo', 'baneado') NULL,
  PRIMARY KEY (`idproveedores`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`productos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`productos` (
  `idproductos` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  `descripcion` VARCHAR(45) NULL,
  `categoria` VARCHAR(45) NULL,
  `precio` DECIMAL(10,2) NULL,
  `costo_envio` DECIMAL(10,2) NULL,
  `cantidad_disponible` INT NULL,
  `zona_id` INT NULL,
  `proveedor_id` INT NULL,
  `modelos` VARCHAR(100) NULL,
  `colores` VARCHAR(100) NULL,
  PRIMARY KEY (`idproductos`),
  INDEX `zonas_id_idx_productos` (`zona_id` ASC),
  INDEX `proveedor_id_idx_productos` (`proveedor_id` ASC),
  CONSTRAINT `fk_zonas_id_productos`
    FOREIGN KEY (`zona_id`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_proveedor_id_productos`
    FOREIGN KEY (`proveedor_id`)
    REFERENCES `dogless`.`proveedores` (`idproveedores`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`ordenes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`ordenes` (
  `idordenes` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NULL,
  `estado` ENUM('Creado', 'En Validación', 'En Proceso', 'Arribo al País', 'En Aduanas', 'En Ruta', 'Recibido') NULL,
  `fecha_creacion` DATE NULL,
  `direccion_envio` VARCHAR(100) NULL,
  `total` DECIMAL(10,2) NULL,
  `metodo_pago` ENUM('tarjeta') NULL,
  `agente_id` INT NULL,
  PRIMARY KEY (`idordenes`),
  INDEX `usuario_id_idx_ordenes` (`usuario_id` ASC),
  INDEX `agente_id_idx_ordenes` (`agente_id` ASC),
  CONSTRAINT `fk_usuario_id_ordenes`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `dogless`.`usuarioFinal` (`idUsuarioFinal`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_agente_id_ordenes`
    FOREIGN KEY (`agente_id`)
    REFERENCES `dogless`.`agente` (`idagente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`detallesOrden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`detallesOrden` (
  `iddetallesOrden` INT NOT NULL AUTO_INCREMENT,
  `id_orden` INT NULL,
  `id_producto` INT NULL,
  `cantidad` INT NULL,
  `precio_unitario` DECIMAL(10,2) NULL,
  `subtotal` DECIMAL(10,2) NULL,
  PRIMARY KEY (`iddetallesOrden`),
  INDEX `id_orden_idx_detallesOrden` (`id_orden` ASC),
  INDEX `id_producto_idx_detallesOrden` (`id_producto` ASC),
  CONSTRAINT `fk_id_orden_detallesOrden`
    FOREIGN KEY (`id_orden`)
    REFERENCES `dogless`.`ordenes` (`idordenes`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_id_producto_detallesOrden`
    FOREIGN KEY (`id_producto`)
    REFERENCES `dogless`.`productos` (`idproductos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`resenas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`resenas` (
  `idresenas` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NULL,
  `producto_id` INT NULL,
  `comentario` MEDIUMTEXT NULL,
  `satisfaccion` INT NULL,
  `fecha_resena` DATE NULL,
  `atencion_agente` INT NULL,
  `buena_calidad` TINYINT NULL,
  `se_recibio_rapido` INT NULL,
  PRIMARY KEY (`idresenas`),
  INDEX `usuario_id_idx_resenas` (`usuario_id` ASC),
  INDEX `producto_id_idx_resenas` (`producto_id` ASC),
  CONSTRAINT `fk_usuario_id_resenas`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `dogless`.`usuarioFinal` (`idUsuarioFinal`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_producto_id_resenas`
    FOREIGN KEY (`producto_id`)
    REFERENCES `dogless`.`productos` (`idproductos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `dogless`.`distritos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`distritos` (
  `iddistrito` INT NOT NULL AUTO_INCREMENT,
  `id_zona` INT NULL,
  `nombre` VARCHAR(45) NULL,
  PRIMARY KEY (`iddistrito`),
  INDEX `id_zonas_idx_distritos` (`id_zona` ASC),
  CONSTRAINT `fk_id_zona_distritos`
    FOREIGN KEY (`id_zona`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
