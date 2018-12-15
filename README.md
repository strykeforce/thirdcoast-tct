Stryke Force team 2767's Third Coast Telemetry command-line utility. We find it useful for tuning motor closed-loop performance and manually controlling robot axes during development.

Devices that are activated in `tct` are automatically configured for graphing in our [Grapher](https://github.com/strykeforce/grapher).

[![asciicast](https://asciinema.org/a/owPEXZIDx8HoUzkF59rE8zvIe.svg)](https://asciinema.org/a/owPEXZIDx8HoUzkF59rE8zvIe)

## Installation

1.  Download the latest version of the installer from [releases](releases/). It will be named `tct-installer-VERSION.bsx`.
2.  Upload the installer to your roboRIO. The directory you upload to doesn't matter but you should make note of it so you can find it again on the roboRIO. The **admin** user home directory (`/home/admin`) is a good choice.
3.  SSH into your roboRIO, locate the installer you just uploaded and run: `sh tct-installer-VERSION.bsx` to install TCT. This will extract and install a JAR file in `/usr/local/lib/tct.jar` and an executable script in `/usr/local/bin/tct`.
4.  You are done with the installer and may remove it: `rm tct-installer-VERSION.bsx`.
5.  Run TCT by typing `tct` at the roboRIO command line.
