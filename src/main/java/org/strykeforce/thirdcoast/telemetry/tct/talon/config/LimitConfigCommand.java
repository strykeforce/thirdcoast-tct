package org.strykeforce.thirdcoast.telemetry.tct.talon.config;

import javax.inject.Inject;
import javax.inject.Provider;
import org.jline.terminal.Terminal;
import org.strykeforce.thirdcoast.telemetry.tct.AbstractCommand;
import org.strykeforce.thirdcoast.telemetry.tct.Menu;
import org.strykeforce.thirdcoast.telemetry.tct.talon.TalonSet;
import org.strykeforce.thirdcoast.telemetry.tct.talon.config.limit.LimitMenuComponent;

/**
 * Configure selected Talons.
 */
@TalonConfigScope
public class LimitConfigCommand extends AbstractCommand {

  public final static String NAME = "Limit Switches";
  private final Provider<LimitMenuComponent.Builder> voltageMenuComponentProvider;
  private final TalonSet talonSet;

  @Inject
  public LimitConfigCommand(TalonSet talonSet,
      Provider<LimitMenuComponent.Builder> voltageMenuComponentProvider, Terminal terminal) {
    super(NAME, ConfigMenuModule.MENU_ORDER.indexOf(NAME), terminal);
    this.voltageMenuComponentProvider = voltageMenuComponentProvider;
    this.talonSet = talonSet;
  }

  @Override
  public void perform() {
    if (talonSet.selected().isEmpty()) {
      terminal.writer().println(bold("no talons selected"));
      return;
    }

    LimitMenuComponent component = voltageMenuComponentProvider.get().build();
    Menu menu = component.menu();
    menu.display();
  }
}
