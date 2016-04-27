package storage.shapes;

interface RemoteDbOperations {
    long getBlockSeed(String username);
    long getDailyChallengeSeed();
    void setBlockSeed(String username, long seed);
    boolean getLoginStatus(String username);
    String getTopFriend(String username);
    boolean findUser(String username);
    String[] searchUser(String subString);
}
