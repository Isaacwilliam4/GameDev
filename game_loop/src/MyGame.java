import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class MyGame {
    private HashMap<String, Event> events;
    private long previousTime;
    private StringBuilder input;

    public void initialize() {
        System.out.println("GameLoop Demo Initializing...");
        previousTime = new Date().getTime();
        events = new HashMap<>();
        input = new StringBuilder();
    }

    public void run() throws IOException {
        System.out.print("[cmd:] ");
        while (true) {
            try{
                int availableInput = System.in.available();
                if (availableInput > 0){
                    processInput(availableInput);
                }

                long currentTime = new Date().getTime();
                long elapsedTime = currentTime - previousTime;
                previousTime = currentTime;
                update(elapsedTime);
            }
            catch(IOException e){
                throw new IOException(e.getMessage());
            }
        }
    }

    public void processInput(int availableInput){
        try{
            for (int i = 0; i < availableInput; i++){
                char nextInput = (char) System.in.read();
                input.append(nextInput);
            }
            char lastChar = input.charAt(input.length()-1);

            if(lastChar != '\n'){
                return;
            }

            String cleanString = input.toString().replaceAll("\n","");
            input = new StringBuilder();
            String[] args = cleanString.split(" ");

            if (args[0].equals("quit")){
                System.exit(0);
            }

            if (args[0].equals("create") && args[1].equals("event")){
                String name = args[2];
                double interval = Double.parseDouble(args[3]);
                int times = Integer.parseInt(args[4]);
                Event event = new Event(name, interval, times);
                events.put(name, event);
            }
            else{
                System.out.println("Invalid command");
            }
        }
        catch(Exception e){
            System.out.println("Invalid command");
        }
        System.out.print("[cmd:] ");
    }

    public void update(long elapsedTime){
        for(String key: events.keySet()){
            Event e = events.get(key);
            double newTime = e.currInterval - elapsedTime;
            if(newTime > 0){
                e.currInterval = newTime;
            }
            else{
                e.times--;
                render(e);
                e.currInterval = e.interval + newTime;
            }

            if (e.times <= 0){
                events.remove(key);
            }
        }
    }
    public void render(Event e){
        System.out.printf("\n\tEvent: %s (%d Remaining)\n", e.name, e.times);
        System.out.print("[cmd:] ");
    }
}
