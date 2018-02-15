# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Calendar Versioning](http://calver.org/).

## 18.1.3 - 2018-02-12

### Changed

- Combined inspect and list commands.

### Fixed

- Fix Velocity and MotionMagic control-mode bugs.


## 18.0.5 - 2018-01-24

### Changed

- Updated TalonSRX status frame rates for 2018.

## [18.0.4] - 2018-01-20

This version upgrades tct to be compatible with the 2018 Phoenix TalonSRX and WPI control system releases.

## 17.1.3 - 2017-12-02

### Added

- You can now use the `B` key in the `tct` utility to go back while running Talons, Servos or Digital Outputs.

### Changed

- Linefeeds added to `tct` utility menus to improve readability.

## 17.1.2 - 2017-11-27

### Fixed

- `tct` utility was not saving configuration for settings that had forwardLimits and reverseLimits double values, for example **peak voltage**.

## 17.1.1 - 2017-11-27

### Added

- `tct` utility added **brake in neutral** configuration.

### Changed

- `tct` utility allows digital inputs to be reconfigured as outputs.
- `tct` utility logging level increased to warn.

### Fixed

- **voltage ramp rate** was not configuring a new `TalonConfigurationBuilder` from an existing `TalonConfiguration`.

## 17.1.0 - 2017-11-16

### Added

- Released `tct` utility for Talons, digital outputs and servos.


[18.1.3]: https://github.com/strykeforce/thirdcoast-tct/compare/v18.0.5...v18.1.3

[unreleased]: https://github.com/strykeforce/thirdcoast/compare/v18.1.3...develop
