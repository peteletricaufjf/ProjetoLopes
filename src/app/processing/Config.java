package app.processing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe helper para pegar dados salvos no 'sensors.properties'
 */
public abstract class Config {
    private static final Properties prop = new Properties();

    private static String getProp(String property) {
        InputStream input = null;

        try {
            input = new FileInputStream("sensors.properties");
            prop.load(input);
            return prop.getProperty(property);
        } catch (IOException io){
            io.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public static int getDigitalInput() {
        return Integer.parseInt(getProp("digital_input"));
    }

    public static int getAnalogInput() {
        return Integer.parseInt(getProp("analog_input"));
    }

    public static int getDigitalOutput() {
        return Integer.parseInt(getProp("digital_output"));
    }

}
