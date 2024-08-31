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
-- Table `dogless`.`superadmin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`superadmin` (
  `idsuperadmin` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `correo` VARCHAR(45) NULL,
  PRIMARY KEY (`idsuperadmin`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`coordinadores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`coordinadores` (
  `idcoordinadores` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `dni` VARCHAR(45) NULL,
  `telefono` VARCHAR(45) NULL,
  `correo` VARCHAR(45) NULL,
  `zona` VARCHAR(45) NULL,
  `fechanacimiento` VARCHAR(45) NULL,
  `contrasenatemporal` VARCHAR(45) NULL,
  `superadmin_idsuperadmin` INT NOT NULL,
  PRIMARY KEY (`idcoordinadores`),
  INDEX `fk_coordinadores_superadmin1_idx` (`superadmin_idsuperadmin` ASC) VISIBLE,
  CONSTRAINT `fk_coordinadores_superadmin1`
    FOREIGN KEY (`superadmin_idsuperadmin`)
    REFERENCES `dogless`.`superadmin` (`idsuperadmin`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`zonas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`zonas` (
  `idzonas` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `coordinadores_idcoordinadores` INT NOT NULL,
  PRIMARY KEY (`idzonas`),
  INDEX `fk_zonas_coordinadores1_idx` (`coordinadores_idcoordinadores` ASC) VISIBLE,
  CONSTRAINT `fk_zonas_coordinadores1`
    FOREIGN KEY (`coordinadores_idcoordinadores`)
    REFERENCES `dogless`.`coordinadores` (`idcoordinadores`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`agentes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`agentes` (
  `idagentes` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `coordinadores_idcoordinadores` INT NOT NULL,
  PRIMARY KEY (`idagentes`),
  INDEX `fk_agentes_coordinadores1_idx` (`coordinadores_idcoordinadores` ASC) VISIBLE,
  CONSTRAINT `fk_agentes_coordinadores1`
    FOREIGN KEY (`coordinadores_idcoordinadores`)
    REFERENCES `dogless`.`coordinadores` (`idcoordinadores`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`usuario` (
  `idusuario` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `dni` VARCHAR(45) NULL,
  `correo` VARCHAR(45) NULL,
  `idroles` INT NOT NULL,
  `correo` VARCHAR(45) NULL,
  `direccion` VARCHAR(45) NULL,
  `distrito` VARCHAR(45) NULL,
  `contraseña` VARCHAR(45) NULL,
  `estado` VARCHAR(45) NULL,
  `telefono` VARCHAR(45) NULL,
  `foto` VARCHAR(45) NULL,
  `idzonas` INT NOT NULL,
  `idagentes` INT NOT NULL,
  PRIMARY KEY (`idusuario`),
  UNIQUE INDEX `idusuario_UNIQUE` (`idusuario` ASC) VISIBLE,
  INDEX `fk_usuario_zonas1_idx` (`idzonas` ASC) VISIBLE,
  INDEX `fk_usuario_agentes1_idx` (`idagentes` ASC) VISIBLE,
  CONSTRAINT `fk_usuario_zonas1`
    FOREIGN KEY (`idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuario_agentes1`
    FOREIGN KEY (`idagentes`)
    REFERENCES `dogless`.`agentes` (`idagentes`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`proveedor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`proveedor` (
  `idproveedor` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `pais` VARCHAR(45) NULL,
  `correo` VARCHAR(45) NULL,
  `tienda` VARCHAR(45) NULL,
  `dni` INT NULL,
  `ruc` VARCHAR(45) NULL,
  PRIMARY KEY (`idproveedor`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`producto` (
  `idproducto` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `categoria` VARCHAR(45) NULL,
  `descripcion` VARCHAR(45) NULL,
  `precio` DOUBLE NULL,
  `costoenvio` DOUBLE NULL,
  `nombreproveedor` VARCHAR(45) NULL,
  `cantidaddisponible` VARCHAR(45) NULL,
  `productocol` VARCHAR(45) NULL,
  `color` VARCHAR(45) NULL,
  `modelo` VARCHAR(45) NULL,
  `idproveedor` INT NOT NULL,
  `idusuario` INT NOT NULL,
  PRIMARY KEY (`idproducto`),
  INDEX `fk_producto_proveedor1_idx` (`idproveedor` ASC) VISIBLE,
  INDEX `fk_producto_usuario1_idx` (`idusuario` ASC) VISIBLE,
  CONSTRAINT `fk_producto_proveedor1`
    FOREIGN KEY (`idproveedor`)
    REFERENCES `dogless`.`proveedor` (`idproveedor`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_producto_usuario1`
    FOREIGN KEY (`idusuario`)
    REFERENCES `dogless`.`usuario` (`idusuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`zonas_has_producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`zonas_has_producto` (
  `zonas_idzonas` INT NOT NULL,
  `producto_idproducto` INT NOT NULL,
  PRIMARY KEY (`zonas_idzonas`, `producto_idproducto`),
  INDEX `fk_zonas_has_producto_producto1_idx` (`producto_idproducto` ASC) VISIBLE,
  INDEX `fk_zonas_has_producto_zonas1_idx` (`zonas_idzonas` ASC) VISIBLE,
  CONSTRAINT `fk_zonas_has_producto_zonas1`
    FOREIGN KEY (`zonas_idzonas`)
    REFERENCES `dogless`.`zonas` (`idzonas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_zonas_has_producto_producto1`
    FOREIGN KEY (`producto_idproducto`)
    REFERENCES `dogless`.`producto` (`idproducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`ordenes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`ordenes` (
  `idordenes` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `idusuario` INT NOT NULL,
  `estado` VARCHAR(45) NULL,
  `fechacreacion` DATETIME NULL,
  `costo` DOUBLE NULL,
  `direccion` VARCHAR(45) NULL,
  PRIMARY KEY (`idordenes`),
  INDEX `fk_ordenes_usuario1_idx` (`idusuario` ASC) VISIBLE,
  CONSTRAINT `fk_ordenes_usuario1`
    FOREIGN KEY (`idusuario`)
    REFERENCES `dogless`.`usuario` (`idusuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dogless`.`resenas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dogless`.`resenas` (
  `idresenas` INT NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `satisfaccion` VARCHAR(45) NULL,
  `idproducto` INT NOT NULL,
  `foto` VARCHAR(45) NULL,
  `calidad` VARCHAR(45) NULL,
  `comentario` VARCHAR(500) NULL,
  `foto` BLOB NULL,
  `resenascol` VARCHAR(45) NULL,
  PRIMARY KEY (`idresenas`, `idproducto`),
  INDEX `fk_resenas_producto1_idx` (`idproducto` ASC) VISIBLE,
  CONSTRAINT `fk_resenas_producto1`
    FOREIGN KEY (`idproducto`)
    REFERENCES `dogless`.`producto` (`idproducto`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
