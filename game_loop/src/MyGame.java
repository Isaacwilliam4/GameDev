import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;

public class MyGame {
    private HashMap<String, Event> events;
    private HashMap<String, Event> renderEvents;
    private long previousTime;
    private StringBuilder input;

    public void initialize() {
        System.out.println("GameLoop Demo Initializing...");
        previousTime = new Date().getTime();
        events = new HashMap<>();
        renderEvents = new HashMap<>();
        input = new StringBuilder();
    }

    private void printCommand(){
        System.out.print("[cmd:] " + input.toString());
    }

    public void run(){
        printCommand();
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
                render();
            }
            catch(Exception e){
                input = new StringBuilder();
                System.out.println("Invalid command");
                printCommand();
            }
        }
    }

    public void processInput(int availableInput) throws Exception {
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
                throw new Exception("Invalid command");
            }
        }
        catch(Exception e){
            throw new Exception(e.getMessage());
        }
        printCommand();
    }

    public void update(long elapsedTime){
        List<String> keysToRemove = new ArrayList<>();
        for(String key: events.keySet()){
            Event e = events.get(key);
            double newTime = e.currInterval - elapsedTime;
            if(newTime > 0){
                e.currInterval = newTime;
            }
            else{
                e.times--;
                renderEvents.put(key, e);
                e.currInterval = e.interval + newTime;
            }

            if (e.times <= 0){
                keysToRemove.add(key);
            }
        }
        for (String key: keysToRemove){
            events.remove(key);
        }
    }
    public void render(){
        for (String key: renderEvents.keySet()){
            Event e = renderEvents.get(key);
            System.out.printf("\n\tEvent: %s (%d Remaining)\n", e.name, e.times);
            printCommand();
        }
        renderEvents.clear();
    }
}
