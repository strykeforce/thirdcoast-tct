package org.strykeforce.thirdcoast

import org.jline.terminal.Terminal

fun Terminal.info(msg: String) = this.writer().println(msg)

fun Terminal.warn(msg: String) = this.writer().println(msg)