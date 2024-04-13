package GroupId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PowerShellJavaInteraction {

    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> signalStrengths = new ArrayList<>();

        String command = "netsh wlan show interfaces"; // Default command
        final String command1 = command;

        if (args.length > 0) {
            command = args[0]; // Use command from argument if provided
        }

        System.out.println("Press Enter to start recording signal strengths (press 'q' to quit):");
        reader.readLine().trim();  // Wait for initial Enter press

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable commandRunner = new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder("powershell", "-Command", command1);

                    Process process = processBuilder.start();
                    StringBuilder outputBuilder = new StringBuilder(); // Store output temporarily

                    try (BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = processReader.readLine()) != null) {
                            outputBuilder.append(line).append("\n"); // Append each line with newline
                        }
                    }

                    process.waitFor();

                    String output = outputBuilder.toString();
                    String signalStrength = extractSignalStrength(output);

                    if (signalStrength != null) {
                        signalStrengths.add(signalStrength);
                        System.out.println("Signal strength: " + signalStrength);
                    } else {
                        System.out.println("Signal strength not found in the output.");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();  // Handle potential exceptions
                }
            }
        };

        scheduler.scheduleAtFixedRate(commandRunner, 0, 2, TimeUnit.SECONDS);  // Run every 2 seconds

        while (true) {
            String userInput = reader.readLine().trim();
            if (userInput.equalsIgnoreCase("q")) {
                scheduler.shutdown();  // Stop the scheduled task
                break;
            }
        }

        System.out.println("Collected signal strengths:");
        for (String strength : signalStrengths) {
            System.out.println(strength);
        }
    }

    // Method to extract signal strength from PowerShell output (using regex)
    private static String extractSignalStrength(String output) {
        String pattern = "Signal\\s*:\\s*(\\d+%)"; // Regex for "Signal: xx%" format
        Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(output);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}