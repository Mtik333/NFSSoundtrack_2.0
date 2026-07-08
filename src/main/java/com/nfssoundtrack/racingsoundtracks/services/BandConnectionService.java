package com.nfssoundtrack.racingsoundtracks.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.others.BandConnectionResult;
import com.nfssoundtrack.racingsoundtracks.repository.BandConnectionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BandConnectionService {

    private static final Logger logger = LoggerFactory.getLogger(BandConnectionService.class);
    private static final int MAX_PATH_ENUM = 100_000;

    private final BandConnectionRepository repository;

    @Value("${bandconnection.datafile:band_graph.json}")
    private String dataFilePath;

    // Immutable snapshots swapped in atomically on (re)build
    private volatile Map<Long, String> bandNames = Collections.emptyMap();
    private volatile Map<Long, String> gameNames = Collections.emptyMap();
    private volatile Map<Long, String> gameShorts = Collections.emptyMap();

    // Game-level graph: bandId → neighbors / bandId → neighbor → shared gameIds
    private volatile Map<Long, Set<Long>> adjacency = Collections.emptyMap();
    private volatile Map<Long, Map<Long, Set<Long>>> edgeGames = Collections.emptyMap();

    // Strict (same-maingroup) graph: bandId → neighbors / bandId → neighbor → shared maingroupIds
    private volatile Map<Long, Set<Long>> adjacencyStrict = Collections.emptyMap();
    private volatile Map<Long, Map<Long, Set<Long>>> edgeMaingroups = Collections.emptyMap();

    // Maingroup metadata for strict-mode display
    private volatile Map<Long, Long> maingroupToGame = Collections.emptyMap();   // maingroupId → gameId
    private volatile Map<Long, String> maingroupNames = Collections.emptyMap();  // maingroupId → groupname

    private volatile boolean graphBuilt = false;
    private final Object buildLock = new Object();

    public BandConnectionService(BandConnectionRepository repository) {
        this.repository = repository;
    }

    private void ensureGraphBuilt() {
        if (!graphBuilt) {
            synchronized (buildLock) {
                if (!graphBuilt) {
                    if (!tryLoadFromFile()) {
                        buildGraphFromDb();
                    }
                }
            }
        }
    }

    public void rebuildGraph() {
        synchronized (buildLock) {
            graphBuilt = false;
            buildGraphFromDb();
        }
    }

    // --- File persistence ---

    private boolean tryLoadFromFile() {
        File file = new File(dataFilePath);
        if (!file.exists()) {
            logger.info("No graph snapshot at {}, will build from DB", dataFilePath);
            return false;
        }
        try {
            long start = System.currentTimeMillis();
            logger.info("Loading band connection graph from {}", dataFilePath);

            ObjectMapper mapper = new ObjectMapper();
            GraphSnapshot snapshot = mapper.readValue(file, GraphSnapshot.class);

            if (snapshot.bandToMaingroups == null || snapshot.maingroupToGame == null) {
                logger.info("Graph snapshot is missing strict-mode data — rebuilding from DB");
                return false;
            }

            assembleGraph(snapshot.bandNames, snapshot.gameNames, snapshot.gameShorts,
                    snapshot.bandToGames, snapshot.bandToMaingroups,
                    snapshot.maingroupToGame, snapshot.maingroupNames);

            logger.info("Graph loaded from file: {} bands, {} games in {}ms",
                    bandNames.size(), gameNames.size(), System.currentTimeMillis() - start);
            return true;
        } catch (IOException e) {
            logger.warn("Failed to load graph snapshot from {}: {} — falling back to DB", dataFilePath, e.getMessage());
            return false;
        }
    }

    private void saveToFile(Map<Long, String> newBandNames, Map<Long, String> newGameNames,
                            Map<Long, String> newGameShorts, Map<Long, Set<Long>> bandToGames,
                            Map<Long, Set<Long>> bandToMaingroups, Map<Long, Long> newMaingroupToGame,
                            Map<Long, String> newMaingroupNames) {
        try {
            GraphSnapshot snapshot = new GraphSnapshot();
            snapshot.bandNames = newBandNames;
            snapshot.gameNames = newGameNames;
            snapshot.gameShorts = newGameShorts;
            snapshot.bandToGames = bandToGames;
            snapshot.bandToMaingroups = bandToMaingroups;
            snapshot.maingroupToGame = newMaingroupToGame;
            snapshot.maingroupNames = newMaingroupNames;

            File file = new File(dataFilePath);
            new ObjectMapper().writeValue(file, snapshot);
            logger.info("Graph snapshot saved to {} ({} KB)", dataFilePath, file.length() / 1024);
        } catch (IOException e) {
            logger.error("Failed to save graph snapshot to {}: {}", dataFilePath, e.getMessage());
        }
    }

    // --- Graph building ---

    private void buildGraphFromDb() {
        long start = System.currentTimeMillis();
        logger.info("Building band connection graph from DB...");

        Map<Long, String> newBandNames = new HashMap<>();
        Map<Long, String> newGameNames = new HashMap<>();
        Map<Long, String> newGameShorts = new HashMap<>();
        Map<Long, Set<Long>> bandToGames = new HashMap<>();
        Map<Long, Set<Long>> bandToMaingroups = new HashMap<>();
        Map<Long, Long> newMaingroupToGame = new HashMap<>();
        Map<Long, String> newMaingroupNames = new HashMap<>();

        List<Object[]> rows = repository.findAllBandGameEdges();
        for (Object[] row : rows) {
            Long bandId = ((Number) row[0]).longValue();
            String bandName = (String) row[1];
            Long gameId = ((Number) row[2]).longValue();
            String gameTitle = (String) row[3];
            String gameShortVal = (String) row[4];
            Long maingroupId = ((Number) row[5]).longValue();
            String maingroupName = (String) row[6];

            newBandNames.put(bandId, bandName);
            newGameNames.put(gameId, gameTitle);
            newGameShorts.put(gameId, gameShortVal);
            bandToGames.computeIfAbsent(bandId, k -> new HashSet<>()).add(gameId);

            bandToMaingroups.computeIfAbsent(bandId, k -> new HashSet<>()).add(maingroupId);
            newMaingroupToGame.put(maingroupId, gameId);
            newMaingroupNames.put(maingroupId, maingroupName);
        }

        assembleGraph(newBandNames, newGameNames, newGameShorts, bandToGames,
                bandToMaingroups, newMaingroupToGame, newMaingroupNames);
        saveToFile(newBandNames, newGameNames, newGameShorts, bandToGames,
                bandToMaingroups, newMaingroupToGame, newMaingroupNames);

        logger.info("Graph built from DB in {}ms", System.currentTimeMillis() - start);
    }

    private void assembleGraph(Map<Long, String> newBandNames, Map<Long, String> newGameNames,
                               Map<Long, String> newGameShorts, Map<Long, Set<Long>> bandToGames,
                               Map<Long, Set<Long>> bandToMaingroups, Map<Long, Long> newMaingroupToGame,
                               Map<Long, String> newMaingroupNames) {
        // --- Game-level graph ---
        Map<Long, Set<Long>> gameToBands = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : bandToGames.entrySet()) {
            Long bandId = entry.getKey();
            for (Long gameId : entry.getValue()) {
                gameToBands.computeIfAbsent(gameId, k -> new HashSet<>()).add(bandId);
            }
        }

        Map<Long, Set<Long>> newAdjacency = new HashMap<>();
        Map<Long, Map<Long, Set<Long>>> newEdgeGames = new HashMap<>();

        for (Map.Entry<Long, Set<Long>> entry : gameToBands.entrySet()) {
            Long gameId = entry.getKey();
            Long[] bands = entry.getValue().toArray(new Long[0]);
            for (int i = 0; i < bands.length; i++) {
                for (int j = i + 1; j < bands.length; j++) {
                    Long a = bands[i];
                    Long b = bands[j];
                    newAdjacency.computeIfAbsent(a, k -> new HashSet<>()).add(b);
                    newAdjacency.computeIfAbsent(b, k -> new HashSet<>()).add(a);
                    newEdgeGames.computeIfAbsent(a, k -> new HashMap<>())
                            .computeIfAbsent(b, k -> new HashSet<>()).add(gameId);
                    newEdgeGames.computeIfAbsent(b, k -> new HashMap<>())
                            .computeIfAbsent(a, k -> new HashSet<>()).add(gameId);
                }
            }
        }

        // --- Strict (same-maingroup) graph ---
        Map<Long, Set<Long>> maingroupToBands = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : bandToMaingroups.entrySet()) {
            Long bandId = entry.getKey();
            for (Long mgId : entry.getValue()) {
                maingroupToBands.computeIfAbsent(mgId, k -> new HashSet<>()).add(bandId);
            }
        }

        Map<Long, Set<Long>> newAdjacencyStrict = new HashMap<>();
        Map<Long, Map<Long, Set<Long>>> newEdgeMaingroups = new HashMap<>();

        for (Map.Entry<Long, Set<Long>> entry : maingroupToBands.entrySet()) {
            Long mgId = entry.getKey();
            Long[] bands = entry.getValue().toArray(new Long[0]);
            for (int i = 0; i < bands.length; i++) {
                for (int j = i + 1; j < bands.length; j++) {
                    Long a = bands[i];
                    Long b = bands[j];
                    newAdjacencyStrict.computeIfAbsent(a, k -> new HashSet<>()).add(b);
                    newAdjacencyStrict.computeIfAbsent(b, k -> new HashSet<>()).add(a);
                    newEdgeMaingroups.computeIfAbsent(a, k -> new HashMap<>())
                            .computeIfAbsent(b, k -> new HashSet<>()).add(mgId);
                    newEdgeMaingroups.computeIfAbsent(b, k -> new HashMap<>())
                            .computeIfAbsent(a, k -> new HashSet<>()).add(mgId);
                }
            }
        }

        // Atomic swap
        this.bandNames = Collections.unmodifiableMap(newBandNames);
        this.gameNames = Collections.unmodifiableMap(newGameNames);
        this.gameShorts = Collections.unmodifiableMap(newGameShorts);
        this.adjacency = Collections.unmodifiableMap(newAdjacency);
        this.edgeGames = Collections.unmodifiableMap(newEdgeGames);
        this.adjacencyStrict = Collections.unmodifiableMap(newAdjacencyStrict);
        this.edgeMaingroups = Collections.unmodifiableMap(newEdgeMaingroups);
        this.maingroupToGame = Collections.unmodifiableMap(newMaingroupToGame);
        this.maingroupNames = Collections.unmodifiableMap(newMaingroupNames);
        this.graphBuilt = true;

        long gameEdges = newEdgeGames.values().stream().mapToLong(m -> m.size()).sum() / 2;
        long strictEdges = newEdgeMaingroups.values().stream().mapToLong(m -> m.size()).sum() / 2;
        logger.info("Graph assembled: {} bands, {} games, {} game-level edges, {} strict edges",
                newBandNames.size(), newGameNames.size(), gameEdges, strictEdges);
    }

    // --- BFS / path finding ---

    public BandConnectionResult findConnection(Long fromId, Long toId, boolean sameGroup) {
        ensureGraphBuilt();

        if (!bandNames.containsKey(fromId)) {
            return BandConnectionResult.notFound("Artist with id " + fromId + " not found in the graph");
        }
        if (!bandNames.containsKey(toId)) {
            return BandConnectionResult.notFound("Artist with id " + toId + " not found in the graph");
        }
        if (fromId.equals(toId)) {
            return new BandConnectionResult(true, null, 0,
                    List.of(new BandConnectionResult.PathNode(fromId, bandNames.get(fromId), null)));
        }

        Map<Long, Set<Long>> adj = sameGroup ? adjacencyStrict : adjacency;
        Map<Long, Map<Long, Set<Long>>> edges = sameGroup ? edgeMaingroups : edgeGames;

        // BFS: track shortest distances and ALL parents at that distance
        Map<Long, Integer> dist = new HashMap<>();
        Map<Long, List<Long>> parents = new HashMap<>();
        Queue<Long> queue = new LinkedList<>();

        dist.put(fromId, 0);
        parents.put(fromId, Collections.emptyList());
        queue.add(fromId);

        boolean found = false;
        int targetDist = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            int currentDist = dist.get(current);
            if (currentDist >= targetDist) continue;

            for (Long neighbor : adj.getOrDefault(current, Collections.emptySet())) {
                if (!dist.containsKey(neighbor)) {
                    dist.put(neighbor, currentDist + 1);
                    List<Long> pList = new ArrayList<>();
                    pList.add(current);
                    parents.put(neighbor, pList);
                    queue.add(neighbor);
                    if (neighbor.equals(toId)) {
                        found = true;
                        targetDist = currentDist + 1;
                    }
                } else if (dist.get(neighbor) == currentDist + 1) {
                    parents.get(neighbor).add(current);
                }
            }
        }

        if (!found) {
            return BandConnectionResult.noConnection();
        }

        // DFS through parents: find the shortest path with the highest total edge weight
        int[] bestWeight = {-1};
        List<Long> bestPath = new ArrayList<>();
        List<Long> currentPath = new ArrayList<>();
        currentPath.add(toId);

        findBestPath(toId, fromId, parents, currentPath, 0, bestWeight, bestPath, new int[]{0}, edges);

        Collections.reverse(bestPath);

        // Assemble PathNode list
        List<BandConnectionResult.PathNode> pathNodes = new ArrayList<>();
        pathNodes.add(new BandConnectionResult.PathNode(bestPath.get(0), bandNames.get(bestPath.get(0)), null));

        for (int i = 1; i < bestPath.size(); i++) {
            Long prev = bestPath.get(i - 1);
            Long curr = bestPath.get(i);
            Set<Long> sharedIds = edges.getOrDefault(prev, Collections.emptyMap())
                    .getOrDefault(curr, Collections.emptySet());

            List<BandConnectionResult.GameRef> games;
            if (sameGroup) {
                // sharedIds are maingroupIds
                games = sharedIds.stream()
                        .map(mgId -> {
                            Long gId = maingroupToGame.get(mgId);
                            return new BandConnectionResult.GameRef(
                                    gId, gameNames.get(gId), gameShorts.get(gId), maingroupNames.get(mgId));
                        })
                        .sorted(Comparator.comparing(BandConnectionResult.GameRef::getTitle))
                        .collect(Collectors.toList());
            } else {
                // sharedIds are gameIds
                games = sharedIds.stream()
                        .map(gId -> new BandConnectionResult.GameRef(gId, gameNames.get(gId), gameShorts.get(gId)))
                        .sorted(Comparator.comparing(BandConnectionResult.GameRef::getTitle))
                        .collect(Collectors.toList());
            }
            pathNodes.add(new BandConnectionResult.PathNode(curr, bandNames.get(curr), games));
        }

        return new BandConnectionResult(true, null, bestPath.size() - 1, pathNodes);
    }

    private void findBestPath(Long node, Long target, Map<Long, List<Long>> parents,
                              List<Long> currentPath, int currentWeight,
                              int[] bestWeight, List<Long> bestPath, int[] enumCount,
                              Map<Long, Map<Long, Set<Long>>> edges) {
        if (enumCount[0] >= MAX_PATH_ENUM) return;

        if (node.equals(target)) {
            enumCount[0]++;
            if (currentWeight > bestWeight[0]) {
                bestWeight[0] = currentWeight;
                bestPath.clear();
                bestPath.addAll(currentPath);
            }
            return;
        }

        List<Long> nodeParents = parents.get(node);
        if (nodeParents == null) return;

        for (Long parent : nodeParents) {
            int edgeWeight = edges.getOrDefault(parent, Collections.emptyMap())
                    .getOrDefault(node, Collections.emptySet()).size();
            currentPath.add(parent);
            findBestPath(parent, target, parents, currentPath, currentWeight + edgeWeight,
                    bestWeight, bestPath, enumCount, edges);
            currentPath.remove(currentPath.size() - 1);
        }
    }

    // --- Admin / status ---

    public List<Map<String, Object>> searchBands(String query, int limit) {
        ensureGraphBuilt();
        String lower = query.toLowerCase();
        return bandNames.entrySet().stream()
                .filter(e -> e.getValue().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .limit(limit)
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", e.getKey());
                    item.put("name", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    public boolean isGraphBuilt() {
        return graphBuilt;
    }

    public int getBandCount() {
        ensureGraphBuilt();
        return bandNames.size();
    }

    // --- JSON snapshot POJO ---

    public static class GraphSnapshot {
        public Map<Long, String> bandNames;
        public Map<Long, String> gameNames;
        public Map<Long, String> gameShorts;
        public Map<Long, Set<Long>> bandToGames;
        // strict-mode additions
        public Map<Long, Set<Long>> bandToMaingroups;
        public Map<Long, Long> maingroupToGame;
        public Map<Long, String> maingroupNames;
    }
}
