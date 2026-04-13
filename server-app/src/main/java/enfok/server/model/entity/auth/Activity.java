package enfok.server.model.entity.auth;

public class Activity {
    private int totalImagesProcessed;
    private int totalBatches;
    private String lastActivity;

    public int getTotalImagesProcessed() { return totalImagesProcessed; }
    public void setTotalImagesProcessed(int totalImagesProcessed) { this.totalImagesProcessed = totalImagesProcessed; }
    public int getTotalBatches() { return totalBatches; }
    public void setTotalBatches(int totalBatches) { this.totalBatches = totalBatches; }
    public String getLastActivity() { return lastActivity; }
    public void setLastActivity(String lastActivity) { this.lastActivity = lastActivity; }
}
