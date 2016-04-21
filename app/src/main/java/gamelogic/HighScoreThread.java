package gamelogic;

import storage.shapes.DatabaseOperations;

/**
 * Created by eharvey on 4/20/16.
 */
public class HighScoreThread extends Thread {
    private GameWindow gameWindowHandle;
    private boolean running;
    private UpdateMethod updateMethod;
    private String localUser;


    HighScoreThread(GameWindow gameWindow, boolean playWithFriends) {
        super();
        gameWindowHandle = gameWindow;
        running = true;
        localUser = DatabaseOperations.getLocalLoggedInUser();
        setUpdateMethod(playWithFriends);
    }

    @Override
    public void run() {
        while (running) {
            updateMethod.execute();
        }
    }

    /**
     * Updates the running state of this thread.
     *
     * @param running true if the thread is running, otherwise false.
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * A prototype for the database update method.
     */
    public interface UpdateMethod {
        public void execute();
    }

    /**
     * Set the desired update method.  If we are playing with friends we must update
     * the gameWindows's friend score and friend name attributes; otherwise we just
     * update the user's highScore.
     *
     * @param playWithFriends true if the user is playing with friends; otherwise false.
     */
    private void setUpdateMethod(boolean playWithFriends) {
        if (playWithFriends) {
            updateMethod = new UpdateMethod() {
                public void execute() {
                    try {
                        sleep(3600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DatabaseOperations.setHighScore(localUser, gameWindowHandle.getScore());
                    String[] topFriend = DatabaseOperations.getTopFriend(localUser);

                    gameWindowHandle.setFriendsScore(topFriend[1], Long.valueOf(topFriend[0]));
                }
            };
            } else {
                updateMethod = new UpdateMethod() {
                    public void execute() {
                        try {
                            sleep(3600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        DatabaseOperations.setHighScore(localUser, gameWindowHandle.getScore());
                    }
                };
            }
    }

}
