package com.nfssoundtrack.racingsoundtracks.others;

import java.util.List;

public class BandConnectionResult {

    private final boolean found;
    private final String message;
    private final int hops;
    private final List<PathNode> path;

    public BandConnectionResult(boolean found, String message, int hops, List<PathNode> path) {
        this.found = found;
        this.message = message;
        this.hops = hops;
        this.path = path;
    }

    public static BandConnectionResult noConnection() {
        return new BandConnectionResult(false, "No connection found between these two artists", 0, null);
    }

    public static BandConnectionResult notFound(String msg) {
        return new BandConnectionResult(false, msg, 0, null);
    }

    public boolean isFound() { return found; }
    public String getMessage() { return message; }
    public int getHops() { return hops; }
    public List<PathNode> getPath() { return path; }

    public static class PathNode {
        private final long bandId;
        private final String bandName;
        private final List<GameRef> viaGames;

        public PathNode(long bandId, String bandName, List<GameRef> viaGames) {
            this.bandId = bandId;
            this.bandName = bandName;
            this.viaGames = viaGames;
        }

        public long getBandId() { return bandId; }
        public String getBandName() { return bandName; }
        public List<GameRef> getViaGames() { return viaGames; }
    }

    public static class GameRef {
        private final long id;
        private final String title;
        private final String gameShort;
        private final String maingroupName; // non-null only in strict (same-group) mode

        public GameRef(long id, String title, String gameShort) {
            this(id, title, gameShort, null);
        }

        public GameRef(long id, String title, String gameShort, String maingroupName) {
            this.id = id;
            this.title = title;
            this.gameShort = gameShort;
            this.maingroupName = maingroupName;
        }

        public long getId() { return id; }
        public String getTitle() { return title; }
        public String getGameShort() { return gameShort; }
        public String getMaingroupName() { return maingroupName; }
    }
}
