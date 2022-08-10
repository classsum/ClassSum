public class DiskBalancerWorkStatus {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final ObjectMapper MAPPER_WITH_INDENT_OUTPUT =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  private static final ObjectReader READER_WORKSTATUS =
      new ObjectMapper().readerFor(DiskBalancerWorkStatus.class);
  private static final ObjectReader READER_WORKENTRY = new ObjectMapper()
      .readerFor(defaultInstance().constructCollectionType(List.class,
          DiskBalancerWorkEntry.class));

  private final List<DiskBalancerWorkEntry> currentState;
  private Result result;
  private String planID;
  private String planFile;

  public DiskBalancerWorkStatus() {
    this.currentState = new LinkedList<>();
  }

  public DiskBalancerWorkStatus(Result result, String planID, String planFile) {
    this();
    this.result = result;
    this.planID = planID;
    this.planFile = planFile;
  }

  public DiskBalancerWorkStatus(Result result, String planID,
                                List<DiskBalancerWorkEntry> currentState) {
    this.result = result;
    this.planID = planID;
    this.currentState = currentState;
  }

  public DiskBalancerWorkStatus(Result result, String planID, String planFile,
                                String currentState) throws IOException {
    this.result = result;
    this.planID = planID;
    this.planFile = planFile;
    this.currentState = READER_WORKENTRY.readValue(currentState);
  }

  public Result getResult() {
    return result;
  }

  public String getPlanID() {
    return planID;
  }

  public String getPlanFile() {
    return planFile;
  }

  public List<DiskBalancerWorkEntry> getCurrentState() {
    return currentState;
  }

  public String currentStateString() throws IOException {
    return MAPPER_WITH_INDENT_OUTPUT.writeValueAsString(currentState);
  }

  public String toJsonString() throws IOException {
    return MAPPER.writeValueAsString(this);
  }


  public static DiskBalancerWorkStatus parseJson(String json) throws
      IOException {
    return READER_WORKSTATUS.readValue(json);
  }

  public void addWorkEntry(DiskBalancerWorkEntry entry) {
    Preconditions.checkNotNull(entry);
    currentState.add(entry);
  }

  public enum Result {
    NO_PLAN(0),
    PLAN_UNDER_PROGRESS(1),
    PLAN_DONE(2),
    PLAN_CANCELLED(3);
    private int result;

    private Result(int result) {
      this.result = result;
    }

    /**
     * Get int value of result.
     *
     * @return int
     */
    public int getIntResult() {
      return result;
    }
  }

  public static class DiskBalancerWorkEntry {
    private String sourcePath;
    private String destPath;
    private DiskBalancerWorkItem workItem;

    public DiskBalancerWorkEntry() {
    }

    public DiskBalancerWorkEntry(String workItem) throws IOException {
      this.workItem = DiskBalancerWorkItem.parseJson(workItem);
    }

    public DiskBalancerWorkEntry(String sourcePath, String destPath,
                                 DiskBalancerWorkItem workItem) {
      this.sourcePath = sourcePath;
      this.destPath = destPath;
      this.workItem = workItem;
    }

    public String getSourcePath() {
      return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
      this.sourcePath = sourcePath;
    }

    public String getDestPath() {
      return destPath;
    }

    public void setDestPath(String destPath) {
      this.destPath = destPath;
    }

    public DiskBalancerWorkItem getWorkItem() {
      return workItem;
    }

    public void setWorkItem(DiskBalancerWorkItem workItem) {
      this.workItem = workItem;
    }
  }
}