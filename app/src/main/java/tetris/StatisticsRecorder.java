package tetris;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsRecorder {

  private final String filePath;

  private int roundNumber = 0;
  private int currentScore = 0;
  private final Map<BlockPieces, Integer> pieceCounts = new LinkedHashMap<>();

  // Summaries of all completed rounds, written out together each time.
  private final List<String> roundSummaries = new ArrayList<>();

  public StatisticsRecorder(String filePath) {
    this.filePath = filePath;
    resetCounts();
  }

  public void recordPiece(BlockPieces piece) {
    pieceCounts.merge(piece, 1, Integer::sum);
  }

  public void finaliseRound(int score) {
    roundNumber++;
    currentScore = score;
    roundSummaries.add(buildRoundSummary());
    writeToFile();
  }

  public void startNewRound() {
    currentScore = 0;
    resetCounts();
  }

  // ── Private helpers ──────────────────────────────────────────────────────

  private void resetCounts() {
    pieceCounts.clear();
    for (BlockPieces piece : BlockPieces.values())
      pieceCounts.put(piece, 0);
  }

  private String buildRoundSummary() {
    StringBuilder sb = new StringBuilder();
    sb.append("Round #").append(roundNumber).append("\n"); // add #
    sb.append("Score: ").append(currentScore).append("\n");
    for (Map.Entry<BlockPieces, Integer> entry : pieceCounts.entrySet())
      sb.append(entry.getKey().getBlockName())
          .append(": ").append(entry.getValue()).append("\n");
    sb.append("-----"); // add separator
    return sb.toString();

  }

  private void writeToFile() {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
      for (String summary : roundSummaries)
        writer.println(summary);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write statistics file: " + filePath, e);
    }
  }
}
