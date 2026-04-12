package enfok.server.model.entity.bd;

import java.sql.Timestamp;

public class Image {
    private int id;
    private int batchId;
    private String originalName;
    private String resultPath;
    private int statusId;
    private int nodeId;
    private Timestamp receptionTime;
    private Timestamp conversionTime;
    private byte[] data; // Arreglo de bytes para guardar/enviar la imagen pura

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBatchId() { return batchId; }
    public void setBatchId(int batchId) { this.batchId = batchId; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getResultPath() { return resultPath; }
    public void setResultPath(String resultPath) { this.resultPath = resultPath; }
    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }
    public int getNodeId() { return nodeId; }
    public void setNodeId(int nodeId) { this.nodeId = nodeId; }
    public Timestamp getReceptionTime() { return receptionTime; }
    public void setReceptionTime(Timestamp receptionTime) { this.receptionTime = receptionTime; }
    public Timestamp getConversionTime() { return conversionTime; }
    public void setConversionTime(Timestamp conversionTime) { this.conversionTime = conversionTime; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
}
