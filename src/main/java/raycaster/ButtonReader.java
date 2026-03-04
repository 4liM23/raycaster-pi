package raycaster;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
// import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

public class ButtonReader {
    static int A_BUTTON_GPIO = 5;
    static int B_BUTTON_GPIO = 6;
    static int X_BUTTON_GPIO = 16;
    static int Y_BUTTON_GPIO = 24;
    Context pi4j;
    DigitalInput btnA, btnB, btnX, btnY;
    boolean downA, downB, downX, downY;

    public ButtonReader() {
        pi4j = Pi4J.newAutoContext();
        DigitalInputConfigBuilder cfg;
        cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("btnA")
                .address(A_BUTTON_GPIO) // BCM GPIO number
                .pull(PullResistance.PULL_UP);
        btnA = pi4j.create(cfg);

        cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("btnB")
                .address(B_BUTTON_GPIO) // BCM GPIO number
                .pull(PullResistance.PULL_UP);
        btnB = pi4j.create(cfg);

        cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("btnX")
                .address(X_BUTTON_GPIO) // BCM GPIO number
                .pull(PullResistance.PULL_UP);
        btnX = pi4j.create(cfg);

        cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("btnY")
                .address(Y_BUTTON_GPIO) // BCM GPIO number
                .pull(PullResistance.PULL_UP);
        btnY = pi4j.create(cfg);
    }

    public boolean A_Button_Pressed() {
        return btnA.isLow();
    }

    public boolean B_Button_Pressed() {
        return btnB.isLow();
    }

    public boolean X_Button_Pressed() {
        return btnX.isLow();
    }

    public boolean Y_Button_Pressed() {
        return btnY.isLow();
    }

}