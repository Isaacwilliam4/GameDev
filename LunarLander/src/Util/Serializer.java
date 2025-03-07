package Util;

import Enums.GameState;
import Models.HighScores;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Serializer implements Runnable {
    private enum Activity {
        Nothing,
        Load,
        Save
    }

    private boolean done = false;
    private final Lock lockSignal = new ReentrantLock();
    private final Condition doSomething = lockSignal.newCondition();
    private Activity doThis = Activity.Nothing;

    private HighScores highScores;

    private final Thread tInternal;

    public Serializer() {
        this.tInternal = new Thread(this);
        this.tInternal.start();
    }

    @Override
    public void run() {
        try {
            while (!done) {
                // Wait for a signal to do something
                lockSignal.lock();
                doSomething.await();
                lockSignal.unlock();

                // Based on what was requested, do something
                switch (doThis) {
                    case Activity.Nothing -> {}
                    case Activity.Save -> saveSomething();
                    case Activity.Load -> loadSomething();
                }
            }
        } catch (Exception ex) {
            System.out.printf("Something bad happened: %s\n", ex.getMessage());
        }
    }

    /// Public method used by client code to request the game state is saved
    /// NOTE: This does not prevent against race conditions if the gameState object
    ///       is modified while the saving is taking place.  A production level
    ///       approach would have an event held by the client signaled when
    ///       the saving is complete.
    public void saveHighScores(HighScores highscores) {
        lockSignal.lock();

        this.highScores = highscores;
        doThis = Activity.Save;
        doSomething.signal();

        lockSignal.unlock();
    }

    /// Public method used the client code to request the game state is loaded.
    /// NOTE: Same comment about race conditions as above.
    public void loadHighScores(HighScores highscores) {
        lockSignal.lock();

        this.highScores = highscores;
        doThis = Activity.Load;
        doSomething.signal();

        lockSignal.unlock();
    }

    /// Public method used to signal this code to perform a graceful shutdown
    public void shutdown() {
        try {
            lockSignal.lock();

            doThis = Activity.Nothing;
            done = true;
            doSomething.signal();

            lockSignal.unlock();

            tInternal.join();
        } catch (Exception ex) {
            System.out.printf("Failure to gracefully shutdown thread: %s\n", ex.getMessage());
        }
    }

    /// This is where the actual serialization of the game state is performed.  Have
    /// chosen to save in JSON format for readability for the demo, but the state
    /// could have been stored using a binary serializer for more efficient storage
    private synchronized void saveSomething() {
        System.out.println("saving something...");
        var highscoreSet = new HashSet<>(highScores.getHighScores());
        this.highScores.setHighScores(new ArrayList<>(highscoreSet));
        this.highScores.getHighScores().sort(Collections.reverseOrder());
        this.highScores.setHighScores(highScores.getHighScores().subList(0, Math.min(5, this.highScores.getHighScores().size())));

        try (FileWriter writer = new FileWriter("highscores.json")) {
            Gson gson = new Gson();
            gson.toJson(this.highScores, writer);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /// This is where the actual deserialization of the game state is performed.
    /// Same note as above regarding the choice to use JSON formatting.
    private synchronized void loadSomething() {
        System.out.println("loading something...");
        File file = new File("highscores.json");
        if (!file.exists()) {
            return;
        }
        try (FileReader reader = new FileReader("highscores.json")) {
            HighScores state = (new Gson()).fromJson(reader, HighScores.class);
            this.highScores.setHighScores(state.getHighScores());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}