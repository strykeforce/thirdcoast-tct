Stryke Force team 2767's Third Coast Telemetry command-line utility. We find it useful for tuning motor closed-loop performance and manually controlling robot axes during development.

Devices that are activated in `tct` are automatically configured for graphing in our [Grapher](https://github.com/strykeforce/grapher).

[![asciicast](https://asciinema.org/a/owPEXZIDx8HoUzkF59rE8zvIe.svg)](https://asciinema.org/a/owPEXZIDx8HoUzkF59rE8zvIe)

## Installation

1.  Download the latest version of the installer from [releases](https://github.com/strykeforce/thirdcoast-tct/releases). It will be named `tct-installer-VERSION.bsx`, where `VERSION` is replaced with the current version number.
2.  Upload the installer to your roboRIO. The directory you upload to doesn't matter but you should make note of it so you can find it again on the roboRIO. The **admin** user home directory (`/home/admin`) is a good choice.
3. Note that all commands that you type at the roboRIO command-line in the following steps are **case-sensitive**.
4.  SSH into your roboRIO, locate the installer you just uploaded and run: `sh tct-installer-VERSION.bsx` to install TCT. This will extract and install a JAR file in `/usr/local/lib/tct.jar` and an executable script in `/usr/local/bin/tct`.
5.  You are done with the installer and may remove it: `rm tct-installer-VERSION.bsx`.
6.  TCT is now in the roboRIO path and can be run by typing `tct` at the roboRIO command line. You must have a Driver Station connected and robot enabled for motors to run. Also, we recommend you don't run your `tct` ssh program (i.e. PuTTY) on the same laptop as the Driver Station since typing can cause the robot to disable or e-stop.

See other engineering resources at [strykeforce.org](https://strykeforce.org/resources/).
