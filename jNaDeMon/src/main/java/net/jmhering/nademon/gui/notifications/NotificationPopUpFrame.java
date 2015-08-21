package net.jmhering.nademon.gui.notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by clupeidae on 29.07.15.
 */
public class NotificationPopUpFrame extends JDialog {
    protected GridBagLayout layout = new GridBagLayout();
    protected int WIDTH = 450;
    protected int HEIGHT = 200;

    NotificationPopUpFrame () {
        super();
        String message = "<b><font size=+1>O</font><u>K</u></b><br>SV002<br><br><b>CRIT</b><br>SV003";
        String header = "Nagios Desktop Monitor - Update Alert";
        setSize(WIDTH, HEIGHT);
        setTitle(header);
        setResizable(false);


        //setUndecorated(true);
        setAlwaysOnTop(true);
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();// size of the screen
        Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());// height of the task bar
        setLocation(scrSize.width - getWidth(), scrSize.height - toolHeight.bottom - getHeight());
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0f;
        constraints.weighty = 1.0f;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;
        JLabel messageLabel = new JLabel("<html>" + message);

        add(messageLabel, constraints);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(5000); // time after which pop up will be disappeared.
                    dispose();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
}
