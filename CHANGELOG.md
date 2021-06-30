# Change Log
## [1.1.1] - 2021-06-30
### Fixed
- Saving classes that were skipped from transformation into the final runtime classpath.
## [1.1.0] - 2021-06-28
### Added
- Changelog file
- Configuration params to transform dependencies
### Fixed
- [4](https://github.com/LikeTheSalad/android-buddy/issues/4) -
  Disabling Byte Buddy type validation as suggested [here](https://github.com/raphw/byte-buddy/issues/1074) due to how
  the Android build works.