package ch.heigvd;

import picocli.CommandLine;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "Inko", mixinStandardHelpOptions = true, version = "1.0", description = Description.DESCRIPTION)

public class InkOverlay implements Callable {
    @Override
    public Integer call() throws Exception {
        return null;
    }


    /**
     * program input on the command
     * @param args arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new InkOverlay()).execute(args);
        System.exit(exitCode);
    }

}
